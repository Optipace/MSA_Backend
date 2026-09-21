package org.optipace.masterService.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CountryUpdateRequest {
	  
	    @Size(max = 20)
	    private String country_code;

	    @Size(max = 100)
	    private String country_name;
	    
	    @Size(max = 20)
	    private String iso_code;
	    
	    @Size(max = 20)
	    private String phone_code;
	    
	 
}
