package org.optipace.masterService.controller;

import lombok.RequiredArgsConstructor;
import org.optipace.masterService.dto.response.*;
import org.optipace.masterService.service.MasterService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/master")
@RequiredArgsConstructor
public class MasterController {

    private final MasterService masterService;

    @GetMapping("/v1/allDepartments")
    public ResponseEntity<SingleResponse<PageResponse<MasterDepartmentResponse>>> getAllDepartment(@RequestParam(defaultValue = "0")int page,
                                                                                                   @RequestParam(defaultValue = "10")int size){
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(masterService.getAllDepartments(pageable));
    }

    @GetMapping("/v1/allDesignations")
    public ResponseEntity<SingleResponse<PageResponse<MasterDesignationResponse>>> getAllDesignations(@RequestParam(defaultValue = "0")int page,
                                                                                                      @RequestParam(defaultValue = "10")int size){
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(masterService.getAllDesignations(pageable));
    }

    @GetMapping("/v1/allOrganizations")
    public ResponseEntity<SingleResponse<PageResponse<MasterOrganizationResponse>>> getAllOrganizations(@RequestParam(defaultValue = "0")int page,
                                                                                                        @RequestParam(defaultValue = "10")int size){
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(masterService.getAllOrganizations(pageable));
    }

    @GetMapping("/v1/allSections")
    public ResponseEntity<SingleResponse<PageResponse<MasterSectionResponse>>> getAllSections(@RequestParam(defaultValue = "0")int page,
                                                                                              @RequestParam(defaultValue = "10")int size){
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(masterService.getAllSections(pageable));
    }

    @GetMapping("/v1/allShifts")
    public ResponseEntity<SingleResponse<PageResponse<MasterShiftResponse>>> getAllShifts(@RequestParam(defaultValue = "0")int page,
                                                                                              @RequestParam(defaultValue = "10")int size){
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(masterService.getAllShifts(pageable));
    }

    @GetMapping("/v1/allCountries")
    public ResponseEntity<SingleResponse<PageResponse<MasterCountryResponse>>> getAllCountries(@RequestParam(defaultValue = "0")int page,
                                                                                          @RequestParam(defaultValue = "10")int size){
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(masterService.getAllCountries(pageable));
    }

    @GetMapping("/v1/allStates")
    public ResponseEntity<SingleResponse<PageResponse<MasterStateResponse>>> getAllStates(@RequestParam(defaultValue = "0")int page,
                                                                                          @RequestParam(defaultValue = "10")int size){
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(masterService.getAllStates(pageable));
    }

    @GetMapping("/v1/allCities")
    public ResponseEntity<SingleResponse<PageResponse<MasterCityResponse>>> getAllCities(@RequestParam(defaultValue = "0")int page,
                                                                                          @RequestParam(defaultValue = "10")int size){
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(masterService.getAllCities(pageable));
    }

    @GetMapping("/v1/allFactories")
    public ResponseEntity<SingleResponse<PageResponse<MasterFactoryResponse>>> getAllFactories(@RequestParam(defaultValue = "0")int page,
                                                                                         @RequestParam(defaultValue = "10")int size){
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(masterService.getAllFactories(pageable));
    }
}
