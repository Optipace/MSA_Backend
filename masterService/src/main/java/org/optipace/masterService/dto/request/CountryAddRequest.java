package org.optipace.masterService.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CountryAddRequest {
	  private String country_code;
	    private String country_name;
	    private String iso_code;
	    private String phone_code;

}
