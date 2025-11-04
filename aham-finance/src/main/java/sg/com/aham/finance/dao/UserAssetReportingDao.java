/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sg.com.aham.finance.dao;

import java.util.List;
import sg.com.aham.finance.model.UserAssetReport;

/**
 *
 * @author HP
 */
public interface UserAssetReportingDao {

    List<UserAssetReport> getUserAssetReportingByDate(String date);

}
