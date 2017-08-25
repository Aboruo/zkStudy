package com.aboruo.zkstudy.chapter05;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher.Event.KeeperState;

/**
 * 类名称：ZkConUseSimple
 * 类描述：zookeeperJava客户端-使用zookeeper简单的构造方法连接zookeeper
 * @author aboruo
 * @date 2017年8月25日 下午12:09:43
 */
public class ZkConUseSimple implements Watcher {
	private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
	public static void main(String[] args) throws IOException {
		ZooKeeper zooKeeper = new ZooKeeper("172.17.0.2:2181", 5000, new ZkConUseSimple());
		System.out.println(zooKeeper.getState());
		try {
			connectedSemaphore.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Zookeeper session established.");
	}

	@Override
	public void process(WatchedEvent event) {
		System.out.println("Receive watched event:" + event);
		if(KeeperState.SyncConnected == event.getState()) {
			connectedSemaphore.countDown();
		}
	}

}
