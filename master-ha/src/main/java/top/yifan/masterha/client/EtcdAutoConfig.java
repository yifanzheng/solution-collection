package top.yifan.masterha.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * EtcdClientAutoConfig
 *
 * @author star
 */
@Configuration
public class EtcdAutoConfig {

    @Value("${etcd.endpoints}")
    private String endpoints;

    @Value("${etcd.parent-dir}")
    private String parentDir;

    @Bean
    @ConditionalOnMissingBean(EtcdClient.class)
    public EtcdClient etcdClient() {
        return new EtcdClient(endpoints);
    }

    @Bean
    @ConditionalOnMissingBean(EtcdTemplate.class)
    public EtcdTemplate etcdTemplate(EtcdClient etcdClient) {
        return new EtcdTemplate(etcdClient, parentDir);
    }
}
