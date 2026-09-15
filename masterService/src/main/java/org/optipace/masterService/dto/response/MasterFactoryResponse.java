package org.optipace.masterService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MasterFactoryResponse {
    private Long factoryId;
    private String factoryCode;
    private String factoryName;
}
