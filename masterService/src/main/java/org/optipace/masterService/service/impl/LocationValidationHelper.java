package org.optipace.masterService.service.impl;

import lombok.AllArgsConstructor;
import org.optipace.masterService.exception.NotFoundException;
import org.optipace.masterService.repository.CityRepository;
import org.optipace.masterService.repository.CountryRepository;
import org.optipace.masterService.repository.StateRepository;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class LocationValidationHelper {
    private final CityRepository cityRepository;
    private final CountryRepository countryRepository;
    private final StateRepository stateRepository;

    public void verifyLocationContext(Long countryId, Long stateId, Long cityId){
        if (!countryRepository.existsById(countryId)) {
            throw new NotFoundException("Country not found with ID: " + countryId);
        }

        if (!stateRepository.existsById(stateId)) {
            throw new NotFoundException("State not found with ID: " + stateId);
        }

        if (!cityRepository.existsById(cityId)) {
            throw new NotFoundException("City not found with ID: " + cityId);
        }
    }
}
