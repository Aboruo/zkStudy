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

public class ZkAuth_Delete implements Watcher {
	private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
	private static String PATH = "/zk-book-auth_test";
	private static String PATHSUB = "/zk-book-auth_test/child";
	@Override
	public void process(WatchedEvent event) {
		if(KeeperState.SyncConnected == event.getState()) {
			if(EventType.None == event.getType() && null == event.getPath()) {
				connectedSemaphore.countDown();
			}
		}
	}

	public static void main(String[] args) {
		try {
			ZooKeeper zk1 = new ZooKeeper("172.17.0.2:2181", 5000, new ZkAuth_Delete());
			connectedSemaphore.countDown();
			zk1.addAuthInfo("digest", "foo:true".getBytes());
			Stat stat = zk1.exists(PATH, true);
			if(stat != null) zk1.delete(PATH, stat.getVersion());
			zk1.create(PATH, "init".getBytes(), Ids.CREATOR_ALL_ACL,CreateMode.PERSISTENT);
			zk1.create(PATHSUB, "initsub".getBytes(), Ids.CREATOR_ALL_ACL, CreateMode.EPHEMERAL);
			
			/** zk2 不添加授权-删除子节点 */
			try {
				ZooKeeper zk2 = new ZooKeeper("172.17.0.2:2181", 5000, null);
				zk2.delete(PATHSUB, -1);
			} catch (Exception e) {
				System.out.println("删除节点失败：" + e.getMessage());
			}
			
			
			/** zk3 添加授权后-删除子节点 */
			ZooKeeper zk3 = new ZooKeeper("172.17.0.2:2181", 5000, null);
			zk3.addAuthInfo("digest", "foo:true".getBytes());
			zk3.delete(PATHSUB, -1);
			System.out.println("成功删除节点：" + PATHSUB);
			
			/** zk4 不添加授权-直接删除父节点 */
			ZooKeeper zk4 = new ZooKeeper("172.17.0.2:2181", 5000, null);
			zk4.delete(PATH, -1);
			System.out.println("成功删除节点：" + PATH);
			Thread.sleep(3000);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
