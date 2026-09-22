package org.optipace.garmentService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    
	 private List<T> content;
	    private int pageNumber;
	    private int pageSize;
	    private long totalElements;
	    private int totalPages;
	    private boolean last;

}