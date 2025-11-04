package com.pivot.aham.common.core.support.file.ftp;

import com.pivot.aham.common.core.exception.FtpException;
import com.pivot.aham.common.core.util.PropertiesUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Java Secure Channel
 *
 * @author addison
 * @version 2016年5月20日 下午3:19:19
 */
public class SftpClient {

    private final Logger logger = LogManager.getLogger();
    private Session session = null;
    private ChannelSftp channel = null;

    private static SftpClient sftpClientPivot;

    private static SftpClient sftpClientSaxo;

    private Session saxoSession = null;
    private ChannelSftp saxoChannel = null;

    private SftpClient() {
    }

    public static SftpClient connect(String host, int port, String userName, String password, Integer timeout, Integer aliveMax) {
        return new SftpClient().init(host, port, userName, password, timeout, aliveMax);
    }

    public static SftpClient connect() {
        return new SftpClient().init();
    }

    public static SftpClient saxoConnect() {
        return new SftpClient().saxoInit();
    }
    
    public static SftpClient connectUOB() {
        return new SftpClient().initUOB();
    }

    public SftpClient init(String host, int port, String userName, String password, Integer timeout, Integer aliveMax) {
        try {

            JSch jsch = new JSch(); // 创建JSch对象
            session = jsch.getSession(userName, host, port); // 根据用户名，主机ip，端口获取一个Session对象
            if (password != null) {
                session.setPassword(password); // 设置密码
            }
            session.setConfig("StrictHostKeyChecking", "no"); // 为Session对象设置properties
            if (timeout != null) {
                session.setTimeout(timeout); // 设置timeout时间
            }
            if (aliveMax != null) {
                session.setServerAliveCountMax(aliveMax);
            }
            session.connect(); // 通过Session建立链接

            if (session.isConnected()) {
                logger.info("SFTP connected");
                channel = (ChannelSftp) session.openChannel("sftp"); // 打开SFTP通道
                logger.info("SFTP open channel");
                channel.connect(); // 建立SFTP通道的连接
                if (channel.isConnected()) {
                    logger.info("SSH Channel connected.");
                } else {
                    throw new FtpException("SSH Channel is not connected");
                }
            } else {
                throw new FtpException("SFTP is not connected");
            }
        } catch (JSchException e) {
            throw new FtpException("", e);
        }
        return this;
    }

    public SftpClient init() {
        String host = PropertiesUtil.getString("sftp.host");
        int port = PropertiesUtil.getInt("sftp.port");
        String userName = PropertiesUtil.getString("sftp.user.name");
        String password = PropertiesUtil.getString("sftp.user.password");
        Integer timeout = PropertiesUtil.getInt("sftp.timeout");
        Integer aliveMax = PropertiesUtil.getInt("sftp.aliveMax");
        return init(host, port, userName, password, timeout, aliveMax);
    }

    public void disconnect() {
        try {
            channel.disconnect();
            logger.info("Channel disconnected.");
        } catch (Exception e) {

        } finally {
            channel = null;
        }

        try {
            session.disconnect();
            logger.info("Session disconnected.");
        } catch (Exception e) {

        } finally {
            session = null;
        }
        try {
            sftpClientPivot.disconnect();
            logger.info("Connection disconnected.");
        } catch (Exception e) {

        } finally {
            sftpClientPivot = null;
        }
    }

    /**
     * 发送文件
     */
    public void put(String src, String dst) {
        try {
            channel.put(src, dst, new FileProgressMonitor());
        } catch (SftpException e) {
            throw new FtpException("", e);
        }
    }

    public void put(String FileName, String directory, InputStream input) throws SftpException {
        try {
            if (!channel.isConnected()) {
                try {
                    channel.connect();
                } catch (JSchException e) {
                    logger.warn("sftp connect fail");
                }
            }
            channel.cd(directory);
        } catch (SftpException e) {
            logger.warn("directory is not exist");
            channel.mkdir(directory);
            channel.cd(directory);
        }
        channel.put(input, FileName);
        logger.info("file:{} is upload successful", FileName);

    }

    /**
     * 获取文件
     */
    public void get(String src, String dst) {
        try {
            channel.get(src, dst, new FileProgressMonitor());
        } catch (SftpException e) {
            throw new FtpException("", e);
        }
    }

    /**
     * 读取SFTP文件
     *
     * @param src
     * @return
     */
    public InputStream get(String src) {
        InputStream inputStream = null;
        try {
            if (channel.isConnected()) {
                inputStream = channel.get(src);
            }
        } catch (SftpException e) {
            logger.error("src进行sftp文件读取异常:", e);
            throw new FtpException("", e);
        }
        return inputStream;
    }

    public SftpClient saxoInit() {
        try {

            String host = PropertiesUtil.getString("sftp.saxo.host");
            int port = PropertiesUtil.getInt("sftp.saxo.port");
            String userName = PropertiesUtil.getString("sftp.saxo.user.name");
            String password = PropertiesUtil.getString("sftp.saxo.user.password");
            Integer timeout = PropertiesUtil.getInt("sftp.saxo.timeout");
            Integer aliveMax = PropertiesUtil.getInt("sftp.aliveMax");

//            String host = "127.0.0.1";
//            int port = 22;
//            String userName = "jinling.cui";
//            String password = "1qaz@WSX";
//            Integer timeout = 5000;
//            Integer aliveMax = 10;
            JSch jsch = new JSch(); // 创建JSch对象
            saxoSession = jsch.getSession(userName, host, port); // 根据用户名，主机ip，端口获取一个Session对象
            if (password != null) {
                saxoSession.setPassword(password); // 设置密码
            }
            saxoSession.setConfig("StrictHostKeyChecking", "no"); // 为Session对象设置properties
            if (timeout != null) {
                saxoSession.setTimeout(timeout); // 设置timeout时间
            }
            if (aliveMax != null) {
                saxoSession.setServerAliveCountMax(aliveMax);
            }
            saxoSession.connect(); // 通过Session建立链接
            saxoChannel = (ChannelSftp) saxoSession.openChannel("sftp"); // 打开SFTP通道
            saxoChannel.connect(); // 建立SFTP通道的连接
            logger.info("SSH Channel connected.");
        } catch (JSchException e) {
            throw new FtpException("", e);
        }
        return this;
    }

    /**
     * 读取SFTP文件
     *
     * @param src
     * @return
     */
    public List<String> getsaxoFile(String src, boolean deleteFlag) {
        List<String> lines = null;
        InputStream inputStream = null;
        try {
            inputStream = saxoChannel.get(src);
            lines = IOUtils.readLines(inputStream, "GBK");
            if (deleteFlag && CollectionUtils.isNotEmpty(lines)) {
                //删除表头
                lines.remove(0);
            }
            inputStream.close();
        } catch (SftpException e) {
            logger.error("进行sftp文件读取异常:", e);
            throw new FtpException("", e);
        } catch (IOException io) {
            logger.error("进行sftp文件io异常:", io);
            throw new FtpException("", io);
        } catch (Exception e) {
            logger.error("进行sftp文件读取异常:", e);
            throw new FtpException("", e);
        } finally {
            saxoChannel.disconnect();
            saxoSession.disconnect();
        }
        return lines;
    }

//    /**
//     * 读取FTP的文件内容
//     *
//     * @param fileUrl
//     * @return
//     */
//    public List<String> readFileContent(String fileUrl) {
//        return readFileContent(fileUrl, false);
//    }
//    /**
//     * @param url
//     * @param holdTitle 是否保存表头
//     * @return
//     */
//    public List<String> readFileContent(String url, boolean holdTitle) {
//        List<String> lines = null;
//        try {
//            String rootPath = PropertiesUtil.getString("mnt_path_financial_model");
//            if (rootPath == null || rootPath.trim().length() < "/mnt/".length()) {
//                throw new BusinessException("illegal path: " + "" + rootPath + url);
//            }
//            String absoluteFilename = "" + rootPath + url;
//            logger.info("从Ftp上读取文件路径:{}", absoluteFilename);
//            lines = IOUtils.readLines(get(absoluteFilename), "GBK");
//            if (!holdTitle && CollectionUtils.isNotEmpty(lines)) {
//                //删除表头
//                lines.remove(0);
//            }
//        } catch (Exception e) {
//            logger.error("取挂载文件出错,错误信息:{}", ExceptionUtils.getFullStackTrace(e));
//            try {
//                String contactEmail = PropertiesUtil.getString("modelException_contactEmail");
//                Email email = new Email()
//                        .setTemplateName("ExceptionEmail")
//                        .setTemplateVariables(InstanceUtil.newHashMap("exMsg", ExceptionUtils.getFullStackTrace(e)))
//                        .setSendTo(contactEmail)
//                        .setTopic("读取挂载文件出错");
//                EmailUtil.sendEmail(email);
//            } catch (Exception e1) {
//                logger.error("发送读取挂载文件出错错误邮件失败:{}", ExceptionUtils.getFullStackTrace(e));
//            }
//        } finally {
//            disconnect();
//        }
//        return lines;
//    }
    
    
       public SftpClient initUOB() {
        try {
            
            String host = "u1-fts.uob.com.sg";
            int port = 8022;
            String userName = "RFTS00117A";
            String password = null;
            //Integer timeout = PropertiesUtil.getInt("sftp.saxo.timeout");
            //Integer aliveMax = PropertiesUtil.getInt("sftp.aliveMax");

            JSch jsch = new JSch(); // 创建JSch对象
            jsch.addIdentity("//root//sshkey_sftp");
            session = jsch.getSession(userName, host, port); // 根据用户名，主机ip，端口获取一个Session对象
            if (password != null) {
                session.setPassword(password); // 设置密码
            }
            session.setConfig("StrictHostKeyChecking", "no"); // 为Session对象设置properties
            /*if (timeout != null) {
                session.setTimeout(timeout); // 设置timeout时间
            }
            if (aliveMax != null) {
                session.setServerAliveCountMax(aliveMax);
            }*/
            session.connect(); // 通过Session建立链接

            if (session.isConnected()) {
                logger.info("SFTP UOB connected");
                channel = (ChannelSftp) session.openChannel("sftp"); // 打开SFTP通道
                logger.info("SFTP UOB open channel");
                channel.connect(); // 建立SFTP通道的连接
                if (channel.isConnected()) {
                    logger.info("SSH UOB Channel connected.");
                } else {
                    throw new FtpException("UOB SSH Channel is not connected");
                }
            } else {
                throw new FtpException("UOB SFTP is not connected");
            }
        } catch (JSchException e) {
            throw new FtpException("", e);
        }
        return this;
    }
       
    public void getUOBFile(String frmDst, String toDst) {
        
        logger.info("getUOBFile file Read >>");
        try {
            if (channel.isConnected()) {
                logger.info("Channel isConnected >>");
                //channel.get("/SG/OUT/VA5_3523095739_110220.pgp", "/root/VA5_3523095739_110220.pgp");
                channel.get(frmDst, toDst); 
                channel.exit();
                session.disconnect();
                logger.info("Completed Channel isConnected >>");
            }
         } catch (SftpException e) {
            logger.error("src进行sftpUOB文件读取异常:", e);
            throw new FtpException("", e);
        }

    }
    
        public void putUOBFile(String frmDst, String toDst) {
        
        logger.info("putUOBFile file Read >>");
        try {
            if (channel.isConnected()) {
                logger.info("Channel isConnected >>");
                //channel.put("/root/UVAI070201.txt.pgp", "/SG/IN/UVAI070201.txt.pgp");  
                channel.put(frmDst, toDst); 
                channel.exit();
                session.disconnect();
                logger.info("Completed Channel isConnected >>");
            }
         } catch (SftpException e) {
            logger.error("src进行sftpUOB文件读取异常:", e);
            throw new FtpException("", e);
        }

    }
}
