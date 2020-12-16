package com.example.controller;

import com.example.Entity.FileEntity;
import com.example.Entity.FileEntityList;
import com.example.Entity.ResponseCode;
import com.example.Entity.ServerResponse;
import com.example.service.impl.FindFileServiceImpl;
import com.example.util.IpUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/file")
@CrossOrigin
public class FindFileController {

    private final static String ENCODE = "UTF-8";

    @Resource
    private FindFileServiceImpl findFileService;

    @ResponseBody
    @RequestMapping("/findFile")
    public ServerResponse findFile(String searchContent, HttpServletRequest request) {

        //获取访问者ip
        System.out.println();
        System.out.println(IpUtil.getIpAddr(request));
        FileEntityList list = null;
        try {
            if (searchContent != null && !searchContent.equals("")) {
                System.out.println("搜索内容=====》》》" + searchContent);
                list = findFileService.getFileEntity(searchContent);
            }
        } catch (Exception e) {
            return ServerResponse.failOfCodeAndMsg(ResponseCode.INTERNAL_SERVER_ERROR.getCode(), "系统内部异常");
        }

        return ServerResponse.successOfData(list);
    }

    @ResponseBody
    @RequestMapping(value = "/checkFilePwd", method = RequestMethod.POST)
    public ServerResponse checkFilePwd(String filePath, String pwd) {
        String filePathUrl = null;
        try {
            filePathUrl = java.net.URLDecoder.decode(filePath, ENCODE);
            if (pwd != null && !pwd.equals(" ") && filePath != null && !filePath.equals(" ")) {
                if (findFileService.checkPwd(filePathUrl, pwd)) {
                    System.out.println("文件路径" + filePath + ",文件密码：  " + pwd);
                    return ServerResponse.success();
                }
            } else {
                System.out.println("开始转换" + filePathUrl);
                if (findFileService.getFileToHtmlUrl(filePathUrl) == null) {
                    return ServerResponse.fail(ResponseCode.TOHTML_ERROR);
                }
                return ServerResponse.success();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.failOfCodeAndMsg(ResponseCode.INTERNAL_SERVER_ERROR.getCode(), "系统内部异常");
        }
        return ServerResponse.fail(ResponseCode.PWD_ERROR);
    }

    @ResponseBody
    @RequestMapping("/findFileToHtmlUrl")
    public ServerResponse findFileToHtmlUrl(String filePath) {
        String filePathUrl = "";
        FileEntity fileEntity = null;
        try {
            filePathUrl = java.net.URLDecoder.decode(filePath, ENCODE);
            fileEntity = findFileService.getFileToHtmlUrl(filePathUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.failOfCodeAndMsg(ResponseCode.INTERNAL_SERVER_ERROR.getCode(), "系统内部异常");
        }
        return ServerResponse.successOfData(fileEntity);
    }
}
