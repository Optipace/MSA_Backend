package org.optipace.masterService.service.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.optipace.masterService.dto.request.AddStateRequest;
import org.optipace.masterService.dto.request.UpdateStateRequest;
import org.optipace.masterService.dto.response.ListOfStateResponse;
import org.optipace.masterService.dto.response.PageResponse;
import org.optipace.masterService.dto.response.SingleResponse;
import org.optipace.masterService.dto.response.StateResponse;
import org.optipace.masterService.entity.State;
import org.optipace.masterService.enums.CustomStatus;
import org.optipace.masterService.exception.BadRequestException;
import org.optipace.masterService.exception.NotFoundException;
import org.optipace.masterService.repository.CountryRepository;
import org.optipace.masterService.repository.StateRepository;
import org.optipace.masterService.service.StateService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class StateServiceImpl implements StateService {

    private final StateRepository stateRepository;

    private final CountryRepository countryRepository;

    private final ModelMapper modelMapper;


    @Override
    public SingleResponse<?> createState(
            AddStateRequest request,
            String adminId) {

        log.info(
                "Initiating state creation for code: {} by Admin: {}",
                request.getStateCode(),
                adminId
        );

        /*
         * Check duplicate only among ACTIVE states.
         *
         * Same country + same state code:
         *
         * A -> reject
         * D -> allow
         */
        if (stateRepository
                .existsByCountry_CountryIdAndStateCode(
                        request.getCountryId(),
                        request.getStateCode()
                )) {

            log.warn(
                    "State code {} already exists in country {}",
                    request.getStateCode(),
                    request.getCountryId()
            );

            throw new BadRequestException(
                    "State code already exists in this country"
            );
        }

        /*
         * Validate Country exists
         */
        countryRepository.findById(request.getCountryId())
                .orElseThrow(() ->
                        new NotFoundException(
                                "Country not found with ID: "
                                        + request.getCountryId()
                        )
                );

        State state = new State();

        state.setCountry(
                countryRepository.getReferenceById(
                        request.getCountryId()
                )
        );

        state.setStateCode(
                request.getStateCode()
        );

        state.setStateName(
                request.getStateName()
        );

        state.setGstStateCode(
                request.getGstStateCode()
        );

        state.setDisplayOrder(
                request.getDisplayOrder()
        );

        state.setRemarks(
                request.getRemarks()
        );

        state.setCreatedBy(
                Long.parseLong(adminId)
        );

        state.setVersionNo(1);

        state.setRecordStatus('A');

        stateRepository.save(state);

        log.info(
                "State successfully created with ID: {}",
                state.getStateId()
        );

        return new SingleResponse<>(
                "State successfully created with code: "
                        + state.getStateCode(),
                CustomStatus.SUCCESS
        );
    }


    @Override
    public SingleResponse<PageResponse<ListOfStateResponse>> getAllState(
            Pageable pageable) {

        Pageable sortedPageable =
                pageable.getSort().isSorted()
                        ? pageable
                        : PageRequest.of(
                                pageable.getPageNumber(),
                                pageable.getPageSize(),
                                Sort.by(
                                        Sort.Order.asc("stateName")
                                                .nullsLast()
                                )
                        );

        Page<State> statePage =
                stateRepository.findByRecordStatus(
                        'A',
                        sortedPageable
                );

        List<ListOfStateResponse> stateResponseList =
                statePage.getContent()
                        .stream()
                        .map(state ->
                                modelMapper.map(
                                        state,
                                        ListOfStateResponse.class
                                )
                        )
                        .toList();

        /*
         * Manually map:
         *
         * State.country.countryId
         * ->
         * ListOfStateResponse.countryId
         */
        for (int i = 0; i < statePage.getContent().size(); i++) {

            State state =
                    statePage.getContent().get(i);

            ListOfStateResponse response =
                    stateResponseList.get(i);

            if (state.getCountry() != null) {

                response.setCountryId(
                        state.getCountry().getCountryId()
                );
            }
        }

        PageResponse<ListOfStateResponse> pageResponse =
                new PageResponse<>(
                        stateResponseList,
                        statePage.getNumber(),
                        statePage.getSize(),
                        statePage.getTotalElements(),
                        statePage.getTotalPages(),
                        statePage.isLast()
                );

        return new SingleResponse<>(
                pageResponse,
                CustomStatus.SUCCESS
        );
    }


    @Override
    @Transactional
    public SingleResponse<?> updateState(
            Long stateId,
            UpdateStateRequest request,
            String adminId) {

        log.info(
                "Initiating state update for ID: {} by Admin: {}",
                stateId,
                adminId
        );

        /*
         * Only ACTIVE state can be updated.
         *
         * D record will not be found.
         */
        State state =
                stateRepository.findByStateIdAndRecordStatus(
                        stateId,
                        'A'
                ).orElseThrow(() ->
                        new NotFoundException(
                                "Active state not found with ID: "
                                        + stateId
                        )
                );

        /*
         * Existing country
         */
        Long countryId =
                state.getCountry().getCountryId();

        if (request.getCountryId() != null) {
            countryId = request.getCountryId();
        }

        /*
         * Existing state code
         */
        String stateCode =
                state.getStateCode();

        if (request.getStateCode() != null
                && !request.getStateCode().trim().isEmpty()) {

            stateCode = request.getStateCode();
        }

        /*
         * Duplicate validation.
         *
         * Only active records are considered.
         */
        if (request.getCountryId() != null
                || request.getStateCode() != null) {

            if (stateRepository
                    .existsByCountry_CountryIdAndStateCodeAndStateIdNotAndRecordStatus(
                            countryId,
                            stateCode,
                            stateId,
                            'A'
                    )) {

                throw new BadRequestException(
                        "State code already exists in this country"
                );
            }
        }

        /*
         * Validate and update Country
         */
        if (request.getCountryId() != null
                && !request.getCountryId()
                .equals(state.getCountry().getCountryId())) {

            countryRepository.findById(
                            request.getCountryId()
                    )
                    .orElseThrow(() ->
                            new NotFoundException(
                                    "Country not found with ID: "
                                            + request.getCountryId()
                            )
                    );

            state.setCountry(
                    countryRepository.getReferenceById(
                            request.getCountryId()
                    )
            );
        }

        /*
         * Update State Code
         */
        if (request.getStateCode() != null
                && !request.getStateCode().trim().isEmpty()) {

            state.setStateCode(
                    request.getStateCode()
            );
        }

        /*
         * Update State Name
         */
        if (request.getStateName() != null
                && !request.getStateName().trim().isEmpty()) {

            state.setStateName(
                    request.getStateName()
            );
        }

        /*
         * Update GST State Code
         */
        if (request.getGstStateCode() != null
                && !request.getGstStateCode().trim().isEmpty()) {

            state.setGstStateCode(
                    request.getGstStateCode()
            );
        }

        /*
         * Update Display Order
         */
        if (request.getDisplayOrder() != null) {

            state.setDisplayOrder(
                    request.getDisplayOrder()
            );
        }

        /*
         * Update Remarks
         */
        if (request.getRemarks() != null) {

            state.setRemarks(
                    request.getRemarks()
            );
        }

        state.setUpdatedBy(
                Long.parseLong(adminId)
        );

        stateRepository.save(state);

        log.info(
                "Successfully updated state ID: {}",
                stateId
        );

        return new SingleResponse<>(
                "State updated successfully",
                CustomStatus.SUCCESS
        );
    }


    @Override
    public SingleResponse<StateResponse> getStateById(
            Long stateId) {

        /*
         * Only ACTIVE state can be fetched.
         */
        State state =
                stateRepository.findByStateIdAndRecordStatus(
                        stateId,
                        'A'
                ).orElseThrow(() ->
                        new NotFoundException(
                                "Active State not found with ID: "
                                        + stateId
                        )
                );

        StateResponse response =
                modelMapper.map(
                        state,
                        StateResponse.class
                );

        /*
         * Manually map Country -> countryId
         */
        if (state.getCountry() != null) {

            response.setCountryId(
                    state.getCountry().getCountryId()
            );
        }

        return new SingleResponse<>(
                response,
                CustomStatus.SUCCESS
        );
    }


    @Override
    public SingleResponse<?> deleteStateById(
            Long stateId,
            String adminId) {

        State state =
                stateRepository.findById(stateId)
                        .orElseThrow(() ->
                                new NotFoundException(
                                        "State not found"
                                )
                        );

        /*
         * Soft delete
         */
        state.setRecordStatus('D');

        state.setUpdatedBy(
                Long.parseLong(adminId)
        );

        stateRepository.save(state);

        log.info(
                "State successfully deleted with ID: {}",
                stateId
        );

        return new SingleResponse<>(
                "State successfully deleted",
                CustomStatus.SUCCESS
        );
    }
}
