package com.aboruo.zkstudy.chapter05;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;

/**
 * 类名称：ZkConUseSID_PASSWD
 * 类描述：java客户端-使用sessionId和passwd连接zookeeper服务
 * @author aboruo
 * @date 2017年8月25日 下午12:10:44
 */
public class ZkConUseSID_PASSWD implements Watcher {
	private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
	public static void main(String[] args) throws Exception {
		ZooKeeper zooKeeper = new ZooKeeper("172.17.0.2:2181", 5000, new ZkConUseSID_PASSWD());
		System.out.println(zooKeeper.getState());
		connectedSemaphore.await();
		long sessionId = zooKeeper.getSessionId();
		byte[] passwd = zooKeeper.getSessionPasswd();
		
		/** use illegal sessionId and sessionPasswd */
		zooKeeper = new ZooKeeper("172.17.0.2:2181", 5000, new ZkConUseSID_PASSWD(), 1l, "test".getBytes());
		
		/** use correct sessionid and sessionPasswd */
		zooKeeper = new ZooKeeper("172.17.0.2:2181", 5000, new ZkConUseSID_PASSWD(), sessionId, passwd);
		Thread.sleep(6000);
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
