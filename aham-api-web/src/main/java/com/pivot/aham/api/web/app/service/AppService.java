package com.pivot.aham.api.web.app.service;

import com.pivot.aham.api.web.app.dto.reqdto.*;
import com.pivot.aham.api.web.app.dto.resdto.*;

/**
 * @author YYYz
 */
public interface AppService {

    LoginResDTO login(LoginDTO loginDTO);

    Integer forgetPwd(ForgetPwdDTO forgetPwdDTO);

    ClientInfoResDTO getClientInfo(ClientInfoDTO clientInfoDTO);

    AddGoalResDTO addGoal(AddGoalDTO addGoalDTO);

    DelGoalResDTO delGoal(DelGoalDTO delGoalDTO);

    WithdrawResDTO withdraw(WithdrawAlscDTO withdrawAlscDTO);

    WithdrawAlscResDTO withdrawalsc(WithdrawAlscDTO withdrawAlscDTO);

    BankInfoResDTO getBankList(BankInfoDTO bankInfoDTO);

    FundMyGoalListResDTO fundMyGoalList(FundMyGoalListDTO fundMyGoalListDTO);

    FundMyGoalResDTO fundMyGoal(GoalReqDTO goalReqDTO);

    ReferResDTO refer(ReferDTO clientInfoDTO);

    RegisterForFeResDTO register(RegisterForFeReqDTO registerReqDTO);

    ChangeRiskResDTO changeRisk(ChangeRiskReqDTO changeRiskReqDTO);

    UserStatementListResDTO getStatementList(UserStatementReqDTO userStatementReqDTO);

    String getPdfStatement(PdfStatementReqDTO pdfStatementReqDTO);

}
