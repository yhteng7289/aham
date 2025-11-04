/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sg.com.aham.finance.impl;

import java.util.List;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import sg.com.aham.finance.dao.UserAssetReportingDao;
import sg.com.aham.finance.mapper.UserAssetMapper;
import sg.com.aham.finance.model.UserAssetReport;

/**
 *
 * @author HP
 */
@Slf4j
@Service
public class UserAssetReportingImpl implements UserAssetReportingDao {

    JdbcTemplate jdbcTemplate;

    private final String MYSQL = "SELECT asset.ACCOUNT_ID, asset.CLIENT_ID, asset.PRODUCT_CODE, asset.SHARE, asset.MONEY, asset.CREATE_TIME, asset.UPDATE_TIME, "
            + "asset.GOAL_ID, (SELECT usd_to_sgd FROM t_exchange_rate z WHERE EXCHANGE_RATE_TYPE = 2 AND z.rate_date  = DATE_FORMAT(asset.create_time , '%Y-%m-%d')) AS USD_TO_SGD, "
            + " DATE_FORMAT(asset.CREATE_TIME, '%Y-%m-%d') AS RATE_DATE FROM t_user_asset asset WHERE DATE_FORMAT(asset.create_time , '%Y-%m') = ? ";

    @Override
    public List<UserAssetReport> getUserAssetReportingByDate(String date) {
        log.info("MYSQL {} ", MYSQL);
        return jdbcTemplate.query(MYSQL, new Object[]{date}, new UserAssetMapper());
    }

    @Autowired
    public UserAssetReportingImpl(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

}
