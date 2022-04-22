package top.yifan.masterha.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

/**
 * MasterHACore
 *
 * @author star
 */
@Service
public class MasterHACore implements CommandLineRunner, DisposableBean {

    private static final Logger log = LoggerFactory.getLogger(MasterHACore.class);

    private static final String ACTIVE_LOCK_KEY = "/node/master/active/lock";
    private static final String ACTIVE_INFO_KEY = "/node/master/active/info";
    private static final long lease_ttl = 10; // 租约期限 10s



    @Override
    public void run(String... args) throws Exception {

    }

    @Override
    public void destroy() throws Exception {

    }

}
