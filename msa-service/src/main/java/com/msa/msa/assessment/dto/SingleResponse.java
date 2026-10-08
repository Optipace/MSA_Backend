package com.msa.msa.assessment.dto;

import com.msa.msa.assessment.enums.CustomStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.coyote.Response;

@Getter
@Setter
@NoArgsConstructor
public class SingleResponse<T> {

    private T data;
    private CustomStatus response;

    public SingleResponse(T data, CustomStatus status) {
        this.data = data;
        this.response = status;
    }
}
