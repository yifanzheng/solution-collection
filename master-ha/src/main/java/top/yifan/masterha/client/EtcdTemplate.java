package top.yifan.masterha.client;

import io.etcd.jetcd.Client;
import io.etcd.jetcd.Watch;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.options.WatchOption;
import org.apache.commons.lang3.StringUtils;
import top.yifan.masterha.util.ByteSequenceUtil;

import java.util.concurrent.ExecutionException;

/**
 * @author star
 */
public class EtcdTemplate {

    private final String parentDir;
    private final EtcdClient etcdClient;

    public EtcdTemplate(EtcdClient etcdClient, String parentDir) {
        this.parentDir = parentDir;
        this.etcdClient = etcdClient;
    }

    public Client getClient() {
        return etcdClient.getClient();
    }

    public String getParentPath() {
        return parentDir;
    }

    /**
     * 创建节点
     *
     * @param key   - 键
     * @param value - 值
     * @param ttl   - 超时时间，单位秒
     * @return 返回租约ID
     */
    public long createNode(String key, String value, int ttl) {
        Client client = etcdClient.getClient();
        try {
            long leaseId = client.getLeaseClient().grant(ttl).get().getID();
            PutOption putOption = PutOption.newBuilder().withLeaseId(leaseId).build();
            client.getKVClient().put(
                    ByteSequenceUtil.fromString(fullPath(key)),
                    ByteSequenceUtil.fromString(value),
                    putOption
            ).get();
            return leaseId;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Create ETCD node[" + key + "] error", e);
        }
    }

    /**
     * 监听指定前缀的节点
     *
     * @param prefix   前缀地址
     * @param listener 监听器
     */
    public void watchForPrefix(String prefix, Watch.Listener listener) {
        Client client = etcdClient.getClient();
        WatchOption watchOption = WatchOption.newBuilder()
                .withPrefix(ByteSequenceUtil.fromString(fullPath(prefix)))
                .build();
        Watch watchClient = client.getWatchClient();
        watchClient.watch(ByteSequenceUtil.fromString(fullPath(prefix)),
                watchOption, listener);
    }


    public String fullPath(String key) {
        if (StringUtils.isEmpty(parentDir)) {
            return key;
        }
        if (parentDir.endsWith("/") && key.startsWith("/")) {
            return parentDir + key.substring(1);
        }
        if (!parentDir.endsWith("/") && !key.startsWith("/")) {
            return parentDir + "/" + key;
        }
        return parentDir + key;
    }
}
