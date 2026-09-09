package org.optipace.authService.dto.requestDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InternalRegisterLoginRequest {
    @NotNull
    private Long employeeId;

    @NotBlank
    private String username;

    @NotBlank
    private String plainTextPassword;

    @NotNull
    private Long roleId;
}
