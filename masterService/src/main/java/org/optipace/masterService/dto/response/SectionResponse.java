package org.optipace.masterService.dto.response;

import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Data
public class SectionResponse {

    private Long sectionId;
    private Long departmentId;
    private Long factoryId;
    private String sectionCode;
    private String sectionName;
    private Integer lineNumber;
    private Integer capacity;
    private Long createdBy;
    private LocalDateTime createdOn;
    private Long updatedBy;
    private LocalDateTime updatedOn;
    private int versionNo;
    private String remarks;
    private char recordStatus;
}
