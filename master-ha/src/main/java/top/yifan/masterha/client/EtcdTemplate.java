package top.yifan.masterha.client;

import io.etcd.jetcd.Client;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
