package com.pivot.aham.common.core.support.zk;

import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.util.List;


public interface IZKClient {

	/**
	 * 创建节点
	 * @param path
	 * @param value
	 * @return
	 */
	String createNodeIfNotExist(String path, String value, CreateMode mode);

	String createNodeWithCallBack(String path, String value, CreateMode mode, BackgroundCallback callback);

	String getNodeData(String path);

	String getNodeData(String path, Stat stat);

	boolean checkNodeIsEx(String path);

	List<String> getNodeChildren(String path);

	boolean deleteNode(String path);

	boolean deleteNode(String path, int version);

	String updateNode(String path, String value);

	String updateNode(String path, String value, int version);


	void addDataListener(String path, NodeCacheListener nodeCacheListener);
	void addPathChildrenListener(String path, PathChildrenCacheListener pathChildrenCacheListener);
	void addTreeListener(String path, TreeCacheListener treeCacheListener);


}
