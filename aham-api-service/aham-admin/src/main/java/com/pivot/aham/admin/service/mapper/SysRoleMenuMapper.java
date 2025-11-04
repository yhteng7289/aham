package com.pivot.aham.admin.service.mapper;

import java.util.List;
import com.pivot.aham.admin.service.mapper.model.SysRoleMenu;
import org.apache.ibatis.annotations.Param;

import org.springframework.stereotype.Repository;
import com.pivot.aham.common.core.base.BaseMapper;

@Repository
public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenu> {
	List<Long> queryMenuIdsByRoleId(@Param("roleId") Long roleId);

//	List<Map<String, Object>> queryPermissions(@Param("roleId") Long roleId);

//	List<String> queryPermission(@Param("roleId") Long id);
}
