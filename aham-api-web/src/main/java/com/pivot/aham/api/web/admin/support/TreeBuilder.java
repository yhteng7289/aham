package com.pivot.aham.api.web.admin.support;

import com.pivot.aham.api.web.admin.vo.res.SysMenuResVo;

import java.util.ArrayList;
import java.util.List;

/**
 * 构造树
 *
 */
public class TreeBuilder {
    List<SysMenuResVo> nodes = new ArrayList<>();

    public static List<SysMenuResVo> buildTree(List<SysMenuResVo> nodes) {

        TreeBuilder treeBuilder = new TreeBuilder(nodes);

        return treeBuilder.buildTree();
    }

    public TreeBuilder() {
    }

    public TreeBuilder(List<SysMenuResVo> nodes) {
        super();
        this.nodes = nodes;
    }

//    // 构建JSON树形结构
//    public String buildJSONTree() {
//        List<SysMenuResVo> nodeTree = buildTree();
//        JSONArray jsonArray = JSONArray.fromObject(nodeTree);
//        return jsonArray.toString();
//    }

    // 构建树形结构
    public List<SysMenuResVo> buildTree() {
        List<SysMenuResVo> treeNodes = new ArrayList<>();
        List<SysMenuResVo> rootNodes = getRootNodes();
        for (SysMenuResVo rootNode : rootNodes) {
            buildChildNodes(rootNode);
            treeNodes.add(rootNode);
        }
        return treeNodes;
    }

    // 递归子节点
    public void buildChildNodes(SysMenuResVo node) {
        List<SysMenuResVo> children = getChildNodes(node);
        if (!children.isEmpty()) {
            for (SysMenuResVo child : children) {
                buildChildNodes(child);
            }
            node.setChildNodes(children);
        }
    }

    // 获取父节点下所有的子节点
    public List<SysMenuResVo> getChildNodes(SysMenuResVo pnode) {
        List<SysMenuResVo> childNodes = new ArrayList<>();
        for (SysMenuResVo n : nodes) {
            if (pnode.getPermissionId().equals(n.getParentId())) {
                childNodes.add(n);
            }
        }
        return childNodes;
    }

    // 判断是否为根节点
    public boolean rootNode(SysMenuResVo node) {
        boolean isRootNode = true;
        for (SysMenuResVo n : nodes) {
            if (node.getParentId().equals(n.getPermissionId())) {
                isRootNode = false;
                break;
            }
        }
        return isRootNode;
    }

    // 获取集合中所有的根节点
    public List<SysMenuResVo> getRootNodes() {
        List<SysMenuResVo> rootNodes = new ArrayList<>();
        for (SysMenuResVo n : nodes) {
            if (rootNode(n)) {
                rootNodes.add(n);
            }
        }
        return rootNodes;
    }
}