package com.pivot.aham.api.web.admin.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.pivot.aham.admin.server.dto.SysDeptDTO;
import com.pivot.aham.admin.server.remoteservice.SysDeptRemoteService;
import com.pivot.aham.api.web.admin.vo.req.SysDeptDetailReqVo;
import com.pivot.aham.api.web.admin.vo.req.SysDeptListReqVo;
import com.pivot.aham.api.web.admin.vo.req.SysDeptPageReqVo;
import com.pivot.aham.api.web.admin.vo.res.SysDeptResVo;
import com.pivot.aham.common.core.base.AbstractController;
import com.pivot.aham.common.core.base.Message;
import com.pivot.aham.common.core.util.BeanMapperUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 部门管理
 *
 * @author addison
 * @since 2018年11月19日
 */
@RestController
@RequestMapping(value = "/api/v1/in/shiro/dept")
@Api(value = "部门管理", description = "部门管理")
public class AdminSysDeptController extends AbstractController{
	@Resource
	private SysDeptRemoteService sysDeptService;
	
	@ApiOperation(value = "查询部门")
	@RequiresPermissions("sys:base:dept:read")
	@PostMapping(value = "/read/list")
	public Message<List<SysDeptResVo>> list(@RequestBody SysDeptListReqVo sysDeptListReqVo) {

		SysDeptDTO sysDeptDTO = new SysDeptDTO();
		BeanMapperUtils.copy(sysDeptListReqVo,sysDeptDTO);
		List<SysDeptDTO> sysDeptDTOList = sysDeptService.queryList(sysDeptDTO);
		List<SysDeptResVo> list = BeanMapperUtils.mapList(sysDeptDTOList,SysDeptResVo.class);

		return Message.success(list);
	}

	@ApiOperation(value = "查询部门")
	@RequiresPermissions("sys:base:dept:read")
	@PostMapping(value = "/read/page")
	public Message<Page<SysDeptResVo>> queryPage(@RequestBody SysDeptPageReqVo sysDeptPageReqVo) {
		//构造分页对象
		Page<SysDeptDTO> rowBounds = new Page<>(
		sysDeptPageReqVo.getPageIndex(),sysDeptPageReqVo.getPageSize());

		SysDeptDTO sysDeptDTO = new SysDeptDTO();
		BeanMapperUtils.copy(sysDeptPageReqVo,sysDeptDTO);

		Page<SysDeptDTO> pagination = sysDeptService.queryPageList(sysDeptDTO,rowBounds);
		Page<SysDeptResVo> sysUserPagination = new Page<>();
		BeanMapperUtils.copy(pagination,sysUserPagination);

		return Message.success(sysUserPagination);
	}

	@ApiOperation(value = "部门详情")
	@RequiresPermissions("sys:base:dept:read")
	@PostMapping(value = "/read/detail")
	public Message<SysDeptResVo> get(@RequestBody SysDeptDetailReqVo param) {
		SysDeptDTO sysDeptDTO = sysDeptService.queryById(param.getId());
		SysDeptResVo sysDeptResVo = new SysDeptResVo();
		BeanMapperUtils.copy(sysDeptDTO,sysDeptResVo);
		return Message.success(sysDeptResVo);
	}

//	@PostMapping
//	@ApiOperation(value = "修改部门")
//	@RequiresPermissions("sys:base:dept:updateOrInsert")
//	public Message update(@RequestBody SysDeptUpdateReqVo param) {
//		SysDeptDTO sysDeptDTO = new SysDeptDTO();
//		BeanMapperUtils.copy(param,sysDeptDTO);
//		sysDeptService.updateOrInsert(sysDeptDTO);
//		return Message.success();
//	}
//
//	@DeleteMapping
//	@ApiOperation(value = "删除部门")
//	@RequiresPermissions("sys:base:dept:delete")
//	public Message delete(@RequestBody SysDeptDetailReqVo param) {
//		sysDeptService.delete(param.getId());
//		return Message.success();
//	}
}
