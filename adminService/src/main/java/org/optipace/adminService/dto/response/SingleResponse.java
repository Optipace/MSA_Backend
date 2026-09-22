package org.optipace.adminService.dto.response;

import org.optipace.adminService.dto.response.Response;
import org.optipace.adminService.enums.CustomStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SingleResponse<T> {
    private T data;
    private Response response;

    public SingleResponse(T data, CustomStatus status){
        this.data = data;
        this.response = new Response(status.getCode(), status.getMessage());
    }
}
