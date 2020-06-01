package com.jenkin.dubbo.zk;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;

public class SubscribeDataChanges {

    public static void main(String[] args) throws InterruptedException {
        ZkClient zkClient = CreateSession.connectZK();
        zkClient.subscribeDataChanges("/jenkinNode", new ZKDataListener());
        Thread.sleep(Integer.MAX_VALUE);
    }

    private static class ZKDataListener implements IZkDataListener {

        @Override
        public void handleDataChange(String dataPath, Object data) throws Exception {
            System.out.println("订阅节点的数据内容的变化"+dataPath + ":" + data.toString());
        }

        @Override
        public void handleDataDeleted(String dataPath) throws Exception {
            System.out.println("订阅节点的数据内容被删除"+dataPath);
        }
    }
}
