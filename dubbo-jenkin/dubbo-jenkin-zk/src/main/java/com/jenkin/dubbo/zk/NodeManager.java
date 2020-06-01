package com.jenkin.dubbo.zk;

import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;

public class NodeManager {

    public static void main(String[] args) {
        NodeManager nodeManager = new NodeManager();
        nodeManager.createNode();
        nodeManager.updateNode();
        nodeManager.deleteNode();
    }

    public void createNode() {
        ZkClient zkClient = CreateSession.connectZK();
        User user = new User();
        user.setId(1);
        user.setName("jenkin1");

        String path = zkClient.create("/jenkinNode", user, CreateMode.PERSISTENT);
        System.out.println("createNode: "+path);
    }

    public void updateNode() {
        ZkClient zkClient = CreateSession.connectZK();
        User user = new User();
        user.setId(2);
        user.setName("jenkin2");

        zkClient.writeData("/jenkinNode", user);
        System.out.println("updateNode Success ");
    }

    public void deleteNode() {
        ZkClient zkClient = CreateSession.connectZK();
        boolean delete = zkClient.delete("/jenkinNode");
        System.out.println("deleteNode: "+delete);
    }
}
