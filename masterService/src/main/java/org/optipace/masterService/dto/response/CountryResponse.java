package org.optipace.masterService.dto.response;

import org.jspecify.annotations.Nullable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CountryResponse {
	
	 private Long countryId;
	    private String countryCode;
	    private String countryName;
	    private String isoCode;
	    private String phoneCode;
		
}
