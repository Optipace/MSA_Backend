package org.optipace.masterService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLogResponse {
    private String activity;
    private String time; // e.g., "10 min ago"
}