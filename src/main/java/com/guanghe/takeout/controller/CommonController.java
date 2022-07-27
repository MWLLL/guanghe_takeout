package com.guanghe.takeout.controller;

import com.guanghe.takeout.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * 处理文件上传和下载
 */
@RestController
@Slf4j
@RequestMapping("/common")
public class CommonController {

    @Value("${guanghe.path}")
    private String basePath;

    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) throws IOException {
        log.info("上传菜品图片...");
        //file是一个临时文件，需要转存到指定位置，否则本次请求完成后临时文件会删除

        String originalName = file.getOriginalFilename();//原始文件名
        String suffix = originalName.substring(originalName.lastIndexOf("."));
        //使用UUID生成文件名，防止文件名重复
        String fileName  = UUID.randomUUID().toString()+suffix;//xxx.jpg
        //创建目录
        File dir = new File(basePath);
        //判断当前目录是否存在
        if (!dir.exists()){
            //目录不存在需要创建
            dir.mkdirs();
        }
        //文件存储到指定位置
        file.transferTo(new File(basePath+fileName));
        //返回文件名称给前端
        return R.success(fileName);
    }

    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) throws IOException{
        log.info("下载菜品图片...");
        //输入流，通过输入流读取文件内容
        FileInputStream fileInputStream = new FileInputStream(new File(basePath+name));

        //输出流，通过输出流写回浏览器
        ServletOutputStream outputStream = response.getOutputStream();
        //设置返回类型
        response.setContentType("image/jpeg");

        int len=0;
        byte[] bytes = new byte[1024];
        while ((len=fileInputStream.read(bytes)) != -1){
            outputStream.write(bytes,0,len);
            outputStream.flush();
        }

        //关闭资源
        outputStream.close();
        fileInputStream.close();

    }
}
