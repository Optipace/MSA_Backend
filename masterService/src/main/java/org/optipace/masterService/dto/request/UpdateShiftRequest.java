package org.optipace.masterService.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import java.sql.Time;

@Getter
@Setter
public class UpdateShiftRequest {
	
	private Long factoryId;

    private String shiftCode;

    private String shiftName;

    @JsonFormat(pattern = "HH:mm:ss")
    private Time startTime;

    @JsonFormat(pattern = "HH:mm:ss")
    private Time endTime;

    private Integer breakDurationMinutes;

    private String remarks;

}
