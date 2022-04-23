package top.yifan.masterha.core;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.Lease;
import io.etcd.jetcd.Lock;
import io.etcd.jetcd.lease.LeaseKeepAliveResponse;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import top.yifan.masterha.client.EtcdTemplate;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;

/**
 * MasterHACore
 *
 * @author star
 */
@Component
public class MasterHACore implements CommandLineRunner, DisposableBean {

    private static final Logger log = LoggerFactory.getLogger(MasterHACore.class);

    private static final String ACTIVE_LOCK_KEY = "/node/master/active/lock";
    private static final String ACTIVE_INFO_KEY = "/node/master/active/info";
    private static final long LEASE_TTL = 10; // 租约期限 10s

    private Client client;
    private HAState haState;

    @Autowired
    private EtcdTemplate etcdTemplate;

    @Override
    public void run(String... args) throws Exception {
        this.client = etcdTemplate.getClient();
        tryActive();
    }

    private void tryActive() throws ExecutionException, InterruptedException {
        Lock lock = client.getLockClient();
        Lease lease = client.getLeaseClient();
        long leaseId = lease.grant(LEASE_TTL).get().getID();
        lease.keepAlive(leaseId, new StreamObserver<LeaseKeepAliveResponse>() {
            @Override
            public void onNext(LeaseKeepAliveResponse value) {
                System.err.println("LeaseKeepAliveResponse value:" + value.getTTL());
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void onCompleted() {
                System.out.println("sss");
            }
        });
        ByteSequence lockKey = ByteSequence.from(etcdTemplate.fullPath(ACTIVE_LOCK_KEY), StandardCharsets.UTF_8);
        lock.lock(lockKey, leaseId).get().getKey();
        System.out.println("sssss");
        //KV kvClient = client.getKVClient();
        //kvClient.put()
    }


    @Override
    public void destroy() throws Exception {
        if (client != null) {
            client.close();
        }
    }

}
