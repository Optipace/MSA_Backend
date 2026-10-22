package org.optipace.adminService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RazorpayOrder {

    private String id;
    private String entity;
    private Integer amount;
    private String currency;
    private String receipt;
    private String status;
}