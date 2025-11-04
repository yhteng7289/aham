package com.pivot.aham.api.web.admin.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.google.common.collect.Lists;
import com.pivot.aham.admin.server.dto.SysMenuDTO;
import com.pivot.aham.admin.server.dto.SysRoleDTO;
import com.pivot.aham.admin.server.dto.SysRoleMenuDTO;
import com.pivot.aham.admin.server.remoteservice.SysAuthorizeRemoteService;
import com.pivot.aham.admin.server.remoteservice.SysRoleRemoteService;
import com.pivot.aham.api.web.admin.vo.req.SysRoleDetailReqVo;
import com.pivot.aham.api.web.admin.vo.req.SysRolePageVo;
import com.pivot.aham.api.web.admin.vo.req.SysRoleSaveReqVo;
import com.pivot.aham.api.web.admin.vo.req.SysRoleUpdateReqVo;
import com.pivot.aham.api.web.admin.vo.res.SysMenuListResVo;
import com.pivot.aham.api.web.admin.vo.res.SysRoleResVo;
import com.pivot.aham.common.core.base.AbstractController;
import com.pivot.aham.common.core.base.Message;
import com.pivot.aham.common.core.util.BeanMapperUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 角色管理
 *
 * @author addison
 * @version 2018年11月10日 下午3:15:43
 */
@RestController
@Api(value = "角色管理", description = "角色管理")
@RequestMapping(value = "/api/v1/in/shiro")
public class AdminSysRoleController extends AbstractController {
    @Resource
    private SysRoleRemoteService sysRoleService;
    @Resource
    private SysAuthorizeRemoteService sysAuthorizeService;

    @ApiOperation(value = "角色列表")
    @RequiresPermissions("sys:base:role:read")
    @PostMapping(value = "/role/all")
    public Message<Page<SysRoleResVo>> query(@RequestBody SysRolePageVo sysRolePageVo) {
        //构造分页对象
        Page<SysRoleDTO> rowBounds = new Page<>(
                sysRolePageVo.getPageNo(),sysRolePageVo.getPageSize());
        SysRoleDTO sysRoleDTO = new SysRoleDTO();
        sysRoleDTO = BeanMapperUtils.map(sysRolePageVo,SysRoleDTO.class);
        sysRoleDTO.setDeleted("N"); // Added WooiTatt
        Page<SysRoleDTO> pagination = sysRoleService.queryPageList(sysRoleDTO,rowBounds);
        Page<SysRoleResVo> sysUserPagination = new Page<>();
        sysUserPagination = BeanMapperUtils.map(pagination,sysUserPagination.getClass());

        List<SysRoleResVo> sysRoleResVoList = Lists.newArrayList();
        List<SysRoleDTO> sysRoleDTOS = pagination.getRecords();
        for(SysRoleDTO sysRole:sysRoleDTOS){
            SysRoleResVo sysRoleResVo = new SysRoleResVo();
            sysRoleResVo.setDeptId(sysRole.getDeptId());
            sysRoleResVo.setDeptName(sysRole.getDeptName());
            sysRoleResVo.setDescription(sysRole.getDescription());
            sysRoleResVo.setRoleId(sysRole.getId());
            sysRoleResVo.setRoleName(sysRole.getRoleName());
            sysRoleResVo.setRoleStatus(sysRole.getRoleStatus());
            sysRoleResVoList.add(sysRoleResVo);

        }
//        List<SysRoleResVo> sysRoleResVoList = BeanMapperUtils.mapList(sysRoleDTOS,SysRoleResVo.class);
        sysUserPagination.setRecords(sysRoleResVoList);

        for(SysRoleResVo sysRoleResVo:sysRoleResVoList){
            List<Long> permissionIds = sysAuthorizeService.queryMenuIdsByRoleId(sysRoleResVo.getRoleId());
            sysRoleResVo.setPermissionIdList(permissionIds);
        }


        return Message.success(sysUserPagination);
    }

//    @ApiOperation(value = "查询角色")
//    @RequiresPermissions("sys:base:role:read")
//    @PutMapping(value = "/read/list")
//    public Message<List<SysRoleResVo>> list( @RequestBody SysRoleListVo sysRoleVo) {
//        SysRoleDTO sysMenuDTO = new SysRoleDTO();
//        BeanMapperUtils.copy(sysRoleVo,sysMenuDTO);
//
//        List<SysRoleDTO> list = sysRoleService.queryList(sysMenuDTO);
//        List<SysRoleResVo> resList = BeanMapperUtils.mapList(list,SysRoleResVo.class);
//        return Message.success(resList);
//    }

    @ApiOperation(value = "角色详情")
    @RequiresPermissions("sys:base:role:read")
    @PostMapping(value = "/role/info")
    public Message<SysRoleResVo> get(@RequestBody SysRoleDetailReqVo sysRoleDetailReqVo) {
        SysRoleDTO sysResDTO = sysRoleService.queryById(sysRoleDetailReqVo.getRoleId());
        SysRoleResVo sysRoleResVo = BeanMapperUtils.map(sysResDTO,SysRoleResVo.class);
        sysRoleResVo.setRoleId(sysResDTO.getId());
        List<Long> permissionIds = sysAuthorizeService.queryMenuIdsByRoleId(Long.valueOf(sysRoleResVo.getRoleId()));
        sysRoleResVo.setPermissionIdList(permissionIds);

        return Message.success(sysRoleResVo);
    }

    @ApiOperation(value = "角色权限列表")
    @RequiresPermissions("sys:base:role:read")
    @PostMapping(value = "/permission/list")
    public Message<List<SysMenuListResVo>> getRolePermissions(@RequestBody SysRoleDetailReqVo sysRoleDetailReqVo) {
        List<SysMenuListResVo> sysMenuListResVoList = Lists.newArrayList();
        List<SysMenuDTO> sysMenuDTOS = sysAuthorizeService.queryMenusByRoleId(sysRoleDetailReqVo.getRoleId());
        for(SysMenuDTO sysMenuDTO:sysMenuDTOS){
            SysMenuListResVo sysMenuListResVo = new SysMenuListResVo();
            sysMenuListResVo.setPermissionName(sysMenuDTO.getPermissionName());
            sysMenuListResVo.setPermissionCode(sysMenuDTO.getPermissionCode());
            sysMenuListResVo.setDescription(sysMenuDTO.getDescription());
            sysMenuListResVo.setPermissionId(sysMenuDTO.getId());
            sysMenuListResVoList.add(sysMenuListResVo);
        }
        return Message.success(sysMenuListResVoList);
    }



    @PostMapping(value = "/role/save")
    @ApiOperation(value = "创建角色")
    @RequiresPermissions("sys:base:role:updateOrInsert")
    public Message save(@RequestBody SysRoleSaveReqVo param) {
        SysRoleDTO sysRoleDTO = BeanMapperUtils.map(param,SysRoleDTO.class);
        SysRoleDTO resRole = sysRoleService.updateOrInsert(sysRoleDTO);

        List<Long> stringList = param.getPermissionIdList();
        if(stringList != null) {
            List<SysRoleMenuDTO> listDto = Lists.newArrayList();
            for (Long per : stringList) {
                SysRoleMenuDTO sysRoleMenuDTO = new SysRoleMenuDTO();
                sysRoleMenuDTO.setPermissionId(per);
                sysRoleMenuDTO.setRoleId(resRole.getId());
                listDto.add(sysRoleMenuDTO);
            }
            sysAuthorizeService.updateRoleMenu(listDto);
        }

        return Message.success();
    }

    
     // Modify WooiTatt
    // Add in Delete Role Feature
    @PostMapping(value = "/role/update")
    @ApiOperation(value = "修改角色")
    @RequiresPermissions("sys:base:role:updateOrInsert")
    public Message update(@RequestBody SysRoleUpdateReqVo param) {
        
        SysRoleDTO sysRoleDTO = BeanMapperUtils.map(param, SysRoleDTO.class);
        sysRoleDTO.setId(param.getRoleId());
        
        if(param.getDeleted() != null && param.getDeleted().equalsIgnoreCase("Y")){
            sysRoleDTO.setDeleted("Y");
        }
        SysRoleDTO resRole = sysRoleService.updateOrInsert(sysRoleDTO);
        
        if(param.getDeleted() == null || !param.getDeleted().equalsIgnoreCase("Y")){
            List<Long> stringList = param.getPermissionIdList();
            if (stringList != null) {
                List<SysRoleMenuDTO> listDto = Lists.newArrayList();
                for (Long per : stringList) {
                    SysRoleMenuDTO sysRoleMenuDTO = new SysRoleMenuDTO();
                    sysRoleMenuDTO.setPermissionId(per);
                    sysRoleMenuDTO.setRoleId(resRole.getId());
                    listDto.add(sysRoleMenuDTO);
                }
                sysAuthorizeService.updateRoleMenu(listDto);
            }
        }

        return Message.success();
    }

   /* @PostMapping(value = "/role/update")
    @ApiOperation(value = "修改角色")
    @RequiresPermissions("sys:base:role:updateOrInsert")
    public Message update(@RequestBody SysRoleUpdateReqVo param) {
        SysRoleDTO sysRoleDTO = BeanMapperUtils.map(param,SysRoleDTO.class);
        sysRoleDTO.setId(param.getRoleId());
        SysRoleDTO resRole = sysRoleService.updateOrInsert(sysRoleDTO);

        List<Long> stringList = param.getPermissionIdList();
        if(stringList != null) {
            List<SysRoleMenuDTO> listDto = Lists.newArrayList();
            for (Long per : stringList) {
                SysRoleMenuDTO sysRoleMenuDTO = new SysRoleMenuDTO();
                sysRoleMenuDTO.setPermissionId(per);
                sysRoleMenuDTO.setRoleId(resRole.getId());
                listDto.add(sysRoleMenuDTO);
            }
            sysAuthorizeService.updateRoleMenu(listDto);
        }

        return Message.success();
    }
*/

//    @DeleteMapping
//    @ApiOperation(value = "删除角色")
//    @RequiresPermissions("sys:base:role:delete")
//    public Message delete(@RequestBody SysRoleDetailReqVo sysRoleDetailReqVo) {
//        sysRoleService.delete(sysRoleDetailReqVo.getId());
//        return Message.success();
//    }
}
