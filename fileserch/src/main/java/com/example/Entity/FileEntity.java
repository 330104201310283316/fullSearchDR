package com.example.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.awt.*;
import java.io.File;
import java.io.Serializable;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class FileEntity implements Serializable {




    /**
     * 唯一标识id
     */
    private String rId;

    /**
     * 返回输入内容
     **/
    private String fileContent;

    /**
     * 返回文件名字
     **/
    private String fileName;

    /**
     * 返回文件路径
     **/
    private String filePath;

    /**
     * 返回的文件流
     */
    private String fileUrl;

    /**
     * 返回图片
     */
    private String img;

    /**
     * 文件是否被加密
     * 0无，1加密
     */
    private String isLock;
}
