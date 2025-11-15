package cn.bugstack.types.dto;

import cn.bugstack.types.enums.ResponseCode;
import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应对象
 * @param <T> 响应数据类型
 */
@Data
public class Response<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 响应码
     */
    private String code;

    /**
     * 响应消息
     */
    private String info;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 成功响应
     * @param data 响应数据
     * @param <T> 数据类型
     * @return Response
     */
    public static <T> Response<T> success(T data) {
        Response<T> response = new Response<>();
        response.setCode(ResponseCode.SUCCESS.getCode());
        response.setInfo(ResponseCode.SUCCESS.getInfo());
        response.setData(data);
        return response;
    }

    /**
     * 成功响应（无数据）
     * @param <T> 数据类型
     * @return Response
     */
    public static <T> Response<T> success() {
        Response<T> response = new Response<>();
        response.setCode(ResponseCode.SUCCESS.getCode());
        response.setInfo(ResponseCode.SUCCESS.getInfo());
        return response;
    }

    /**
     * 失败响应
     * @param code 响应码
     * @param info 响应消息
     * @param <T> 数据类型
     * @return Response
     */
    public static <T> Response<T> fail(String code, String info) {
        Response<T> response = new Response<>();
        response.setCode(code);
        response.setInfo(info);
        return response;
    }

    /**
     * 失败响应
     * @param responseCode 响应码枚举
     * @param <T> 数据类型
     * @return Response
     */
    public static <T> Response<T> fail(ResponseCode responseCode) {
        Response<T> response = new Response<>();
        response.setCode(responseCode.getCode());
        response.setInfo(responseCode.getInfo());
        return response;
    }

}