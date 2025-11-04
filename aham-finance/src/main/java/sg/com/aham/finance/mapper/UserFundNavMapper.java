/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sg.com.aham.finance.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import sg.com.aham.finance.model.*;

/**
 *
 * @author HP
 */
public class UserFundNavMapper implements RowMapper<UserFundNavReport> {

    @Override
    public UserFundNavReport mapRow(ResultSet rs, int i) throws SQLException {
        UserFundNavReport userFundNavReport = new UserFundNavReport();
        userFundNavReport.setAccountId(rs.getLong("account_id"));
        userFundNavReport.setClientId(rs.getInt("client_id"));
        userFundNavReport.setGoalId(rs.getString("goal_id"));
        userFundNavReport.setFundNav(rs.getBigDecimal("fund_nav"));
        userFundNavReport.setTotalShare(rs.getBigDecimal("total_share"));
        userFundNavReport.setTotalAsset(rs.getBigDecimal("total_asset"));
        userFundNavReport.setNavTime(rs.getDate("nav_time"));
        userFundNavReport.setCreateTime(rs.getDate("create_time"));
        userFundNavReport.setUpdateTime(rs.getDate("update_time"));
        userFundNavReport.setUsdToSgd(rs.getBigDecimal("usd_to_sgd"));
        userFundNavReport.setRateDate(rs.getString("rate_date"));
        return userFundNavReport;
    }
}
