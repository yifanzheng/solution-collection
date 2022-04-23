package top.yifan.masterha.client;

import io.etcd.jetcd.Client;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

/**
 * EtcdClient
 *
 * @author star
 */
class EtcdClient implements DisposableBean {

    private static final Logger log = LoggerFactory.getLogger(EtcdClient.class);

    private final String endpoints;
    private Client client;

    /**
     * etcd 客户端是否启动
     */
    private volatile boolean isActive = false;

    public EtcdClient(String endpoints) {
        this.endpoints = endpoints;
    }

    public Client getClient() {
        createClientIfAbsent();
        return client;
    }

    private void createClientIfAbsent() {
        if (client != null && isActive) {
            return;
        }
        synchronized (this) {
            if (client != null && isActive) {
                return;
            }
            if (StringUtils.isBlank(endpoints)) {
                throw new IllegalArgumentException("ETCD endpoints can't be empty");
            }
            String[] endpointList = endpoints.split(",");
            client = Client.builder().endpoints(endpointList).build();
            isActive = true;
            log.info("Connection ETCD, endpoints: {}", endpoints);
        }
    }


    @Override
    public void destroy() throws Exception {
        if (client != null) {
            client.close();
            isActive = false;
            log.info("[APP Destroy] Closed ETCD Client");
        }
    }
}
