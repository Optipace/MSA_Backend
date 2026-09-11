package org.optipace.masterService.dto.request;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import java.sql.Time;

@Getter
@Setter
public class AddShiftRequest {
	
	@NotNull(message = "Factory ID is required")
    private Long factoryId;

    @NotBlank(message = "Shift code is required")
    @Size(max = 20, message = "Shift code cannot exceed 20 characters")
    private String shiftCode;

    @NotBlank(message = "Shift name is required")
    @Size(max = 100, message = "Shift name cannot exceed 100 characters")
    private String shiftName;

    @NotNull(message = "Start time is required")
    @JsonFormat(pattern = "HH:mm:ss") 
    private Time startTime;

    @NotNull(message = "End time is required")
    @JsonFormat(pattern = "HH:mm:ss")
    private Time endTime;

    private Integer breakDurationMinutes;

    private String remarks;

}
