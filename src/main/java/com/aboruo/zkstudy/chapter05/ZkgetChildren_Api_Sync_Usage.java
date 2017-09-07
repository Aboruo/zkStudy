package com.aboruo.zkstudy.chapter05;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;

public class ZkgetChildren_Api_Sync_Usage implements Watcher {
	private static CountDownLatch connetedSemaphore = new CountDownLatch(1);
	private static ZooKeeper zk = null;
	@Override
	public void process(WatchedEvent event) {
		if(KeeperState.SyncConnected == event.getState()) {
			if(EventType.None == event.getType() && null == event.getPath()) {
				connetedSemaphore.countDown();
			}else if(event.getType() == EventType.NodeChildrenChanged){
				try {
					System.out.println("ReGet Child:" + zk.getChildren(event.getPath(), true));
				} catch (KeeperException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void main(String[] args) {
		try {
			zk = new ZooKeeper("172.17.0.2:2181", 5000, new ZkgetChildren_Api_Sync_Usage());
			connetedSemaphore.await();
			String path = "/zk-book";
			/** 1.判断节点是否存在，如果存在先删除 */
			Stat stat = zk.exists(path, true);
			/*List<String> tmpChildren = zk.getChildren(path, true);
			System.out.println(tmpChildren);*/
			if(stat != null) zk.delete(path, stat.getVersion());
			zk.create(path, "".getBytes(), Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
			zk.create(path + "/c1", "".getBytes(),Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL);
			List<String> childrenList = zk.getChildren(path, true);
			//打印当前字节点列表
			System.out.println(childrenList);
			zk.create(path + "/c2", "".getBytes(),Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL);
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
