/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sg.com.aham.finance.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import sg.com.aham.finance.model.UserAssetReport;

/**
 *
 * @author HP
 */
public class UserAssetMapper implements RowMapper<UserAssetReport> {

    @Override
    public UserAssetReport mapRow(ResultSet rs, int i) throws SQLException {

        UserAssetReport userAssetReport = new UserAssetReport();
        userAssetReport.setAccountId(rs.getLong("account_id"));
        userAssetReport.setClientId(rs.getInt("client_id"));
        userAssetReport.setProductCode(rs.getString("product_code"));
        userAssetReport.setShare(rs.getBigDecimal("share"));
        userAssetReport.setMoney(rs.getBigDecimal("money"));

        userAssetReport.setCreateTime(rs.getDate("create_time"));
        userAssetReport.setUpdateTime(rs.getDate("update_time"));
        userAssetReport.setGoalId(rs.getString("goal_id"));
        userAssetReport.setUsdToSgd(rs.getBigDecimal("usd_to_sgd"));
        userAssetReport.setRateDate(rs.getString("rate_date"));
        return userAssetReport;

    }

}
