package top.yifan.masterha.watcher;

import com.alibaba.fastjson2.JSON;
import io.etcd.jetcd.Watch;
import io.etcd.jetcd.watch.WatchEvent;
import io.etcd.jetcd.watch.WatchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import top.yifan.masterha.client.EtcdTemplate;

import java.util.List;

/**
 * MasterWatcher
 *
 * @author star
 */
@Component
public class MasterWatcher implements CommandLineRunner {

    private static final String TEST_NODE = "/test";

    @Autowired
    private EtcdTemplate etcdTemplate;

    @Override
    public void run(String... args) throws Exception {
         this.onWatch();
    }

    public void onWatch() {
        Watch.Listener listener = new Watch.Listener() {
            @Override
            public void onNext(WatchResponse watchResponse) {
                List<WatchEvent> events = watchResponse.getEvents();
                events.forEach(e -> {
                    System.out.println(JSON.toJSONString(e));
                });
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {

            }
        };
        etcdTemplate.watchForPrefix("/node/master/active/info", listener);
    }
}
