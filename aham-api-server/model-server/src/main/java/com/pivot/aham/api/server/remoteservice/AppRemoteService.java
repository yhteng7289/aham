package com.pivot.aham.api.server.remoteservice;

import com.pivot.aham.api.server.dto.app.reqdto.AppUserStatementReqDTO;
import com.pivot.aham.api.server.dto.app.resdto.AppUserStatementResDTO;
import com.pivot.aham.api.server.dto.app.resdto.RegisterResDTO;
import com.pivot.aham.api.server.dto.CheckOTPDTO;
import com.pivot.aham.api.server.dto.GetOTPDTO;
import com.pivot.aham.api.server.dto.NewSysUserDTO;
import com.pivot.aham.api.server.dto.PersonalInfoNotUploadDTO;
import com.pivot.aham.api.server.dto.PersonalInfoUploadDTO;
import com.pivot.aham.common.core.base.BaseRemoteService;
import com.pivot.aham.common.core.base.RpcMessage;

import java.util.List;

public interface AppRemoteService extends BaseRemoteService {

    RpcMessage<String> uploadPersonalInfo(PersonalInfoUploadDTO personalInfoUploadDTO);

    RpcMessage<RegisterResDTO> savePersonalInfo(PersonalInfoNotUploadDTO personalInfoNotUploadDTO);

    RpcMessage<String> getOTP(GetOTPDTO getOTPDTO);
    
    RpcMessage<String> loginGetOTP(NewSysUserDTO newSysUserDTO);
    
    RpcMessage<String> loginCheckOTP(CheckOTPDTO checkOTPDTO);
    
    RpcMessage<String> checkOTP(CheckOTPDTO checkOTPDTO);

    RpcMessage<List<AppUserStatementResDTO>> getUserStatementList(AppUserStatementReqDTO appUserStatementReqDTO);

}
