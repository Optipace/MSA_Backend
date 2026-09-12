package org.optipace.masterService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ListOfSectionResponse {

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
