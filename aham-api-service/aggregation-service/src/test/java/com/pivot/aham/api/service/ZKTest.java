package com.pivot.aham.api.service;

import com.pivot.aham.common.core.support.zk.IZKClient;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class ZKTest {

    @Autowired
    private IZKClient client;

    @Test
    public void init() {

//        boolean isEx = client.deleteNode( "/test" );
        client.createNodeIfNotExist( "/test","addison",CreateMode.PERSISTENT);
//        String tmpWorker = client.createNodeIfNotExist( "/testTmp","addison",CreateMode.EPHEMERAL_SEQUENTIAL);
//        String data = client.getNodeData("/test");
//        Stat dataStat = new Stat();
//        client.getNodeData("/test",dataStat);
//        String updateNodeData = client.updateNode("/test","addison1111");
        final Stat dataStat2 = new Stat();
//        client.getNodeData("/test",dataStat2);
//        String updateNodeDataVersion = client.updateNode("/test","addison1111",dataStat.getVersion()+1);
//        client.deleteNode( "/test" );
//
//        client.createNodeWithCallBack("/test","addison",CreateMode.PERSISTENT,new BackgroundCallback(){
//            @Override
//            public void processResult(CuratorFramework client, CuratorEvent interevent) throws Exception {
//                if(interevent.getType()== CuratorEventType.CREATE){
//                    System.out.println("create path="+interevent.getPath()+",code="+interevent.getResultCode());
//                }else if(interevent.getType()==CuratorEventType.GET_DATA){
//                    System.out.println("get path="+interevent.getPath()+",data="+new String(interevent.getData()));
//                }else if(interevent.getType()==CuratorEventType.SET_DATA){
//                    System.out.println("set path="+interevent.getPath()+",data="+new String(client.getData().forPath(interevent.getPath())));
//                }else if(interevent.getType()==CuratorEventType.DELETE){
//                    System.out.println("delete path="+interevent.getPath());
//                }
//            }
//        });

        client.addDataListener("/test", new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                client.getNodeData("/test",dataStat2);
                System.out.println(dataStat2.toString());
                System.out.println("===========node Changed====================");
            }
        });
        client.updateNode("/test","2222");



        client.addPathChildrenListener("/test", new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent event) throws Exception {
                if(event.getType()== PathChildrenCacheEvent.Type.CHILD_ADDED){
                    System.out.println(event.getData());
                    System.out.println("===========path childEvent====================");
                }
            }
        });
//        client.createNodeIfNotExist("/test/child","test",CreateMode.EPHEMERAL_SEQUENTIAL);

        client.addTreeListener("/test", new TreeCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, TreeCacheEvent event) throws Exception {
                if(event.getType()== TreeCacheEvent.Type.NODE_ADDED){
                    System.out.println(event.getData().getPath());
                    System.out.println("===========tree childEvent====================");
                }
            }
        });
        client.createNodeIfNotExist("/test/child1","test",CreateMode.EPHEMERAL_SEQUENTIAL);


//        client.getNodeData("/test",dataStat2);

//        client.deleteNode( "/test" );




    }
}
