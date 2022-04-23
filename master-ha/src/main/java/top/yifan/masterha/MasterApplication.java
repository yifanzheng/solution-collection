package top.yifan.masterha;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import java.net.InetAddress;

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

        initializeNodeSummary(protocol, hostAddress, port);
    }

    private static void initializeNodeSummary(String protocol, String hostAddress, String port) {


    }

}
