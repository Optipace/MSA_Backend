package com.msa.msa.garment.service;

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
import com.msa.msa.garment.dto.GarmentSubmitResponse;
import com.msa.msa.garment.dto.GarmentValidateRequest;
import com.msa.msa.garment.dto.GarmentValidateResponse;
import com.msa.msa.garment.entity.GarmentExpectedDefect;
import com.msa.msa.garment.entity.GarmentIdentificationResponse;
import com.msa.msa.garment.entity.GarmentInstance;
import com.msa.msa.garment.repository.GarmentExpectedDefectRepository;
import com.msa.msa.garment.repository.GarmentIdentificationResponseRepository;
import com.msa.msa.garment.repository.GarmentInstanceRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class GarmentService {

    private static final String DEFECT_MODULE_CODE = "DEFECT";
    private static final String AREA_MODULE_CODE = "AREA";

    private final AssessmentModuleInstanceRepository assessmentModuleInstanceRepository;
    private final AssessmentModuleRepository assessmentModuleRepository;
    private final ModuleStatusRepository moduleStatusRepository;
    private final AssessmentResponseRepository assessmentResponseRepository;

    private final GarmentInstanceRepository garmentInstanceRepository;
    private final GarmentExpectedDefectRepository garmentExpectedDefectRepository;
    private final GarmentIdentificationResponseRepository garmentIdentificationResponseRepository;

    public GarmentService(
            AssessmentModuleInstanceRepository assessmentModuleInstanceRepository,
            AssessmentModuleRepository assessmentModuleRepository,
            ModuleStatusRepository moduleStatusRepository,
            AssessmentResponseRepository assessmentResponseRepository,
            GarmentInstanceRepository garmentInstanceRepository,
            GarmentExpectedDefectRepository garmentExpectedDefectRepository,
            GarmentIdentificationResponseRepository garmentIdentificationResponseRepository) {

        this.assessmentModuleInstanceRepository = assessmentModuleInstanceRepository;
        this.assessmentModuleRepository = assessmentModuleRepository;
        this.moduleStatusRepository = moduleStatusRepository;
        this.assessmentResponseRepository = assessmentResponseRepository;
        this.garmentInstanceRepository = garmentInstanceRepository;
        this.garmentExpectedDefectRepository = garmentExpectedDefectRepository;
        this.garmentIdentificationResponseRepository =
                garmentIdentificationResponseRepository;
    }

    /**
     * Validate garment barcode and associate the garment
     * with the current assessment session.
     *
     * Expected defects/areas are never returned to the frontend.
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

        if (request.getBarcode() == null
                || request.getBarcode().trim().isEmpty()) {
            throw new BadRequestException("Barcode is required");
        }

        // 1. Find module instance and verify ownership
        AssessmentModuleInstance moduleInstance =
                assessmentModuleInstanceRepository
                        .findByAssessmentModuleInstanceIdAndAssessmentAttemptAssessmentSessionAssessmentSessionId(
                                request.getAssessmentModuleInstanceId(),
                                request.getAssessmentSessionId())
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Assessment module does not belong to this assessment"));

        // 2. Get module configuration
        AssessmentModule module =
                assessmentModuleRepository
                        .findById(moduleInstance.getAssessmentModuleId())
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Assessment module configuration not found"));

        // 3. Verify DEFECT module
        if (!DEFECT_MODULE_CODE.equals(module.getModuleCode())) {
            throw new BadRequestException(
                    "Garment identification must be performed in the Defect module");
        }

        // 4. DEFECT must already be IN_PROGRESS
        if (!"IN_PROGRESS".equals(
                moduleInstance.getModuleStatus().getStatusCode())) {

            throw new BadRequestException(
                    "Defect identification must be started before scanning a garment");
        }

        // 5. Get assessment session
        AssessmentSession session =
                moduleInstance.getAssessmentAttempt()
                        .getAssessmentSession();

        // 6. Find garment by barcode
        GarmentInstance garment =
                garmentInstanceRepository
                        .findByBarcode(request.getBarcode().trim())
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Garment not found for the provided barcode"));

        // 7. Validate garment status
        if ("REJECTED".equals(garment.getCurrentStatus())) {
            throw new BadRequestException(
                    "This garment is rejected and cannot be used for assessment");
        }

        if ("UNDER_ASSESSMENT".equals(garment.getCurrentStatus())) {
            throw new BadRequestException(
                    "This garment is already under assessment");
        }

        // 8. Associate garment with assessment session
        session.setGarmentInstanceId(garment.getGarmentInstanceId());

        // 9. Mark garment as under assessment
        garment.setCurrentStatus("UNDER_ASSESSMENT");

        garmentInstanceRepository.save(garment);

        // session is managed through the assessment relationship,
        // so no explicit session repository save is required.

        return new GarmentValidateResponse(
                true,
                garment.getGarmentInstanceId(),
                "Garment validated successfully");
    }

    /**
     * Submit combined Defect + Area identification.
     *
     * Defect scoring compares (area + defect) pairs.
     * Area scoring compares distinct expected areas against
     * distinct areas identified by the assessor.
     */
    @Transactional
    public GarmentSubmitResponse submitGarment(
            GarmentSubmitRequest request) {

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

        if (request.getGarmentInstanceId() == null) {
            throw new BadRequestException(
                    "Garment instance ID is required");
        }

        if (request.getSelections() == null) {
            throw new BadRequestException(
                    "Selections are required");
        }

        // 1. Find DEFECT module instance and verify ownership
        AssessmentModuleInstance defectModuleInstance =
                assessmentModuleInstanceRepository
                        .findByAssessmentModuleInstanceIdAndAssessmentAttemptAssessmentSessionAssessmentSessionId(
                                request.getAssessmentModuleInstanceId(),
                                request.getAssessmentSessionId())
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Assessment module does not belong to this assessment"));

        // 2. Get module configuration
        AssessmentModule defectModule =
                assessmentModuleRepository
                        .findById(defectModuleInstance.getAssessmentModuleId())
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Assessment module configuration not found"));

        // 3. Verify DEFECT module
        if (!DEFECT_MODULE_CODE.equals(defectModule.getModuleCode())) {
            throw new BadRequestException(
                    "This assessment module is not the Defect module");
        }

        // 4. DEFECT must be IN_PROGRESS
        if (!"IN_PROGRESS".equals(
                defectModuleInstance.getModuleStatus().getStatusCode())) {

            throw new BadRequestException(
                    "Defect identification is not in progress");
        }

        // 5. Get assessment attempt/session
        AssessmentSession session =
                defectModuleInstance.getAssessmentAttempt()
                        .getAssessmentSession();

        // 6. Verify requested garment belongs to this session
        if (!request.getGarmentInstanceId().equals(
                session.getGarmentInstanceId())) {

            throw new BadRequestException(
                    "Garment does not belong to this assessment session");
        }

        // 7. Load garment
        GarmentInstance garment =
                garmentInstanceRepository
                        .findById(request.getGarmentInstanceId())
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Garment not found"));

        // 8. Load hidden answer key
        List<GarmentExpectedDefect> expectedDefects =
                garmentExpectedDefectRepository
                        .findByGarmentInstanceId(
                                garment.getGarmentInstanceId());

        if (expectedDefects.isEmpty()) {
            throw new BadRequestException(
                    "No expected defect configuration found for this garment");
        }

        // 9. Validate submitted selections
        Set<String> submittedPairs = new HashSet<>();
        Set<Long> submittedAreas = new HashSet<>();

        for (GarmentSelection selection : request.getSelections()) {

            if (selection == null) {
                throw new BadRequestException(
                        "Selection cannot be null");
            }

            if (selection.getDefectId() == null) {
                throw new BadRequestException(
                        "Defect ID is required for every selection");
            }

            if (selection.getAreaId() == null) {
                throw new BadRequestException(
                        "Area ID is required for every selection");
            }

            String pairKey =
                    selection.getAreaId()
                            + ":"
                            + selection.getDefectId();

            if (!submittedPairs.add(pairKey)) {
                throw new BadRequestException(
                        "Duplicate defect and area selection found");
            }

            submittedAreas.add(selection.getAreaId());
        }

        // 10. Build expected defect/area sets
        Set<String> expectedPairs = new HashSet<>();
        Set<Long> expectedAreas = new HashSet<>();

        for (GarmentExpectedDefect expected : expectedDefects) {

            String pairKey =
                    expected.getGarmentAreaId()
                            + ":"
                            + expected.getDefectId();

            expectedPairs.add(pairKey);
            expectedAreas.add(expected.getGarmentAreaId());
        }

        // 11. Calculate correctly identified defects
        int correctlyIdentifiedDefects = 0;

        for (String submittedPair : submittedPairs) {
            if (expectedPairs.contains(submittedPair)) {
                correctlyIdentifiedDefects++;
            }
        }

        int totalExpectedDefects = expectedPairs.size();

        // 12. Calculate correctly identified areas
        //
        // An area is correct if the assessor identified that area
        // and it exists in the expected answer key.
        int correctlyIdentifiedAreas = 0;

        for (Long submittedArea : submittedAreas) {
            if (expectedAreas.contains(submittedArea)) {
                correctlyIdentifiedAreas++;
            }
        }

        int totalExpectedAreas = expectedAreas.size();

        // 13. Calculate percentages
        BigDecimal defectScore =
                percentage(
                        correctlyIdentifiedDefects,
                        totalExpectedDefects);

        BigDecimal areaScore =
                percentage(
                        correctlyIdentifiedAreas,
                        totalExpectedAreas);

        // Combined Defect + Area score
        BigDecimal combinedScore =
                defectScore
                        .add(areaScore)
                        .divide(
                                BigDecimal.valueOf(2),
                                2,
                                RoundingMode.HALF_UP);

        // 14. Create AssessmentResponse
        AssessmentResponse assessmentResponse =
                new AssessmentResponse();

        assessmentResponse.setAssessmentModuleInstanceId(
                defectModuleInstance.getAssessmentModuleInstanceId());

        // response_status_id is currently optional because
        // there is no response-status master table.
        assessmentResponse.setResponseStatusId(null);

        OffsetDateTime now =
                OffsetDateTime.now(ZoneOffset.UTC);

        assessmentResponse.setStartedOn(
                defectModuleInstance.getStartedOn());

        assessmentResponse.setCompletedOn(now);

        assessmentResponse.setTimeTakenSeconds(null);

        assessmentResponse.setTotalQuestions(
                totalExpectedDefects);

        assessmentResponse.setAnsweredQuestions(
                submittedPairs.size());

        assessmentResponse.setUnansweredQuestions(
                Math.max(
                        totalExpectedDefects - submittedPairs.size(),
                        0));

        assessmentResponse.setRawScore(combinedScore);
        assessmentResponse.setPercentage(combinedScore);
        assessmentResponse.setRemarks(
                "Combined Defect and Area Identification");

        assessmentResponse.setCreatedOn(now);

        assessmentResponse =
                assessmentResponseRepository.save(
                        assessmentResponse);

        // 15. Save individual selections
        for (GarmentSelection selection :
                request.getSelections()) {

            String pairKey =
                    selection.getAreaId()
                            + ":"
                            + selection.getDefectId();

            GarmentExpectedDefect matchedExpected = null;

            for (GarmentExpectedDefect expected :
                    expectedDefects) {

                String expectedKey =
                        expected.getGarmentAreaId()
                                + ":"
                                + expected.getDefectId();

                if (expectedKey.equals(pairKey)) {
                    matchedExpected = expected;
                    break;
                }
            }

            boolean defectCorrect =
                    expectedPairs.contains(pairKey);

            boolean areaCorrect =
                    expectedAreas.contains(
                            selection.getAreaId());

            GarmentIdentificationResponse response =
                    new GarmentIdentificationResponse();

            response.setAssessmentResponseId(
                    assessmentResponse.getAssessmentResponseId());

            response.setGarmentInstanceId(
                    garment.getGarmentInstanceId());

            response.setSelectedDefectId(
                    selection.getDefectId());

            response.setSelectedGarmentAreaId(
                    selection.getAreaId());

            if (matchedExpected != null) {
                response.setExpectedDefectId(
                        matchedExpected.getGarmentExpectedDefectId());
            }

            response.setDefectCorrect(defectCorrect);
            response.setAreaCorrect(areaCorrect);

            response.setDefectScore(
                    defectCorrect
                            ? BigDecimal.valueOf(100)
                            : BigDecimal.ZERO);

            response.setAreaScore(
                    areaCorrect
                            ? BigDecimal.valueOf(100)
                            : BigDecimal.ZERO);

            response.setCreatedOn(now);

            garmentIdentificationResponseRepository.save(
                    response);
        }

        // 16. Get COMPLETED status
        ModuleStatus completedStatus =
                moduleStatusRepository
                        .findByStatusCode("COMPLETED")
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "COMPLETED module status is not configured"));

        // 17. Complete DEFECT module
        defectModuleInstance.setModuleStatus(
                completedStatus);

        defectModuleInstance.setCompletedOn(now);

        defectModuleInstance.setObtainedScore(
                combinedScore);

        defectModuleInstance.setMaximumScore(
                BigDecimal.valueOf(100));

        defectModuleInstance.setPercentage(
                combinedScore);

        assessmentModuleInstanceRepository.save(
                defectModuleInstance);

        // 18. Find AREA module instance belonging to
        // the SAME assessment attempt
        AssessmentModuleInstance areaModuleInstance = null;

        List<AssessmentModuleInstance> moduleInstances =
                assessmentModuleInstanceRepository
                        .findByAssessmentAttemptAssessmentAttemptIdOrderBySequenceNo(
                                defectModuleInstance
                                        .getAssessmentAttempt()
                                        .getAssessmentAttemptId());

        for (AssessmentModuleInstance instance :
                moduleInstances) {

            AssessmentModule instanceModule =
                    assessmentModuleRepository
                            .findById(
                                    instance.getAssessmentModuleId())
                            .orElse(null);

            if (instanceModule == null) {
                continue;
            }

            if (AREA_MODULE_CODE.equals(
                    instanceModule.getModuleCode())) {

                areaModuleInstance = instance;
                break;
            }
        }

        if (areaModuleInstance == null) {
            throw new BadRequestException(
                    "AREA module instance not found for this assessment");
        }

        // 19. Complete AREA module
        areaModuleInstance.setModuleStatus(
                completedStatus);

        areaModuleInstance.setCompletedOn(now);

        areaModuleInstance.setObtainedScore(
                areaScore);

        areaModuleInstance.setMaximumScore(
                BigDecimal.valueOf(100));

        areaModuleInstance.setPercentage(
                areaScore);

        assessmentModuleInstanceRepository.save(
                areaModuleInstance);

        // 20. Find next module after sequence 4
        String nextModuleCode = null;

        for (AssessmentModuleInstance nextModule :
                moduleInstances) {

            if (nextModule.getSequenceNo()
                    > defectModuleInstance.getSequenceNo()) {

                if ("LOCKED".equals(
                        nextModule.getModuleStatus()
                                .getStatusCode())) {

                    ModuleStatus availableStatus =
                            moduleStatusRepository
                                    .findByStatusCode("AVAILABLE")
                                    .orElseThrow(() ->
                                            new BadRequestException(
                                                    "AVAILABLE module status is not configured"));

                    nextModule.setModuleStatus(
                            availableStatus);

                    assessmentModuleInstanceRepository.save(
                            nextModule);

                    AssessmentModule nextModuleConfig =
                            assessmentModuleRepository
                                    .findById(
                                            nextModule.getAssessmentModuleId())
                                    .orElseThrow(() ->
                                            new BadRequestException(
                                                    "Next assessment module configuration not found"));

                    nextModuleCode =
                            nextModuleConfig.getModuleCode();

                    break;
                }
            }
        }

        // 21. Mark garment completed
        garment.setCurrentStatus("COMPLETED");

        garmentInstanceRepository.save(garment);

        // 22. Return combined result
        String result =
                combinedScore.compareTo(
                        BigDecimal.valueOf(70)) >= 0
                        ? "PASS"
                        : "FAIL";

        return new GarmentSubmitResponse(
                assessmentResponse.getAssessmentResponseId(),
                garment.getGarmentInstanceId(),
                totalExpectedDefects,
                correctlyIdentifiedDefects,
                totalExpectedAreas,
                correctlyIdentifiedAreas,
                defectScore,
                areaScore,
                combinedScore,
                result,
                completedStatus.getStatusCode(),
                nextModuleCode);
    }

    private BigDecimal percentage(
            int correct,
            int total) {

        if (total == 0) {
            return BigDecimal.ZERO;
        }

        return BigDecimal.valueOf(correct)
                .multiply(BigDecimal.valueOf(100))
                .divide(
                        BigDecimal.valueOf(total),
                        2,
                        RoundingMode.HALF_UP);
    }
}