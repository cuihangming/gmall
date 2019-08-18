package com.atgmall.manageweb.controller;

import org.apache.commons.lang3.StringUtils;
import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @作者: 崔航铭 手机: 18704311678
 * @Email:1870431678@163.com
 * @date 2019/7/26 23:24
 */
@RestController
@CrossOrigin
public class FileUpLoadController {


    //Value取值，只要纳入到容器中，就可以取值
    @Value("${fileServer.url}")
    private String url;


    @PostMapping("fileUpload")                      //所上传的文件
    public String fileUpload(@RequestParam("file") MultipartFile file) throws IOException, MyException {
        String imgurl=url;
        if (file != null) {
            String Configfile = this.getClass().getResource("/tracker.conf").getFile();
            ClientGlobal.init(Configfile);
            TrackerClient trackerClient = new TrackerClient();
            TrackerServer trackerServer = trackerClient.getConnection();
            StorageClient storageClient = new StorageClient(trackerServer, null);
            String orginalFilename = "D://壁纸图片/320602.jpg";
            //获取文件名称
            String filename = file.getOriginalFilename();
            String s1 = StringUtils.substringAfterLast(filename, ".");

            String[] upload_file = storageClient.upload_file(file.getBytes(), s1, null);
            for (int i = 0; i < upload_file.length; i++) {
                String s = upload_file[i];
                imgurl+="/"+s;
                System.out.println("s = " + s);
            }
        }
        return imgurl;
    }

}
