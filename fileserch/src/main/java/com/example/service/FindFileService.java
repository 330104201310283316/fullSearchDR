package com.example.service;

import com.example.Entity.FileEntity;
import com.example.Entity.FileEntityList;


public interface FindFileService {
     /**
      * 查询文档内容
      * @param searchContent
      * @return FileEntityList
      */
     FileEntityList getFileEntity(String searchContent);

     /**
      *
      * @param filePath
      * @return
      */
     FileEntity getFileToHtmlUrl(String filePath);

     /**
      *
      * @param filePath
      * @param pwd
      * @return
      */
     Boolean checkPwd(String filePath ,String pwd);
}
