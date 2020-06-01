package com.jenkin.dubbo.zk;

import org.I0Itec.zkclient.IZkStateListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;
import org.apache.zookeeper.Watcher;

public class CreateSession {

    public static ZkClient connectZK() {
        // zk集群的地址
        String ZKServers = "127.0.0.1:2181";

        ZkClient zkClient = new ZkClient(ZKServers, 10000, 10000, new SerializableSerializer());
        zkClient.subscribeStateChanges(new IZkStateListener() {
            @Override
            public void handleStateChanged(Watcher.Event.KeeperState keeperState) throws Exception {
                System.out.println("handleStateChanged, status: "+keeperState);
            }

            @Override
            public void handleNewSession() throws Exception {
                System.out.println("handleNewSession");
            }
        });
        System.out.println("connected ok!");
        return zkClient;
    }
}
