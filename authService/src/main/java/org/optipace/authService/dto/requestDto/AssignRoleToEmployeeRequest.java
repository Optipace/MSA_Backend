package org.optipace.authService.dto.requestDto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AssignRoleToEmployeeRequest {

    @NotNull(message = "Provide employee id")
    private Long employeeId;

    @NotNull(message = "Provide employee role")
    private Long roleId;

//    @NotNull(message = "Provide the primary role for employee")
//    private Boolean isPrimaryRole;

//    @NotBlank(message = "Effective From Date is required")
//    @FutureOrPresent(message = "Effective From Date must be today or future date")
//    @JsonFormat(pattern = "yyyy-MM-dd")
//    private String effectiveFrom;

//    @NotBlank(message = "Effective To Date is required")
//    @FutureOrPresent(message = "Effective To Date must be today or future date")
//    @JsonFormat(pattern = "yyyy-MM-dd")
//    private String effectiveTo;
}
