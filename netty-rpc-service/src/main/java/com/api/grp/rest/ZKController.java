package com.api.grp.rest;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.utils.CloseableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/zk")
@ConditionalOnProperty(name = "zk.enabled",havingValue = "true")
public class ZKController implements Closeable{

    @Autowired
    private CuratorFramework curatorFramework;

    private String masterPath;

    @Value("${spring.application.name}")
    private String applicationName;

    private List<LeaderLatch> latchList;
    private List<LeaderSelector> selectors;

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    @PostConstruct
    private void init(){
        latchList = new ArrayList<>();
        selectors =new ArrayList<>();
        masterPath ="/"+applicationName +"/master";

        executor.execute(()->{
            for(int i=0 ;i<5;i++){
                try{
                    LeaderLatch leaderLatch = new LeaderLatch(curatorFramework,masterPath,"client_"+i);
                    leaderLatch.start();
                    latchList.add(leaderLatch);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }


            CustLeaderSelectorListenerAdapter adapter =new CustLeaderSelectorListenerAdapter();
            for(int i= 0;i< 5;i++){
                LeaderSelector selector =new LeaderSelector(curatorFramework,masterPath+"/new",adapter);
                selector.setId("client_"+i);
                selector.autoRequeue();//设置自动重新入选主队列，如果不设置，不会自动再次选主
                selector.start();
                selectors.add(selector);
            }

        });


    }


    @GetMapping("/get")
    public String getLeader() throws Exception{
        for(int i=0;i< latchList.size();i++){
            LeaderLatch leaderLatch = latchList.get(i);
            boolean b = leaderLatch.hasLeadership();
            if(b){
                System.out.println("当前leader:"+leaderLatch.getId());
                leaderLatch.close(LeaderLatch.CloseMode.NOTIFY_LEADER);//放弃leader，静默关闭,放弃以后不会再重新选主
                //关闭后，在zookeeper树下会删除当前节点信息。
                return "success";
            }
        }
        return "success";
    }

    @GetMapping("/get/new")
    public String getLeaderNew() throws Exception{
        for(int i=0;i< selectors.size();i++){
            LeaderSelector selector = selectors.get(i);
            boolean b = selector.hasLeadership();
            if(b){
                System.out.println("当前leader:"+selector.getId());
                return "success";
            }
        }
        return "success";
    }



    @Override
    public void close() throws IOException {
        //在关闭时就不会报错
        for(int i=0;i< latchList.size();i++){
            CloseableUtils.closeQuietly(latchList.get(i));
        }

        //在关闭时就不会报错
        for(int i=0;i< selectors.size();i++){
            CloseableUtils.closeQuietly(selectors.get(i));
        }
        executor.shutdown();
    }
}


class CustLeaderLatchListener implements LeaderLatchListener {
    @Override
    public void isLeader() {

    }

    @Override
    public void notLeader() {

    }
}

class CustLeaderSelectorListenerAdapter extends LeaderSelectorListenerAdapter implements Closeable {

    @Override
    public void close() throws IOException {

    }

    @Override
    public void takeLeadership(CuratorFramework client) throws Exception {
//        System.out.println("切换leader");
        try{
            Thread.sleep(2000);
        }catch (Exception e){}
    }

}