package com.example.Entity;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mockito.internal.matchers.Null;

/**
 * 服务端通用返回类
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL) //返回前端的时候忽略字段属性为null的字段
public class ServerResponse<T> {

    /*状态码*/
    private String state;

    /*返回的提示*/
    private String msg;

    /*返回的数据内容*/
    private T data;

    private ServerResponse(String status) {
        this.state = status;
    }

    private ServerResponse(String state, String msg) {
        this.state = state;
        this.msg = msg;
    }

    private ServerResponse(String state, T data) {
        this.state = state;
        this.data = data;
    }

    /* 只返回200状态码，如登出时，既不需要提示，也不需要返回任何数据*/
    public static ServerResponse<Null> success() {
        return new ServerResponse<>(ResponseCode.SUCCESS.getCode());
    }

    /* 返回200状态码和数据, 如请求表格数据*/
    public static <T> ServerResponse<T> successOfData(T data) {
        return new ServerResponse<>(ResponseCode.SUCCESS.getCode(), data);
    }

    /* 返回200状态码和状态信息，如保存成功时，前端提示：保存成功！*/
    public static ServerResponse<String> successOfMsg(String msg) {
        return new ServerResponse<>(ResponseCode.SUCCESS.getCode(), msg);
    }

    /* 返回内置的符合 http 规范的失败信息*/
    public static ServerResponse<String> fail(ResponseCode code) {
        return new ServerResponse<>(code.getCode(), code.getDesc());
    }

    /* 返回自定义失败状态码和自定义失败信息，如： -1，修改失败，当前用户不存在！*/
    public static ServerResponse<String> failOfCodeAndMsg(String code, String msg) {
        return new ServerResponse<>(code, msg);
    }
}
