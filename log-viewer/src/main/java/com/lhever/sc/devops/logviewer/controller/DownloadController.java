package com.lhever.sc.devops.logviewer.controller;

import com.lhever.sc.devops.core.utils.FileUtils;
import com.lhever.sc.devops.core.utils.ZipUtils;
import com.lhever.sc.devops.logviewer.utils.CommonUtils;
import com.lhever.sc.devops.logviewer.utils.DownloadUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.File;

/**
 * <p>
 * 类说明
 * </p>
 *
 * @author lihong10 2020/5/22 21:53
 * @version v1.0
 * @modificationHistory=========================逻辑或功能性重大变更记录
 * @modify by user: {修改人} 2020/5/22 21:53
 * @modify by reason:{方法名}:{原因}
 */
@RestController
public class DownloadController {

    @RequestMapping(path = "download", method = RequestMethod.GET)
    @ResponseBody
    public void download(HttpServletResponse response, String serviceName, String fileName) throws Exception {
        String logBasePath = CommonUtils.getLogBasePath(serviceName);

        String filePath = "" + FileUtils.trimTail(logBasePath) + "/" + fileName;
        if (!FileUtils.fileExists(filePath)) {
            DownloadUtils.write(response, "no such file");
            return;
        }
        DownloadUtils.downLoad(new File(filePath), fileName, response);
    }


    @RequestMapping(path = "view", method = RequestMethod.GET, produces = "text/plain")
    @ResponseBody
    public synchronized void view(HttpServletResponse response, String serviceName, String fileName) throws Exception {
        String logBasePath = CommonUtils.getLogBasePath(serviceName);
        String filePath = "" + FileUtils.trimTail(logBasePath) + "/" + fileName;
        if (!FileUtils.fileExists(filePath)) {
            DownloadUtils.write(response, "文件不存在");
        }

        File maybeZip = new File(filePath);
        if (maybeZip.getName().endsWith(".zip")) {
            String maybeLog = maybeZip.getAbsolutePath().substring(0, maybeZip.getAbsolutePath().length() - 4);
            File mybaelogFile = new File(maybeLog);
            boolean unzip = false;
            if (!mybaelogFile.exists()) {
                ZipUtils.unZip(maybeZip, logBasePath);
                unzip = true;
            }
            if (mybaelogFile.exists()) {
                DownloadUtils.write(mybaelogFile, response);
                if (unzip) { //说明文件是解压出来的，删除
                    FileUtils.delete(mybaelogFile);
                }
                return;
            }
        } else if (maybeZip.length() / 1024 / 1024 <= 10) {
            DownloadUtils.write(maybeZip, response);
            return;
        }
        DownloadUtils.write(response, "该文件不可访问");
    }


}
