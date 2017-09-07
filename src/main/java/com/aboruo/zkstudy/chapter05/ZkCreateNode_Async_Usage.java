package com.aboruo.zkstudy.chapter05;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

public class ZkCreateNode_Async_Usage implements Watcher {
	private static CountDownLatch connectedSemaphore = new CountDownLatch(1);

	@Override
	public void process(WatchedEvent event) {
		if (KeeperState.SyncConnected == event.getState()) {
			connectedSemaphore.countDown();
		}
	}

	public static void main(String[] args) {
		try {
			ZooKeeper zooKeeper = new ZooKeeper("172.17.0.2:2181", 5000, new ZkCreateNode_Async_Usage());
			connectedSemaphore.await();
			zooKeeper.create("/zk-test-ephemeral-", "".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL,
					new IStringCallBackImpl(), "I am context."); //创建临时节点
			zooKeeper.create("/zk-test-ephemeral-", "".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL,
					new IStringCallBackImpl(), "I am context.");
			zooKeeper.create("/zk-test-ephemeral-", "".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL,
					new IStringCallBackImpl(), "I am context.");//创建临时顺序节点
			Thread.sleep(Integer.MAX_VALUE);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

class IStringCallBackImpl implements AsyncCallback.StringCallback {

	@Override
	public void processResult(int rc, String path, Object ctx, String name) {
		System.out.println("Create path result:[" + rc + ", " + path + ", " + ctx + ", real path name: " + name);
	}
}
