package com.pivot.aham.common.core.support.zk;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class ZKClientImpl implements IZKClient {


    @Autowired
    private CuratorFramework client;

    @Override
    public String createNodeIfNotExist(String path, String value,CreateMode mode) {
        String result = null;
        try {
            if (!checkNodeIsEx(path)) {
                if (null == value) {
                    this.client.create().creatingParentsIfNeeded().withMode(mode).forPath(path,new byte[0]);
                } else {
                    result = this.client.create().creatingParentsIfNeeded().withMode(mode)
                        .forPath(path, value.getBytes());
                }
            } else {
                log.info("[createNodeIfNotExist] 节点已经存在。path:{}", path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("[createNodeIfNotExist] path:{},value:{},result:{}", path, value, result);
        return result;
    }

    @Override
    public String createNodeWithCallBack(String path, String value,CreateMode mode, BackgroundCallback callback) {
        ExecutorService service = Executors.newFixedThreadPool(2);
        try {
            client.create().creatingParentsIfNeeded().withMode(mode)
                .inBackground(callback, service).forPath(path, value.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getNodeData(String path) {
        byte[] data = new byte[0];
        try {
            if (checkNodeIsEx(path)) {
                data = this.client.getData().forPath(path);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String(data, Charset.forName("utf8"));
    }

    @Override
    public String getNodeData(String path, Stat stat) {

        byte[] data = new byte[0];
        try {
            if (checkNodeIsEx(path)) {
                data = this.client.getData().storingStatIn(stat).forPath(path);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String(data, Charset.forName("utf8"));
    }

    @Override
    public boolean checkNodeIsEx(String path) {
        boolean exist = false;
        try {
            Stat stat = this.client.checkExists().forPath(path);

            if (stat != null) {
                exist = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info( "[CheckNode] Path:{} is exist?{}",path, exist);
        return exist;
    }

    @Override
    public List<String> getNodeChildren(String path) {
        List<String> children = Lists.newArrayList();
        try {
            if (checkNodeIsEx(path)) {
                children = this.client.getChildren().forPath(path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return children;
    }

    @Override
    public boolean deleteNode(String path) {
        boolean isSuccess = false;
        try {
            this.client.delete().guaranteed().deletingChildrenIfNeeded().withVersion(-1).forPath(path);
            isSuccess = true;
            log.info( "[DeleteNode] path:{},{}",path,isSuccess );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    @Override
    public boolean deleteNode(String path, int version) {
        boolean isSuccess = false;
        try {
            this.client.delete().deletingChildrenIfNeeded().withVersion(version).forPath(path);
            isSuccess = true;
            log.info( "[DeleteNode] path:{},{}",path,isSuccess );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    @Override
    public String updateNode(String path, String value) {
        String result = null;
        try {
            Stat stat = client.setData().forPath(path, value.getBytes());
            if (stat != null) {
                result = value;
            }
            log.info( "[UpdateNode] path:{},value:{}",path,value );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public String updateNode(String path, String value, int version) {
        String result = null;
        try {
            Stat stat = client.setData().withVersion(version).forPath(path, value.getBytes());
            if (stat != null) {
                result = value;
            }
            log.info( "[UpdateNode] path:{},value:{}",path,value );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void addDataListener(String path,NodeCacheListener nodeCacheListener) {
        final NodeCache nodeCache = new NodeCache(this.client,path);
        try {
            nodeCache.start();
        } catch (Exception e) {
            log.error("数据监听启动异常",e);
        }
        nodeCache.getListenable().addListener(nodeCacheListener);

    }

    @Override
    public void addPathChildrenListener(String parentPath,PathChildrenCacheListener pathChildrenCacheListener) {
        PathChildrenCache pathChildrenCache = new PathChildrenCache(client,parentPath,true);
        try {
            pathChildrenCache.start();
        } catch (Exception e) {
            log.error("子节点监听启动异常",e);
        }
        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
    }

    @Override
    public void addTreeListener(String path,TreeCacheListener treeCacheListener) {
        TreeCache treeCache = new TreeCache(client,path);
        try {
            treeCache.start();
        } catch (Exception e) {
            log.error("节点监听启动异常",e);
        }
        treeCache.getListenable().addListener(treeCacheListener);
    }
}
