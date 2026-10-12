
package org.optipace.adminService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecentAssessmentPageResponse {

    private List<RecentAssessmentResponse> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;

    public RecentAssessmentPageResponse(
            List<RecentAssessmentResponse> data,
            int page,
            int pageSize,
            long totalElements,
            int totalPages,
            boolean last) {

        this.content = data;
        this.page = page;
        this.size = pageSize;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.first = page == 0;
        this.last = last;
    }
}