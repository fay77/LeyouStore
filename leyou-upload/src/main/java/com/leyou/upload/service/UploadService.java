package com.leyou.upload.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

/**
 * Created by jing_tian on 2020/9/29.
 */
@Service
public class UploadService {
    private static final List<String> CONTENT_TYPES = Arrays.asList("image/gif" , "image/jpeg");

    private static final Logger LOGGER = LoggerFactory.getLogger(UploadService.class);


    public String uploadImage(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        //校验文件类型
        String contentType = file.getContentType();
        if (!CONTENT_TYPES.contains(contentType)) {
            LOGGER.info("文件类型不合法：{}", originalFilename);
            return null;
        }
        try {
            //校验文件内容
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            if (bufferedImage == null) {
                LOGGER.info("文件内容不合法：{}", originalFilename);
                return null;
            }
            //保存到文件的服务器
            file.transferTo(new File("/Users/hunliji/my_server_files/" + originalFilename));
            //返回url,进行回显
            return "http://image.leyou.com/" + originalFilename;
        } catch (IOException e) {
            LOGGER.info("服务器内部错误：{}", originalFilename);
            e.printStackTrace();
        }
        return null;
    }
}
