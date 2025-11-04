package com.pivot.aham.common.core.support.file.ftp;

import com.pivot.aham.common.core.exception.MessageException;
import com.pivot.aham.common.core.util.ErrorLogAndMailUtil;
import com.pivot.aham.common.core.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystem;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.List;

/**
 * ftputils
 *
 * @author addison
 */
@Slf4j
public class FTPClientUtil {

    private static FTPClient ftpClient;

    public static FTPClientUtil connect() {
        return new FTPClientUtil().init();
    }

    private FTPClientUtil init() {
        ftpClient = getFTPClient();
        return this;
    }

    public void putFtp(InputStream inputStream, String fileName) {
        String ftpPath = PropertiesUtil.getString("sftp.baseDir");
        boolean result = false;
        try {
            int reply;
            reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                throw new MessageException("上传失败");
            }
            ftpClient.changeWorkingDirectory("/");
            //切换到上传目录
            if (!ftpClient.changeWorkingDirectory(ftpPath)) {
                //如果目录不存在创建目录
                String[] dirs = ftpPath.split("/");
                for (String dir : dirs) {
                    if (null == dir || "".equals(dir)) {
                        continue;
                    }
                    ftpClient.makeDirectory(dir);
                    ftpClient.changeWorkingDirectory(dir);
                }
            }
            ftpClient.enterLocalPassiveMode();
            //设置上传文件的类型为二进制类型
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            //上传文件 file最好写全路径
            ftpClient.storeFile(fileName, inputStream);
            inputStream.close();
            ftpClient.logout();
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                } catch (IOException ioe) {
                }
            }
        }

    }

    public static FTPClient getFTPClient() {

        String host = PropertiesUtil.getString("sftp.host");
        int port = PropertiesUtil.getInt("sftp.port");
        String userName = PropertiesUtil.getString("sftp.user.name");
        String password = PropertiesUtil.getString("sftp.user.password");
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient = new FTPClient();
            ftpClient.connect(host, port);// 连接FTP服务器
            ftpClient.login(userName, password);// 登陆FTP服务器
            if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                System.out.println("未连接到FTP，用户名或密码错误。");
                ftpClient.disconnect();
            } else {
                System.out.println("FTP连接成功。");
            }
        } catch (SocketException e) {
            e.printStackTrace();
            System.out.println("FTP的IP地址可能错误，请正确配置。");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("FTP的端口错误,请正确配置。");
        }
        return ftpClient;
    }

    /**
     * 读取FTP文件内容列表
     */
    public static List<String> readFileContent(String url) {
        return readFileContent(url, false);
    }

    public static List<String> readFileContent(String url, boolean holdTitle) {
        List<String> lines = null;
        FileSystemManager manager = null;
        FileSystem fileSystem = null;
        try {
            manager = VFS.getManager();
//            url = getConnectionUrl(url, useProd);
            FileObject fileObject = manager.resolveFile(url);
            log.info("readFileContent ---->>>>  url : " + url);
            fileSystem = fileObject.getFileSystem();
            lines = IOUtils.readLines(fileObject.getContent().getInputStream(), "UTF-8");
            if (!holdTitle && CollectionUtils.isNotEmpty(lines)) {
                //删除表头
                lines.remove(0);
            }
        } catch (IOException e) {
            ErrorLogAndMailUtil.logError(log, e);
        } finally {
            if (manager != null && fileSystem != null) {
                manager.closeFileSystem(fileSystem);
            }
        }
        return lines;
    }

    public static InputStream getFtpInputStream(String url) {
        FileSystemManager manager = null;
        FileSystem fileSystem = null;
        InputStream inputStream = null;
        try {
            manager = VFS.getManager();
//            url = getConnectionUrl(url, useProd);
            FileObject fileObject = manager.resolveFile(url);
            fileSystem = fileObject.getFileSystem();
            inputStream = fileObject.getContent().getInputStream();
        } catch (IOException e) {
            ErrorLogAndMailUtil.logError(log, e);
        } finally {
            if (manager != null && fileSystem != null) {
                manager.closeFileSystem(fileSystem);
            }
        }
        return inputStream;
    }

    public static OutputStream getFtpOutPutStream(String url) {
        FileSystemManager manager = null;
        FileSystem fileSystem = null;
        OutputStream outputStream = null;
        try {
            manager = VFS.getManager();
//            url = getConnectionUrl(url, useProd);
            FileObject fileObject = manager.resolveFile(url);
            fileSystem = fileObject.getFileSystem();
            if (!fileObject.exists()) {
                fileObject.createFile();
            }
            outputStream = fileObject.getContent().getOutputStream();
        } catch (IOException e) {
            ErrorLogAndMailUtil.logError(log, e);
        } finally {
            if (manager != null && fileSystem != null) {
                manager.closeFileSystem(fileSystem);
            }

        }
        return outputStream;
    }

//    /**
//     * ftp.serverAddress=ftp.jimubox.com
//     * ftp.port=21
//     * ftp.uname=xuanji_portfolio_test
//     * ftp.password=d93hhzA1c1bpLnu
//     *
//     * @param filename
//     * @return
//     */
//    public static String getConnectionUrl(String filename, boolean useProd) {
//        String realName;
//        String passWord;
//        if (PropertiesUtil.isProd()) {
//            realName = PropertiesUtil.getString("ftp.uname");
//            passWord = PropertiesUtil.getString("ftp.password");
//        } else {
//            realName = useProd ? PropertiesUtil.getString("ftp.uname.prod") : PropertiesUtil.getString("ftp.uname");
//            passWord = useProd ? PropertiesUtil.getString("ftp.password.prod") : PropertiesUtil.getString("ftp.password");
//        }
//
//        return "ftp://" + realName + ":" + passWord +
//                "@" + PropertiesUtil.getString("ftp.serverAddress") + ":" + PropertiesUtil.getString("ftp.port") +
//                filename;
////        return "ftp://xuanji_portfolio_test:d93hhzA1c1bpLnu@ftp.jimubox.com:21" + filename;
//    }
}
