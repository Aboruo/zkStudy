package com.aboruo.zkstudy.chapter05;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class ZkgetData_Api_Sync_Usage implements Watcher {
	private static CountDownLatch connetedSemaphore = new CountDownLatch(1);
	private static ZooKeeper zk = null;
	private static Stat stat = new Stat();

	@Override
	public void process(WatchedEvent event) {
		if (KeeperState.SyncConnected == event.getState()) {
			if (EventType.None == event.getType() && null == event.getPath()) {
				connetedSemaphore.countDown();
			} else if (event.getType() == EventType.NodeDataChanged) {
				try {
					System.out.println(new String(zk.getData(event.getPath(), true, stat)));
					System.out.println(stat.getCzxid() + "," + stat.getMzxid() + "," + stat.getVersion());
				} catch (KeeperException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public static void main(String[] args) {
		try {
			zk = new ZooKeeper("172.17.0.2:2181", 5000, new ZkgetData_Api_Sync_Usage());
			connetedSemaphore.await();
			String path = "/zk-book";
			/** 1.判断节点是否存在，如果存在先删除 */
			stat = zk.exists(path, true);
			if (stat != null)
				zk.delete(path, stat.getVersion());
			else
				stat = new Stat();
			zk.create(path, "123".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
			System.out.println(new String(zk.getData(path, true, stat)));
			System.out.println(stat.getCzxid() + ", mzxid:" + stat.getMzxid() + ",version:" + stat.getVersion());
			zk.setData(path, "123".getBytes(),-1);
			Thread.sleep(1000);
			zk.setData(path, "123".getBytes(),-1);
			Thread.sleep(1000);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (KeeperException e) {
			e.printStackTrace();
		}
	}
}