package com.nak.demo.dto.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
@JsonPropertyOrder(value = {"code","message","description","data","timestamp"})
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class Response {
    private String code;
    private String message;
    private String description;
    private Object data;
    private Long timestamp;

    private Response(String code, String message, String description,Object data){
        this.code = code;
        this.message = message;
        this.description = description;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    private Response(String code , String message, String description){
        this.code = code;
        this.message = message;
        this.description = description;
        this.timestamp = System.currentTimeMillis();
    }

    public static Response success(String message , String description){
        return new Response("200",message,description);
    }

    public static Response success(String code, String message,String description){
        return new Response(code,message,description);
    }

    public static Response success(String code,String message,String description,Object data){
        return new Response(code,message,description,data);
    }
    public static Response badRequest(String message,String description){
        return  new Response("400",message,description);
    }

    public static Response badRequest(String message,String description,Object data) {
        return new Response("400", message, description,data);
    }
    public static Response error(String code,String message,String description){
        return new Response(code,message,description);
    }
}
