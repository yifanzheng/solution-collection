package top.yifan.masterha.core;

import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.Lease;
import io.etcd.jetcd.Lock;
import io.etcd.jetcd.lease.LeaseKeepAliveResponse;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.support.CloseableClient;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import top.yifan.masterha.client.EtcdTemplate;
import top.yifan.masterha.node.MasterNodeHelper;
import top.yifan.masterha.node.NodeMode;
import top.yifan.masterha.util.ByteSequenceUtil;
import top.yifan.masterha.util.ThreadUtil;

import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * MasterHACore
 *
 * @author star
 */
@Component
public class MasterHAEngine implements CommandLineRunner, DisposableBean {

    private static final Logger log = LoggerFactory.getLogger(MasterHAEngine.class);

    private static final String ACTIVE_LOCK_KEY = "/node/master/active/lock";
    private static final String ACTIVE_INFO_KEY = "/node/master/active/info";
    private static final long LEASE_TTL = 10; // 租约期限 10s

    private final ExecutorService executorService;
    private final AtomicInteger taskNum = new AtomicInteger(0);
    private CloseableClient keepAliveClient;
    private HAState haState;

    public MasterHAEngine() {
        this.executorService = new ThreadPoolExecutor(1, 2, 30, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(),
                r -> new Thread(r, "MasterHA-Thread:[" + taskNum.getAndIncrement() + "]"));
    }

    @Value("${server.mode}")
    private NodeMode nodeMode;

    @Autowired
    private EtcdTemplate etcdTemplate;

    @Override
    public void run(String... args) throws Exception {
        tryToBeMasterAsync();
    }

    private void tryToBeMasterAsync() {
        log.info("try to be master in node mode: {}", nodeMode);
        // 判断是否是集群模式，默认是 Standalone
        if (!Objects.equals(NodeMode.CLUSTER, nodeMode)) {
            return;
        }
        executorService.submit(() -> {
            try {
                tryToBeMaster();
            } catch (ExecutionException e) {
                log.error("Failed to register active master, and try to be register after 3s.", e);
                ThreadUtil.sleep(3000);
                tryToBeMasterAsync();
            } catch (InterruptedException e) {
                log.error("Failed to register active master.", e);
                Thread.currentThread().interrupt();
            }
        });
    }

    private void tryToBeMaster() throws ExecutionException, InterruptedException {
        Client etcdClient = etcdTemplate.getClient();
        Lock lock = etcdClient.getLockClient();
        Lease lease = etcdClient.getLeaseClient();
        // 获取租约ID
        long leaseId = lease.grant(LEASE_TTL).get().getID();
        // 调用keepAlive方法，使授予到的leaseId保活
        keepAliveClient = lease.keepAlive(leaseId, new StreamObserver<LeaseKeepAliveResponse>() {
            @Override
            public void onNext(LeaseKeepAliveResponse value) {
                System.err.println("LeaseKeepAliveResponse value:" + value.getTTL());
            }

            @Override
            public void onError(Throwable t) {
                toStandby();
                tryToBeMasterAsync();
            }

            @Override
            public void onCompleted() {

            }
        });
        // 进行租约锁定
        lock.lock(ByteSequenceUtil.fromString(etcdTemplate.fullPath(ACTIVE_LOCK_KEY)), leaseId).get().getKey();
        toActive();

        // 使用 KV 客户端注册主节点信息
        KV kvClient = etcdClient.getKVClient();
        PutOption putOption = PutOption.newBuilder().withLeaseId(leaseId).build();
        kvClient.put(ByteSequenceUtil.fromString(etcdTemplate.fullPath(ACTIVE_INFO_KEY)),
                ByteSequenceUtil.fromString(MasterNodeHelper.getNodeSummaryJSON()),
                putOption).get();
    }

    private void toActive() {
        this.haState = HAState.ACTIVE;
        MasterNodeHelper.flushNodeHAState(haState);
    }

    private void toStandby() {
        this.haState = HAState.STANDBY;
        MasterNodeHelper.flushNodeHAState(haState);
    }


    @Override
    public void destroy() throws Exception {
        this.executorService.shutdown();
        if (keepAliveClient != null) {
            keepAliveClient.close();
        }
    }

}
