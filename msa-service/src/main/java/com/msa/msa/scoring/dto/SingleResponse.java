package com.msa.msa.scoring.dto;

import lombok.Data;

@Data
public class SingleResponse<T> {
    private String status;
    private String message;
    private T data;
}