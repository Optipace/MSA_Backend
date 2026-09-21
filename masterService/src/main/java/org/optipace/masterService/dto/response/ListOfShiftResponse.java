package org.optipace.masterService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Time;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ListOfShiftResponse {
	
	private Long shiftId;

    private Long factoryId;

    private String shiftCode;

    private String shiftName;

    private Time startTime;

    private Time endTime;

    private Integer breakDurationMinutes;

}
