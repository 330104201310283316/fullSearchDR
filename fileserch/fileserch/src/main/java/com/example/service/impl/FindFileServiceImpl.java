package com.example.service.impl;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import com.example.Entity.FileEntity;
import com.example.Entity.FileEntityList;
import com.example.service.FindFileService;
import com.example.util.LunceneUtil;
import com.example.util.ToHtml;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FindFileServiceImpl implements FindFileService {

    @Value("${webApi.url}")
    private String webApiUrl;

    @Value("${file.temp}")
    private String FileTemp;

    @Value("${file.index}")
    private String INDEX_DIR;

    @Value("${file.Source_DIR}")
    private String Source_DIR;

    @Value("${webApi.img}")
    private String webApiImg;

    // 使用正则表达式, 匹配特殊字符
    private static final Pattern pattern = Pattern.compile("[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]");

    @Override
    public FileEntityList getFileEntity(String searchContent) {
        Matcher searchContentMth = pattern.matcher(searchContent);
        FileEntityList fileEntityList = new FileEntityList();
        //传入索引生成的目录
        File fileIndex = new File(INDEX_DIR);
        try {
            fileIndex.mkdir();
            //创建索引
            LunceneUtil.creatIndex(INDEX_DIR, Source_DIR);
            //传参查询
            fileEntityList = LunceneUtil.searchIndex(searchContentMth.replaceAll(" ").trim(), INDEX_DIR, webApiImg);
        } catch (Exception e) {
            System.out.println("抛出异常");
        }

        return fileEntityList;
    }

    @Override
    public FileEntity getFileToHtmlUrl(String filePath) {
        FileEntity fileEntity = new FileEntity();
        File fileTemp = new File(FileTemp);
        fileTemp.mkdir();
        String fileStrUrl = ToHtml.toHtml(filePath, FileTemp);
        if (fileStrUrl == null) {
            return null;
        }
        fileEntity.setFileUrl(webApiUrl + fileStrUrl);
        return fileEntity;
    }

    @Override
    public Boolean checkPwd(String filePath, String pwd) {
        return ToHtml.checkPwd(filePath, pwd);
    }

    public static void main(String[] args) {
        //传参查询
        System.out.println(ToHtml.checkPwd("D:\\luceneSource\\c\\新建 DOCX 文档_lock.docx", "123"));
    }

}
