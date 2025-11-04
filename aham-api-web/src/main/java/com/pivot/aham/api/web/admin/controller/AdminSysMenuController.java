package com.pivot.aham.api.web.admin.controller;

import com.google.common.collect.Lists;
import com.pivot.aham.admin.server.dto.SysMenuDTO;
import com.pivot.aham.admin.server.remoteservice.SysMenuRemoteService;
import com.pivot.aham.api.web.admin.support.TreeBuilder;
import com.pivot.aham.api.web.admin.vo.req.SysMenuListReqVo;
import com.pivot.aham.api.web.admin.vo.req.SysMenuSaveReqVo;
import com.pivot.aham.api.web.admin.vo.req.SysMenuUpdateReqVo;
import com.pivot.aham.api.web.admin.vo.res.SysMenuListResVo;
import com.pivot.aham.api.web.admin.vo.res.SysMenuResVo;
import com.pivot.aham.common.core.base.Message;
import com.pivot.aham.common.core.util.BeanMapperUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 菜单
 *
 * @author addison
 * @since 2018年11月19日
 */
@RestController
@Api(value = "权限管理", description = "权限管理")
@RequestMapping(value = "/api/v1/in/shiro")
public class AdminSysMenuController extends AbstractMethodError {
    @Resource
    private SysMenuRemoteService sysMenuService;

//    @ApiOperation(value = "分页查询菜单")
//    @PutMapping(value = "/read/page")
//    @RequiresPermissions("sys:base:permission:read")
//    public Message<Pagination<SysMenuResVo>> queryPage(@RequestBody SysMenuPageReqVo sysMenuVo) {
//        //构造分页对象
//        Pagination<SysMenuDTO> rowBounds = new Pagination<>(
//        sysMenuVo.getPageIndex(),sysMenuVo.getPageSize());
//
//        SysMenuDTO sysMenuDTO = new SysMenuDTO();
//        BeanMapperUtils.copy(sysMenuVo,sysMenuDTO);
//
//        Pagination<SysMenuDTO> pagination = sysMenuService.queryPageList(sysMenuDTO,rowBounds);
//        Pagination<SysMenuResVo> sysUserPagination = new Pagination<>();
//        BeanMapperUtils.copy(pagination,sysUserPagination);
//
//        return Message.success(sysUserPagination);
//    }

    @ApiOperation(value = "权限列表")
    @PostMapping(value = "/permission/all")
    @RequiresPermissions("sys:base:permission:read")
    public Message<List<SysMenuListResVo>> getList(@RequestBody SysMenuListReqVo sysMenuVo) {
        SysMenuDTO sysMenuDTO = new SysMenuDTO();
        BeanMapperUtils.copy(sysMenuVo,sysMenuDTO);

        List<SysMenuDTO> sysMenuDTOS = sysMenuService.queryList(sysMenuDTO);
        List<SysMenuListResVo> resList = Lists.newArrayList();

        for(SysMenuDTO sysMenu:sysMenuDTOS){
            SysMenuListResVo sysMenuResVo = new SysMenuListResVo();
            sysMenuResVo.setPermissionId(sysMenu.getId());
            sysMenuResVo.setDescription(sysMenu.getDescription());
            sysMenuResVo.setPermissionCode(sysMenu.getPermissionCode());
            sysMenuResVo.setPermissionName(sysMenu.getPermissionName());
            resList.add(sysMenuResVo);
        }


        return Message.success(resList);
    }

    @ApiOperation(value = "查询权限树")
    @PostMapping(value = "/permission/tree/all")
    @RequiresPermissions("sys:base:permission:read")
    public Message<List<SysMenuResVo>> getMenuTree() {
        SysMenuDTO sysMenuDTO = new SysMenuDTO();
        List<SysMenuDTO> list = sysMenuService.queryList(sysMenuDTO);
        List<SysMenuResVo> resList = Lists.newArrayList();
        for(SysMenuDTO sysMenu:list){
            SysMenuResVo sysMenuResVo = new SysMenuResVo();
            sysMenuResVo.setPermissionId(sysMenu.getId());
            sysMenuResVo.setDescription(sysMenu.getDescription());
            sysMenuResVo.setNodeType(sysMenu.getNodeType());
            sysMenuResVo.setParentId(sysMenu.getParentId());
            sysMenuResVo.setPermissionCode(sysMenu.getPermissionCode());
            sysMenuResVo.setPermissionName(sysMenu.getPermissionName());
            resList.add(sysMenuResVo);
        }
//        List<SysMenuResVo> resList = BeanMapperUtils.mapList(list,SysMenuResVo.class);
        List<SysMenuResVo> listTree = TreeBuilder.buildTree(resList);

        return Message.success(listTree);
    }


//    @ApiOperation(value = "权限详情")
//    @PutMapping(value = "/read/detail")
//    @RequiresPermissions("sys:base:permission:read")
//    public Message<SysMenuListResVo> get(@RequestBody SysMenuDetailReqVo sysMenuVo) {
//        SysMenuDTO sysResDTO = sysMenuService.queryById(sysMenuVo.getPermissionId());
//        SysMenuListResVo sysMenuResVo = BeanMapperUtils.map(sysResDTO,SysMenuListResVo.class);
//        return Message.success(sysMenuResVo);
//    }

    @PostMapping(value = "/permission/save")
    @ApiOperation(value = "创建权限")
    @RequiresPermissions("sys:base:permission:updateOrInsert")
    public Message save(@RequestBody SysMenuSaveReqVo param) {
        SysMenuDTO sysMenuDTO = BeanMapperUtils.map(param,SysMenuDTO.class);
        sysMenuService.updateOrInsert(sysMenuDTO);
        return Message.success();
    }

    @PostMapping(value = "/permission/update")
    @ApiOperation(value = "修改权限")
    @RequiresPermissions("sys:base:permission:updateOrInsert")
    public Message update(@RequestBody SysMenuUpdateReqVo param) {
        SysMenuDTO sysMenuDTO = BeanMapperUtils.map(param,SysMenuDTO.class);
        sysMenuDTO.setId(param.getPermissionId());
        sysMenuService.updateOrInsert(sysMenuDTO);
        return Message.success();
    }

    
//    @DeleteMapping
//    @ApiOperation(value = "删除菜单")
//    @RequiresPermissions("sys:base:permission:delete")
//    public Message delete( @RequestBody SysMenuDetailReqVo param) {
//        sysMenuService.delete(param.getPermissionId());
//        return Message.success();
//    }
}
