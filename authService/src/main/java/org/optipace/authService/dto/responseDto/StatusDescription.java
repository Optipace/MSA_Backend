package org.optipace.authService.dto.responseDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StatusDescription {
    private String message;
    private Long code;

    public StatusDescription(String message){
        this.message = message;
    }
}
