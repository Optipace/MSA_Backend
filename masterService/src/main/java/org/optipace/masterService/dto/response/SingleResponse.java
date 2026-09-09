package org.optipace.masterService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.optipace.masterService.enums.CustomStatus;

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
