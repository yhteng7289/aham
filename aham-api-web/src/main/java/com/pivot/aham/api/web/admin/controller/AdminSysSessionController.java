package com.pivot.aham.api.web.admin.controller;

import com.pivot.aham.admin.server.dto.SysSessionDTO;
import com.pivot.aham.admin.server.remoteservice.SysSessionRemoteService;
import com.pivot.aham.common.core.base.AbstractController;
import com.pivot.aham.common.core.base.Message;
import com.pivot.aham.common.core.listener.SessionListener;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 查询session
 *
 * @author addison
 * @since 2018年11月19日
 */
@RestController
@Api(value = "会话管理", description = "会话管理")
@RequestMapping(value = "/api/v1/in/shiro/session")
public class AdminSysSessionController extends AbstractController {
    @Resource
	private SessionListener sessionListener;
    @Resource
	private SysSessionRemoteService sysSessionService;


	@ApiOperation(value = "查询会话")
	@PostMapping(value = "/read/list")
	public Object get(ModelMap modelMap) {
		Integer number = sessionListener.getAllUserNumber();
		modelMap.put("userNumber", number);
		SysSessionDTO sysSessionDTO = new SysSessionDTO();
		List<SysSessionDTO> sysSessionDTOList = sysSessionService.queryList(sysSessionDTO);
		modelMap.put("sysSessionList",sysSessionDTOList);
		return modelMap;
	}

	@DeleteMapping
	@ApiOperation(value = "删除会话")
	public Object delete(@RequestParam String sessionId) {
		SysSessionDTO sysSessionDTO = new SysSessionDTO();
		sysSessionDTO.setSessionId(sessionId);
		sysSessionService.deleteBySessionId(sysSessionDTO);
		return Message.success();
	}
}
