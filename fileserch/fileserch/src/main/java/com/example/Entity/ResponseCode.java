package com.example.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ResponseCode {

    SUCCESS("200", "请求成功!"),
    PWD_ERROR("201","密码错误"),
    TOHTML_ERROR("203", "转换html格式失败!"),
    BAD_REQUEST("400", "请求参数存在错误!"),
    UNAUTHORIZED("401", "当前用户状态为未登陆，请进行登录！"),
    LOGIN_TIME_OUT("407", "当前用户登录信息已过期，请重新登录！"),
    FORBIDDEN("403", "当前用户权限不足！"),
    NOT_FOUND("404", "请求的数据不存在!"),
    DATA_NOT_FOUND("504", "请求的数据不存在!"),
    INTERNAL_SERVER_ERROR("500", "服务器内部错误!"),
    FILE_SERVER_ERROR("500", "文件服务器异常!"),
    SERVICE_UNAVAILABLE("503", "服务器繁忙，请稍后再查询!"),
	CHECK_DATA_UN_PASS("405", "校验不通过!"),
    ;

    private String code;

    private String desc;

}
