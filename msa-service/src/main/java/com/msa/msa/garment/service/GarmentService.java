package com.msa.msa.garment.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.msa.msa.assessment.entity.AssessmentModule;
import com.msa.msa.assessment.entity.AssessmentModuleInstance;
import com.msa.msa.assessment.entity.AssessmentResponse;
import com.msa.msa.assessment.entity.AssessmentSession;
import com.msa.msa.assessment.entity.ModuleStatus;
import com.msa.msa.assessment.repository.AssessmentModuleInstanceRepository;
import com.msa.msa.assessment.repository.AssessmentModuleRepository;
import com.msa.msa.assessment.repository.AssessmentResponseRepository;
import com.msa.msa.assessment.repository.ModuleStatusRepository;
import com.msa.msa.common.exception.BadRequestException;
import com.msa.msa.garment.dto.GarmentSelection;
import com.msa.msa.garment.dto.GarmentSubmitRequest;
import com.msa.msa.garment.dto.GarmentSubmitRequest.GarmentSubmission;
import com.msa.msa.garment.dto.GarmentSubmitResponse;
import com.msa.msa.garment.dto.GarmentSubmitResponse.GarmentResult;
import com.msa.msa.garment.dto.GarmentValidateRequest;
import com.msa.msa.garment.dto.GarmentValidateResponse;
import com.msa.msa.garment.entity.GarmentIdentificationResponse;
import com.msa.msa.garment.repository.GarmentIdentificationResponseRepository;

import jakarta.transaction.Transactional;

@Service
public class GarmentService {

    private static final String DEFECT_MODULE_CODE = "DEFECT";

    private static final String IN_PROGRESS = "IN_PROGRESS";
    private static final String COMPLETED = "COMPLETED";
    private static final String AVAILABLE = "AVAILABLE";
    private static final String LOCKED = "LOCKED";

    private static final BigDecimal PASS_MARK = BigDecimal.valueOf(70);
    private static final BigDecimal FULL_SCORE = BigDecimal.valueOf(100);
    private static final BigDecimal ZERO_SCORE = BigDecimal.ZERO;

    private static final String EXPECTED_CONFIGURATION_SQL =
            "SELECT submission_detail_id, defect_id, area_id " +
            "FROM garment.garment_defect_area_submission " +
            "WHERE batch_id = ? " +
            "AND LOWER(TRIM(garment_id)) = LOWER(TRIM(?)) " +
            "ORDER BY submission_detail_id";

    private static final String BATCH_CONFIGURATION_SQL =
            "SELECT " +
            "    s.garment_id, " +
            "    s.defect_id, " +
            "    dm.defect_name, " +
            "    s.area_id, " +
            "    ga.area_name " +
            "FROM garment.garment_defect_area_submission s " +
            "LEFT JOIN garment.defect_master dm " +
            "    ON dm.defect_id = s.defect_id " +
            "LEFT JOIN garment.garment_area ga " +
            "    ON ga.garment_area_id = s.area_id " +
            "WHERE s.batch_id = ? " +
            "ORDER BY s.garment_id, " +
            "         dm.display_order NULLS LAST, " +
            "         ga.display_order NULLS LAST, " +
            "         s.submission_detail_id";

    private final AssessmentModuleInstanceRepository
            assessmentModuleInstanceRepository;

    private final AssessmentModuleRepository assessmentModuleRepository;

    private final ModuleStatusRepository moduleStatusRepository;

    private final AssessmentResponseRepository assessmentResponseRepository;

    private final GarmentIdentificationResponseRepository
            garmentIdentificationResponseRepository;

    private final JdbcTemplate jdbcTemplate;

    public GarmentService(
            AssessmentModuleInstanceRepository
                    assessmentModuleInstanceRepository,
            AssessmentModuleRepository assessmentModuleRepository,
            ModuleStatusRepository moduleStatusRepository,
            AssessmentResponseRepository assessmentResponseRepository,
            GarmentIdentificationResponseRepository
                    garmentIdentificationResponseRepository,
            JdbcTemplate jdbcTemplate) {

        this.assessmentModuleInstanceRepository =
                assessmentModuleInstanceRepository;
        this.assessmentModuleRepository = assessmentModuleRepository;
        this.moduleStatusRepository = moduleStatusRepository;
        this.assessmentResponseRepository = assessmentResponseRepository;
        this.garmentIdentificationResponseRepository =
                garmentIdentificationResponseRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Validates the assessment module and retrieves all configured
     * garments, defects and areas for the requested batch.
     */
    @Transactional
    public GarmentValidateResponse validateGarment(
            GarmentValidateRequest request) {

        if (request == null) {
            throw new BadRequestException("Request cannot be null");
        }

        if (request.getAssessmentSessionId() == null) {
            throw new BadRequestException(
                    "Assessment session ID is required");
        }

        if (request.getAssessmentModuleInstanceId() == null) {
            throw new BadRequestException(
                    "Assessment module instance ID is required");
        }

        if (request.getBatchId() == null || request.getBatchId() <= 0) {
            throw new BadRequestException(
                    "Batch ID must be a positive number");
        }

        AssessmentModuleInstance moduleInstance =
                findModuleInstance(
                        request.getAssessmentModuleInstanceId(),
                        request.getAssessmentSessionId());

        validateDefectModule(moduleInstance);

        AssessmentSession session =
                moduleInstance.getAssessmentAttempt()
                        .getAssessmentSession();

        GarmentValidateResponse.BatchGarmentsData batchData =
                getBatchConfiguration(request.getBatchId());

        if (batchData.getGarments().isEmpty()) {
            throw new BadRequestException(
                    "No garment configuration found for batch "
                            + request.getBatchId());
        }

        // Store only the batch during validation.
        session.setBatchId(request.getBatchId());

        return new GarmentValidateResponse(
                true,
                "Garments fetched successfully",
                batchData);
    }

    /**
     * Retrieves configured garments and groups areas beneath defects.
     * The detected flag defaults to false.
     */
    private GarmentValidateResponse.BatchGarmentsData getBatchConfiguration(
            Long batchId) {

        try {
            List<BatchConfigurationRow> rows = jdbcTemplate.query(
                    BATCH_CONFIGURATION_SQL,
                    (rs, rowNum) -> mapBatchConfigurationRow(rs),
                    batchId);

            Map<String, GarmentValidateResponse.GarmentData> garments =
                    new LinkedHashMap<>();

            Map<String, Map<Long, GarmentValidateResponse.DefectData>>
                    defectsByGarment = new LinkedHashMap<>();

            for (BatchConfigurationRow row : rows) {

                if (row.getGarmentId() == null
                        || row.getGarmentId().isBlank()) {
                    continue;
                }

                String garmentId = row.getGarmentId().trim();

                GarmentValidateResponse.GarmentData garment =
                        garments.computeIfAbsent(
                                garmentId,
                                id -> new GarmentValidateResponse.GarmentData(
                                        id,
                                        new ArrayList<>()));

                Map<Long, GarmentValidateResponse.DefectData> defectMap =
                        defectsByGarment.computeIfAbsent(
                                garmentId,
                                id -> new LinkedHashMap<>());

                if (row.getDefectId() == null) {
                    continue;
                }

                GarmentValidateResponse.DefectData defect =
                        defectMap.get(row.getDefectId());

                if (defect == null) {
                    defect = new GarmentValidateResponse.DefectData(
                            row.getDefectId(),
                            row.getDefectName(),
                            new ArrayList<>());

                    defectMap.put(row.getDefectId(), defect);
                    garment.getDefects().add(defect);
                }

                if (row.getAreaId() != null
                        && row.getAreaName() != null) {

                    boolean areaAlreadyAdded =
                            defect.getAreas().stream()
                                    .anyMatch(area ->
                                            row.getAreaId().equals(
                                                    area.getId()));

                    if (!areaAlreadyAdded) {
                        defect.getAreas().add(
                                new GarmentValidateResponse.AreaData(
                                        row.getAreaId(),
                                        row.getAreaName(),
                                        false));
                    }
                }
            }

            return new GarmentValidateResponse.BatchGarmentsData(
                    batchId,
                    new ArrayList<>(garments.values()));

        } catch (DataAccessException ex) {
            throw new BadRequestException(
                    "Unable to retrieve garment configuration for batch "
                            + batchId);
        }
    }

    private BatchConfigurationRow mapBatchConfigurationRow(
            ResultSet rs) throws SQLException {

        return new BatchConfigurationRow(
                rs.getString("garment_id"),
                rs.getObject("defect_id", Long.class),
                rs.getString("defect_name"),
                rs.getObject("area_id", Long.class),
                rs.getString("area_name"));
    }

    /**
     * Submits multiple garments in one request.
     *
     * Each garment is scored independently.
     * A garment receives 100 only if all expected defect-area pairs
     * and all expected areas are identified; otherwise it receives 0.
     * Extra selections do not increase the score.
     *
     * One AssessmentResponse is created for the overall module.
     */
    @Transactional
    public GarmentSubmitResponse submitGarment(
            GarmentSubmitRequest request) {

        // 1. Validate the request.
        if (request == null) {
            throw new BadRequestException("Request cannot be null");
        }

        if (request.getAssessmentSessionId() == null) {
            throw new BadRequestException(
                    "Assessment session ID is required");
        }

        if (request.getAssessmentModuleInstanceId() == null) {
            throw new BadRequestException(
                    "Assessment module instance ID is required");
        }

        if (request.getBatchId() == null || request.getBatchId() <= 0) {
            throw new BadRequestException(
                    "Batch ID must be a positive number");
        }

        if (request.getGarments() == null
                || request.getGarments().isEmpty()) {
            throw new BadRequestException(
                    "At least one garment is required");
        }

        // 2. Validate the assessment module.
        AssessmentModuleInstance moduleInstance =
                findModuleInstance(
                        request.getAssessmentModuleInstanceId(),
                        request.getAssessmentSessionId());

        validateDefectModule(moduleInstance);

        AssessmentSession session =
                moduleInstance.getAssessmentAttempt()
                        .getAssessmentSession();

        if (session.getBatchId() == null
                || !request.getBatchId().equals(session.getBatchId())) {

            throw new BadRequestException(
                    "The selected batch does not match this assessment session");
        }

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

        List<GarmentResult> garmentResults = new ArrayList<>();
        Set<String> processedGarmentIds = new HashSet<>();

        int overallExpectedDefects = 0;
        int overallAnsweredPairs = 0;
        int overallCorrectDefects = 0;

        BigDecimal overallScoreTotal = ZERO_SCORE;

        // 3. Process every submitted garment independently.
        for (GarmentSubmission garmentSubmission : request.getGarments()) {

            if (garmentSubmission == null) {
                throw new BadRequestException(
                        "Garment submission cannot be null");
            }

            String garmentId =
                    normalizeGarmentId(garmentSubmission.getGarmentId());

            if (!processedGarmentIds.add(garmentId.toLowerCase())) {
                throw new BadRequestException(
                        "Duplicate garment ID in request: " + garmentId);
            }

            List<GarmentSelection> selections =
                    garmentSubmission.getSelections();

            if (selections == null) {
                throw new BadRequestException(
                        "Selections are required for garment: " + garmentId);
            }

            // 4. Load expected configuration for this garment.
            List<ExpectedConfigurationItem> expectedConfigurations =
                    getExpectedConfiguration(
                            request.getBatchId(),
                            garmentId);

            if (expectedConfigurations.isEmpty()) {
                throw new BadRequestException(
                        "No expected defect or area configuration found "
                                + "for garment " + garmentId
                                + " in batch " + request.getBatchId());
            }

            Set<String> expectedPairs = new HashSet<>();
            Set<Long> expectedAreas = new HashSet<>();

            for (ExpectedConfigurationItem expected
                    : expectedConfigurations) {

                if (expected.getDefectId() != null
                        && expected.getAreaId() != null) {

                    expectedPairs.add(
                            pairKey(
                                    expected.getDefectId(),
                                    expected.getAreaId()));
                }

                if (expected.getAreaId() != null) {
                    expectedAreas.add(expected.getAreaId());
                }
            }

            if (expectedPairs.isEmpty() && expectedAreas.isEmpty()) {
                throw new BadRequestException(
                        "No valid expected defect or area configuration "
                                + "found for garment " + garmentId);
            }

            // 5. Validate and collect submitted selections.
            Set<String> submittedPairs = new HashSet<>();
            Set<Long> submittedAreas = new HashSet<>();

            for (GarmentSelection selection : selections) {

                if (selection == null) {
                    throw new BadRequestException(
                            "Selection cannot be null for garment "
                                    + garmentId);
                }

                if (selection.getDefectId() == null) {
                    throw new BadRequestException(
                            "Defect ID is required for garment "
                                    + garmentId);
                }

                if (selection.getAreaId() == null) {
                    throw new BadRequestException(
                            "Area ID is required for garment "
                                    + garmentId);
                }

                String key = pairKey(
                        selection.getDefectId(),
                        selection.getAreaId());

                if (!submittedPairs.add(key)) {
                    throw new BadRequestException(
                            "Duplicate defect and area selection for garment "
                                    + garmentId);
                }

                submittedAreas.add(selection.getAreaId());
            }

            // 6. Calculate this garment's scores.
            int totalExpectedDefects = expectedPairs.size();
            int totalExpectedAreas = expectedAreas.size();

            int correctlyIdentifiedDefects = 0;

            for (String expectedPair : expectedPairs) {
                if (submittedPairs.contains(expectedPair)) {
                    correctlyIdentifiedDefects++;
                }
            }

            int correctlyIdentifiedAreas = 0;

            for (Long expectedArea : expectedAreas) {
                if (submittedAreas.contains(expectedArea)) {
                    correctlyIdentifiedAreas++;
                }
            }

            boolean allDefectsIdentified =
                    submittedPairs.containsAll(expectedPairs);

            boolean allAreasIdentified =
                    submittedAreas.containsAll(expectedAreas);

            BigDecimal defectScore =
                    allDefectsIdentified ? FULL_SCORE : ZERO_SCORE;

            BigDecimal areaScore =
                    allAreasIdentified ? FULL_SCORE : ZERO_SCORE;

            BigDecimal combinedScore =
                    allDefectsIdentified && allAreasIdentified
                            ? FULL_SCORE
                            : ZERO_SCORE;

            String garmentResult =
                    combinedScore.compareTo(PASS_MARK) >= 0
                            ? "PASS"
                            : "FAIL";

            // 7. Store the selections for this garment.
            for (GarmentSelection selection : selections) {

                String selectedPair = pairKey(
                        selection.getDefectId(),
                        selection.getAreaId());

                ExpectedConfigurationItem matchedExpected = null;

                for (ExpectedConfigurationItem expected
                        : expectedConfigurations) {

                    if (expected.getDefectId() == null
                            || expected.getAreaId() == null) {
                        continue;
                    }

                    if (selectedPair.equals(
                            pairKey(
                                    expected.getDefectId(),
                                    expected.getAreaId()))) {

                        matchedExpected = expected;
                        break;
                    }
                }

                boolean defectCorrect =
                        expectedPairs.contains(selectedPair);

                boolean areaCorrect =
                        expectedAreas.contains(selection.getAreaId());

                GarmentIdentificationResponse response =
                        new GarmentIdentificationResponse();

                // Set the response ID after the overall response is saved.
                response.setBatchId(request.getBatchId());
                response.setGarmentId(garmentId);
                response.setSelectedDefectId(selection.getDefectId());
                response.setSelectedGarmentAreaId(selection.getAreaId());

                if (matchedExpected != null) {
                    response.setExpectedSubmissionDetailId(
                            matchedExpected.getSubmissionDetailId());
                }

                response.setDefectCorrect(defectCorrect);
                response.setAreaCorrect(areaCorrect);
                response.setDefectScore(
                        defectCorrect ? FULL_SCORE : ZERO_SCORE);
                response.setAreaScore(
                        areaCorrect ? FULL_SCORE : ZERO_SCORE);
                response.setCreatedOn(now);

                // Temporarily collect rows until AssessmentResponse is saved.
                pendingResponses.add(response);
            }

            // 8. Add this garment's result to the response list.
            GarmentResult garmentResultData = new GarmentResult(
                    garmentId,
                    totalExpectedDefects,
                    correctlyIdentifiedDefects,
                    totalExpectedAreas,
                    correctlyIdentifiedAreas,
                    defectScore,
                    areaScore,
                    combinedScore,
                    garmentResult,
                    COMPLETED);

            garmentResults.add(garmentResultData);

            overallExpectedDefects += totalExpectedDefects;
            overallAnsweredPairs += submittedPairs.size();
            overallCorrectDefects += correctlyIdentifiedDefects;
            overallScoreTotal = overallScoreTotal.add(combinedScore);
        }

        // 9. Calculate the overall module score.
        BigDecimal moduleScore = overallScoreTotal.divide(
                BigDecimal.valueOf(garmentResults.size()),
                2,
                java.math.RoundingMode.HALF_UP);

        // Keep the first garment ID on the session for compatibility.
        session.setGarmentId(garmentResults.get(0).getGarmentId());

        // 10. Save one overall assessment response.
        AssessmentResponse assessmentResponse =
                new AssessmentResponse();

        assessmentResponse.setAssessmentModuleInstanceId(
                moduleInstance.getAssessmentModuleInstanceId());
        assessmentResponse.setResponseStatusId(null);
        assessmentResponse.setStartedOn(moduleInstance.getStartedOn());
        assessmentResponse.setCompletedOn(now);
        assessmentResponse.setTimeTakenSeconds(null);
        assessmentResponse.setTotalQuestions(overallExpectedDefects);
        assessmentResponse.setAnsweredQuestions(overallAnsweredPairs);
        assessmentResponse.setUnansweredQuestions(
                Math.max(
                        overallExpectedDefects - overallCorrectDefects,
                        0));
        assessmentResponse.setRawScore(moduleScore);
        assessmentResponse.setPercentage(moduleScore);
        assessmentResponse.setRemarks(
                "Combined Defect and Area Identification for "
                        + garmentResults.size() + " garment(s)");
        assessmentResponse.setCreatedOn(now);

        assessmentResponse =
                assessmentResponseRepository.save(assessmentResponse);

        // 11. Save every garment's selection against the overall response.
        for (GarmentIdentificationResponse response : pendingResponses) {
            response.setAssessmentResponseId(
                    assessmentResponse.getAssessmentResponseId());

            garmentIdentificationResponseRepository.save(response);
        }

        // 12. Complete the module.
        ModuleStatus completedStatus =
                moduleStatusRepository.findByStatusCode(COMPLETED)
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "COMPLETED module status is not configured"));

        moduleInstance.setModuleStatus(completedStatus);
        moduleInstance.setCompletedOn(now);
        moduleInstance.setObtainedScore(moduleScore);
        moduleInstance.setMaximumScore(FULL_SCORE);
        moduleInstance.setPercentage(moduleScore);

        assessmentModuleInstanceRepository.save(moduleInstance);

        // 13. Unlock the next locked module.
        List<AssessmentModuleInstance> moduleInstances =
                assessmentModuleInstanceRepository
                        .findByAssessmentAttemptAssessmentAttemptIdAndIsDeletedFalseOrderBySequenceNo(
                                moduleInstance.getAssessmentAttempt()
                                        .getAssessmentAttemptId());

        String nextModuleCode = null;

        for (AssessmentModuleInstance nextModule : moduleInstances) {

            if (nextModule.getSequenceNo() == null
                    || moduleInstance.getSequenceNo() == null
                    || nextModule.getSequenceNo()
                            <= moduleInstance.getSequenceNo()) {
                continue;
            }

            if (nextModule.getModuleStatus() == null
                    || !LOCKED.equals(
                            nextModule.getModuleStatus().getStatusCode())) {
                continue;
            }

            ModuleStatus availableStatus =
                    moduleStatusRepository.findByStatusCode(AVAILABLE)
                            .orElseThrow(() ->
                                    new BadRequestException(
                                            "AVAILABLE module status is not configured"));

            nextModule.setModuleStatus(availableStatus);
            assessmentModuleInstanceRepository.save(nextModule);

            AssessmentModule nextModuleConfig =
                    assessmentModuleRepository
                            .findById(nextModule.getAssessmentModuleId())
                            .orElseThrow(() ->
                                    new BadRequestException(
                                            "Next assessment module configuration not found"));

            nextModuleCode = nextModuleConfig.getModuleCode();
            break;
        }

        // 14. Return one result per garment.
        return new GarmentSubmitResponse(
                assessmentResponse.getAssessmentResponseId(),
                request.getBatchId(),
                garmentResults,
                completedStatus.getStatusCode(),
                nextModuleCode);
    }

    private final List<GarmentIdentificationResponse> pendingResponses =
            new ArrayList<>();

    private AssessmentModuleInstance findModuleInstance(
            UUID moduleInstanceId,
            UUID assessmentSessionId) {

        return assessmentModuleInstanceRepository
                .findByAssessmentModuleInstanceIdAndAssessmentAttemptAssessmentSessionAssessmentSessionId(
                        moduleInstanceId,
                        assessmentSessionId)
                .orElseThrow(() ->
                        new BadRequestException(
                                "Assessment module does not belong to this assessment"));
    }

    private void validateDefectModule(
            AssessmentModuleInstance moduleInstance) {

        AssessmentModule module =
                assessmentModuleRepository
                        .findById(moduleInstance.getAssessmentModuleId())
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Assessment module configuration not found"));

        if (!DEFECT_MODULE_CODE.equals(module.getModuleCode())) {
            throw new BadRequestException(
                    "Garment identification must be performed in the DEFECT module");
        }

        if (moduleInstance.getModuleStatus() == null
                || !IN_PROGRESS.equals(
                        moduleInstance.getModuleStatus().getStatusCode())) {

            throw new BadRequestException(
                    "Defect and area identification must be started before submitting");
        }
    }

    private List<ExpectedConfigurationItem> getExpectedConfiguration(
            Long batchId,
            String garmentId) {

        try {
            return jdbcTemplate.query(
                    EXPECTED_CONFIGURATION_SQL,
                    (rs, rowNum) -> mapExpectedConfiguration(rs),
                    batchId,
                    garmentId);
        } catch (DataAccessException ex) {
            throw new BadRequestException(
                    "Unable to retrieve the expected garment configuration");
        }
    }

    private ExpectedConfigurationItem mapExpectedConfiguration(
            ResultSet rs) throws SQLException {

        return new ExpectedConfigurationItem(
                rs.getObject("submission_detail_id", Long.class),
                rs.getObject("defect_id", Long.class),
                rs.getObject("area_id", Long.class));
    }

    private String normalizeGarmentId(String garmentId) {

        if (garmentId == null || garmentId.isBlank()) {
            throw new BadRequestException("Garment ID is required");
        }

        return garmentId.trim();
    }

    private String pairKey(Long defectId, Long areaId) {
        return defectId + ":" + areaId;
    }

    private static class BatchConfigurationRow {

        private final String garmentId;
        private final Long defectId;
        private final String defectName;
        private final Long areaId;
        private final String areaName;

        private BatchConfigurationRow(
                String garmentId,
                Long defectId,
                String defectName,
                Long areaId,
                String areaName) {

            this.garmentId = garmentId;
            this.defectId = defectId;
            this.defectName = defectName;
            this.areaId = areaId;
            this.areaName = areaName;
        }

        public String getGarmentId() {
            return garmentId;
        }

        public Long getDefectId() {
            return defectId;
        }

        public String getDefectName() {
            return defectName;
        }

        public Long getAreaId() {
            return areaId;
        }

        public String getAreaName() {
            return areaName;
        }
    }

    private static class ExpectedConfigurationItem {

        private final Long submissionDetailId;
        private final Long defectId;
        private final Long areaId;

        private ExpectedConfigurationItem(
                Long submissionDetailId,
                Long defectId,
                Long areaId) {

            this.submissionDetailId = submissionDetailId;
            this.defectId = defectId;
            this.areaId = areaId;
        }

        public Long getSubmissionDetailId() {
            return submissionDetailId;
        }

        public Long getDefectId() {
            return defectId;
        }

        public Long getAreaId() {
            return areaId;
        }
    }
}
