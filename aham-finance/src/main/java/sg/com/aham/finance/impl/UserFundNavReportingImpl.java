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
import sg.com.aham.finance.dao.UserFundNavReportingDao;
import sg.com.aham.finance.mapper.UserFundNavMapper;
import sg.com.aham.finance.model.UserFundNavReport;

/**
 *
 * @author HP
 */
@Slf4j
@Service
public class UserFundNavReportingImpl implements UserFundNavReportingDao {

    JdbcTemplate jdbcTemplate;

    private final String MYSQL = "SELECT nav.account_id, nav.client_id, nav.goal_id, nav.fund_nav, nav.total_share, nav.total_asset, nav.nav_time, nav.create_time,  "
            + "nav.update_time, (SELECT usd_to_sgd FROM t_exchange_rate z WHERE EXCHANGE_RATE_TYPE = 2 AND z.rate_date  = DATE_FORMAT(nav.create_time , '%Y-%m-%d')) AS USD_TO_SGD, "
            + " DATE_FORMAT(nav.CREATE_TIME, '%Y-%m-%d') AS RATE_DATE FROM t_user_fund_nav nav WHERE DATE_FORMAT(nav.create_time , '%Y-%m') = ? ";

    @Override
    public List<UserFundNavReport> getUserFundNavReportingByDate(String date) {
        log.info("MYSQL {} ", MYSQL);
        return jdbcTemplate.query(MYSQL, new Object[]{date}, new UserFundNavMapper());
    }

    @Autowired
    public UserFundNavReportingImpl(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

}
