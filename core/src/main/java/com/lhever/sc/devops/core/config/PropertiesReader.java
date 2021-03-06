package com.lhever.sc.devops.core.config;

import com.lhever.sc.devops.core.utils.StringUtils;
import com.lhever.sc.devops.core.utils.IOUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Properties;


public class PropertiesReader extends AbstractPropertiesReader {

    private String fileName;

    /**
     * @param fileName 要加载的properties文件名, 必要的话可加上路径
     * @author lihong10 2015-4-14 上午11:19:41
     * @since v1.0
     */
    public PropertiesReader(String fileName, boolean outside) {
        this.props = new Properties();
        this.fileName = fileName;

        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        try {
            if (outside) {
//                inputStream = getInputStreamByFile(fileName);
                //解决中文乱码的问题，采用InputStreamReader.
                inputStreamReader = getInputStreamReaderByFile(fileName);
                props.load(inputStreamReader);
            } else {
                inputStream = getInputStream(Thread.currentThread().getContextClassLoader(), fileName);
                if (inputStream == null) {
                    inputStream = getInputStream(PropertiesReader.class.getClassLoader(), fileName);
                }
                if (inputStream == null) {
                    inputStream = new ClassPathResource(fileName).getInputStream();
                }
                props.load(inputStream);
            }
        } catch (Exception ex) {
//            LOG.error("找不到配置文件: " + fileName, ex);
            throw new RuntimeException("找不到配置文件: " + fileName, ex);
        } finally {
            IOUtils.closeQuietly(inputStream, inputStreamReader);
        }
    }

    public PropertiesReader(Properties props) {
        super(props);
    }

    public static InputStream getInputStreamByFile(String path) {
        File file = new File(path);
        if (!file.exists() || !file.isFile()) {
            throw new IllegalArgumentException("文件" + path + "不存在");
        }

        InputStream in = null;
        try {
            in = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return in;
    }

    public static InputStreamReader getInputStreamReaderByFile(String path) {
        File file = new File(path);
        if (!file.exists() || !file.isFile()) {
            throw new IllegalArgumentException("文件" + path + "不存在");
        }

        InputStreamReader in = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            in = new InputStreamReader(fis, Charset.forName("UTF-8"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return in;
    }


    public static InputStream getInputStream(ClassLoader classLoader, String fileName) {
        if (classLoader == null || StringUtils.isBlank(fileName)) {
            LOG.info("classLoader is null or fileName is null");
            return null;
        }

        fileName = fileName.trim();

        InputStream stream = null;
        try {
            stream = classLoader.getResourceAsStream(fileName);
        } catch (Exception e) {
            LOG.error("read " + fileName + " error", e);
        }

        if (stream == null && !fileName.startsWith("/")) {
            try {
                stream = classLoader.getResourceAsStream("/" + fileName);
            } catch (Exception e) {
                LOG.error("read /" + fileName + " error", e);
            }
        }
        return stream;
    }


    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }


}
