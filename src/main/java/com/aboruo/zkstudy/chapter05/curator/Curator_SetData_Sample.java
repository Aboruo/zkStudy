package com.aboruo.zkstudy.chapter05.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

public class Curator_SetData_Sample {
	private static CuratorFramework client = null;

	public static void main(String[] args) {
		client = CuratorFrameworkFactory.builder().connectString("172.17.0.2:2181,172.17.0.3:2181,172.17.0.4:2181")
				.sessionTimeoutMs(5000).retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
		String path = "/zk-book";
		client.start();
		try {
			/** 0 首先查看节点是否存在，若存在则删除 */
			Stat statCheck = client.checkExists().forPath(path);
			if(statCheck != null) client.delete().deletingChildrenIfNeeded().withVersion(statCheck.getVersion()).forPath(path);
			
			/** 1 创建节点 */
			client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path, "init".getBytes());
			
			/** 2 获取节点状态 */
			Stat stat = new Stat();
			byte[] content = client.getData().storingStatIn(stat).forPath(path);
			System.out.println("节点：/zk-book状态：" + path + ",new version:" + stat.getVersion() + ",content:" + new String(content));
			
			/** 3 更改节点内容 */
			client.setData().withVersion(stat.getVersion()).forPath(path,"new value".getBytes());
			System.out.println("第一次更新后节点内容：" + new String(client.getData().forPath(path)));
			//第二次针对stat更改节点内容
			client.setData().withVersion(stat.getVersion()).forPath(path,"second value".getBytes());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
