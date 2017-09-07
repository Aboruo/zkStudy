package com.aboruo.zkstudy.chapter05.zkclient;

import java.util.List;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;

public class Zkclient_CreateAndDel_Sample {

	public static void main(String[] args) throws InterruptedException {
		ZkClient zkClient = new ZkClient("172.17.0.2:2181", 5000);
		/** 1.递归创建带有父目录的子节点 */
		String pathParetn = "/zk-book/sub";
		String path = "/zk-book/sub/c1";
		String path2 = "/zk-book/sub/c2";
		zkClient.subscribeChildChanges(pathParetn, new IZkChildListener() {
			@Override
			public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
				System.out.println(parentPath + " 's child changed,currentChilds:" + currentChilds);
			}
		});
		zkClient.createPersistent(pathParetn,true);
		zkClient.createPersistent(path, true);
		Thread.sleep(1000);
		zkClient.writeData(path, "test1");
		zkClient.createPersistent(path2, true);
		Thread.sleep(1000);
		zkClient.writeData(path2, "test2");
		List<String> c1List = zkClient.getChildren("/zk-book/sub");
		System.out.println(c1List);
		String content1 = zkClient.readData(path2);
		System.out.println(content1);
		/** 2.递归删除目录及节点 */
		zkClient.deleteRecursive(path);
	}

}
