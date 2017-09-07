package com.aboruo.zkstudy.chapter05;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class ZkSetData_Api_Async_Usage implements Watcher {
	private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
	private static ZooKeeper zk;
	private static Stat stat = new Stat();
	@Override
	public void process(WatchedEvent event) {
		if(KeeperState.SyncConnected == event.getState()) {
			if(EventType.None == event.getType() && null == event.getPath()) {
				connectedSemaphore.countDown();
			}else if(EventType.NodeDataChanged == event.getType()) {
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
		/** 1. 创建zookeeper链接 */
		try {
			zk = new ZooKeeper("172.17.0.2:2181", 5000, new ZkSetData_Api_Async_Usage());
			connectedSemaphore.await();
			String path = "/zk-book";
			zk.create(path, "123".getBytes(),Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL);
			System.out.println(new String(zk.getData(path, true, stat)));
			zk.setData(path,"456".getBytes(), -1,new IStatCallBackImpl(),"change the node value from 123 to 456");
			Thread.sleep(1000);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeeperException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
class IStatCallBackImpl implements AsyncCallback.StatCallback{

	@Override
	public void processResult(int rc, String path, Object ctx, Stat stat) {
		if(rc == 0) {
			System.out.println("处理返回状态：" + rc + ",path:" + path + ",ctx:" + ctx + ",stat:" + stat);
		}
	}
	
}
