package org.optipace.garmentService.dto.response;

import org.optipace.garmentService.enums.CustomStatus;

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
	    private Response response;

	    public SingleResponse(T data, CustomStatus status){
	        this.data = data;
	        this.response = new Response(status.getCode(), status.getMessage());
	    }
}