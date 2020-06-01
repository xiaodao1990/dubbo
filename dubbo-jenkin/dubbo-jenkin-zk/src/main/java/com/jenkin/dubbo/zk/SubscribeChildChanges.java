package com.jenkin.dubbo.zk;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;

import java.util.List;

/**
 * 订阅节点的信息改变(创建节点、删除节点、添加子节点)
 */
public class SubscribeChildChanges {

    public static void main(String[] args) throws InterruptedException {
        ZkClient zkClient = CreateSession.connectZK();
        zkClient.subscribeChildChanges("/jenkinNode", new ZKChildListener());
        Thread.sleep(Integer.MAX_VALUE);
    }

    private static class ZKChildListener implements IZkChildListener {

        /**
         * handleChildChange： 用来处理服务器端发送过来的通知
         * @param parentPath 对应的父节点的路径
         * @param currentChilds 子节点的相对路径
         * @throws Exception
         */
        @Override
        public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
            System.out.println("订阅节点的信息改变（创建节点，删除节点，添加子节点）"+parentPath+"   "+currentChilds.toString());
        }
    }
}
