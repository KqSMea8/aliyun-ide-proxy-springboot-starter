package com.alibaba.dataworks.ide.proxy.common;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 接口返回类
 */
@Data
@Accessors(chain=true)
public class Result<T> implements Serializable{

    private static final long serialVersionUID = 7154887528070131284L;

    private String message ;

    private Integer code ;

    private Boolean success ;

    private T data ;

    private Long timestamp ;

    private String sessionId ;

    public Result() {
        timestamp = System.currentTimeMillis() ;
    }

    /**
     * 包含错误信息与返回code的返回值
     * @param msg
     * @param code
     * @return
     */
    public static Result ofError(String msg , Integer code ){
        return of(msg , code , null , false ) ;
    }

    /**
     * 包含错误信息的返回值
     * @param msg
     * @return
     */
    public static Result ofError(String msg ){
        return of(msg , Code.ERROR.code , null , false ) ;
    }

    /**
     * 包含错误信息的返回值
     * @param msg
     * @return
     */
    public static Result ofError(String msg , String sessionId){
        return of(msg , Code.ERROR.code , null , false ,sessionId) ;
    }

    /**
     * 构建一个包含正确元素的返回值
     * @param data
     * @return
     */
    public static <T> Result<T> ofSuccess(T data){
        return of(null , Code.SUCCESS.code , data , true) ;
    }


    /**
     * 构建一个包含正确元素的返回值
     * @param data
     * @param code
     * @return
     */
    public static <T> Result<T> ofSuccess(Integer code  , T data){
        return of(null , code , data , true) ;
    }

    /**
     * 自定义一个完整的返回结构
     * @param msg
     * @param code
     * @return
     */
    public static <T> Result<T> of(String msg , Integer code , T data , Boolean success){
        Result result =  new Result() ;
        result.setMessage(msg)
            .setSuccess(success)
            .setData(data)
            .setCode(code);
        return result ;
    }

    /**
     * 自定义一个完整的返回结构
     * @param msg
     * @param code
     * @return
     */
    public static <T> Result<T> of(String msg , Integer code , T data , Boolean success , String sessionId){
        Result result = of(msg,code,data,success).setSessionId(sessionId);
        return result ;
    }

}