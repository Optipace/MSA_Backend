package org.optipace.adminService.dto.response;

import org.optipace.adminService.dto.response.Response;
import org.optipace.adminService.enums.CustomStatus;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SingleResponse<T> {
    
    private T data;
    private ResponseInfo response;
    
    // ✅ Add this constructor explicitly
    public SingleResponse(T data, CustomStatus status) {
        this.data = data;
        this.response = new ResponseInfo(status.getCode(), status.getMessage());
    }
    
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