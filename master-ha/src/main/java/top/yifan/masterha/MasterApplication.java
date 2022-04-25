package top.yifan.masterha;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import top.yifan.masterha.node.NodeMode;
import top.yifan.masterha.node.MasterNodeHelper;
import top.yifan.masterha.node.MasterNodeSummary;
import top.yifan.masterha.node.NodeState;

import java.net.InetAddress;
import java.time.Instant;

@SpringBootApplication
public class MasterApplication {

    private static final Logger log = LoggerFactory.getLogger(MasterApplication.class);

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(MasterApplication.class);
        Environment env = app.run(args).getEnvironment();
        String protocol = "http";
        if (env.getProperty("server.ssl.key-store") != null) {
            protocol = "https";
        }
        String hostAddress = "localhost";
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            log.warn("The host name could not be determined, using `localhost` as fallback.");
        }
        String port = env.getProperty("server.port");
        log.info("\n----------------------------------------------------------\n\t"
                        + "Application '{}' is running! Access URLs:\n\t"
                        + "Local: \t\t{}://localhost:{}\n\t"
                        + "External: \t{}://{}:{}\n\t"
                        + "Profile(s): \t{}"
                        + "\n----------------------------------------------------------",
                env.getProperty("spring.application.name"),
                protocol,
                port,
                protocol,
                hostAddress,
                port,
                env.getActiveProfiles());

        initializeNodeSummary(env, protocol, hostAddress);
    }

    private static void initializeNodeSummary(Environment env, String protocol, String hostAddress) {
        String endpoint = env.getProperty("server.endpoint");
        if (StringUtils.isBlank(endpoint)) {
            endpoint = String.format("%s://%s:%s", protocol, hostAddress, env.getProperty("server.port"));
        } else {
            endpoint = String.format("%s://%s", protocol, endpoint);
        }
        String nodeMode = env.getProperty("server.mode");
        // 初始化节点信息
        MasterNodeSummary nodeSummary = MasterNodeHelper.getNodeSummary();
        nodeSummary.setState(NodeState.UP);
        nodeSummary.setStartTime(Instant.now());
        nodeSummary.setEndpoint(endpoint);
        if (StringUtils.isNotBlank(nodeMode)) {
            nodeSummary.setMode(NodeMode.valueOf(nodeMode.toUpperCase()));
        }
    }

}
