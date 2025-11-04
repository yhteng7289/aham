
import java.io.IOException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.math.RoundingMode;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author HP
 */
public class Good1 {

    public static void main(String args[]) throws IOException, Exception {

        BigDecimal a = new BigDecimal(100.025).setScale(2, BigDecimal.ROUND_DOWN);
        System.out.println(new BigDecimal(522.163).setScale(2, BigDecimal.ROUND_DOWN));
        System.out.println(new BigDecimal(522.165).setScale(2, BigDecimal.ROUND_UP));
        System.out.println(new BigDecimal(522.168).setScale(2, BigDecimal.ROUND_UP));

        System.out.println(new BigDecimal(522.8856206628477905).setScale(2, BigDecimal.ROUND_DOWN));
        System.out.println(new BigDecimal(522.8886206628477905).setScale(2, BigDecimal.ROUND_DOWN));
        System.out.println(new BigDecimal(522.8836206628477905).setScale(2, BigDecimal.ROUND_UP));
        System.out.println(new BigDecimal(522.8836206628477905).setScale(2, BigDecimal.ROUND_CEILING));
        System.out.println(new BigDecimal(522.8836206628477905).setScale(2, BigDecimal.ROUND_FLOOR));

        String z = null;
        try {

            try {
                z.trim();
            } catch (Exception e) {
                System.out.println("1st Exception");
            }

        } catch (Exception e) {
            System.out.println("2nd Exception");
        }

//        String path = "D:/data/PortLevel/";
//        for (int i = 1; i <= 3; i++) {
//            for (int j = 1; j <= 5; j++) {
//                for (int k = 1; k <= 5; k++) {
//                    String fileName = "POOL" + i + "_NAV_Tracking_Risk" + j + "_Age" + k + ".csv";
//                    String fullPath = path + fileName;
//                    File file = new File(fullPath);
//                    BufferedReader br = null;
//                    String line = "";
//                    String cvsSplitBy = ",";
//
//                    try {
//
//                        br = new BufferedReader(new FileReader(fullPath));
//                        while ((line = br.readLine()) != null) {
//                            if (line.contains("20200310")) {
//                                String portFolio = "P" + i + "R" + j + "A" + k;
//                                System.out.println(line + ",'" + portFolio + "'");
//                            }
//                        }
//
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    } finally {
//                        if (br != null) {
//                            try {
//                                br.close();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                }
//            }
//        }
    }
}
