package org.optipace.garmentService.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SingleResponse<T> {
    
    private T data;
    private ResponseInfo response;
    
    // ==================== SUCCESS ====================
    public static <T> SingleResponse<T> success(T data) {
        return new SingleResponse<>(data, new ResponseInfo(1, "SUCCESS"));
    }
    
    public static <T> SingleResponse<T> success(T data, String message) {
        return new SingleResponse<>(data, new ResponseInfo(1, message));
    }
    
    // ==================== ERROR ====================
    public static <T> SingleResponse<T> error(int code, String message) {
        return new SingleResponse<>(null, new ResponseInfo(code, message));
    }
    
    public static <T> SingleResponse<T> error(String message) {
        return new SingleResponse<>(null, new ResponseInfo(0, message));
    }
    
    public static <T> SingleResponse<T> error(T data, int code, String message) {
        return new SingleResponse<>(data, new ResponseInfo(code, message));
    }
    
    // ==================== INNER CLASS ====================
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResponseInfo {
        private int code;
        private String message;
    }
}