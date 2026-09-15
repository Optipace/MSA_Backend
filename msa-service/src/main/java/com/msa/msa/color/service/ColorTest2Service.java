package com.msa.msa.color.service;

import com.msa.msa.assessment.entity.AssessmentModule;
import com.msa.msa.assessment.entity.AssessmentModuleInstance;
import com.msa.msa.assessment.entity.AssessmentSession;
import com.msa.msa.assessment.entity.AssessmentTemplateRule;
import com.msa.msa.assessment.entity.ModuleStatus;
import com.msa.msa.assessment.repository.AssessmentModuleInstanceRepository;
import com.msa.msa.assessment.repository.AssessmentModuleRepository;
import com.msa.msa.assessment.repository.AssessmentTemplateRuleRepository;
import com.msa.msa.assessment.repository.ModuleStatusRepository;
import com.msa.msa.color.dto.ColorTest2StartResponse;
import com.msa.msa.color.dto.ColorTest2SubmitRequest;
import com.msa.msa.color.dto.ColorTest2SubmitResponse;
import com.msa.msa.common.exception.BadRequestException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Service
public class ColorTest2Service {

    private static final String COLOR2_MODULE_CODE = "COLOR2";

    private static final String TIME_LIMIT_RULE =
            "COLOR2_TIME_LIMIT_SECONDS";

    private static final String PASS_THRESHOLD_RULE =
            "COLOR2_PASS_CORRECT_COUNT";

    private final AssessmentModuleInstanceRepository assessmentModuleInstanceRepository;

    private final AssessmentModuleRepository assessmentModuleRepository;

    private final AssessmentTemplateRuleRepository assessmentTemplateRuleRepository;

    private final ModuleStatusRepository moduleStatusRepository;

    public ColorTest2Service(
            AssessmentModuleInstanceRepository assessmentModuleInstanceRepository,
            AssessmentModuleRepository assessmentModuleRepository,
            AssessmentTemplateRuleRepository assessmentTemplateRuleRepository,
            ModuleStatusRepository moduleStatusRepository) {

        this.assessmentModuleInstanceRepository =
                assessmentModuleInstanceRepository;

        this.assessmentModuleRepository =
                assessmentModuleRepository;

        this.assessmentTemplateRuleRepository =
                assessmentTemplateRuleRepository;

        this.moduleStatusRepository =
                moduleStatusRepository;
    }

    /**
     * Start Color Test 2.
     *
     * The existing AssessmentService is responsible for
     * generic module starting and sequence handling.
     *
     * This method only reads the COLOR2 timer configuration
     * and returns it to the frontend.
     */
    @Transactional
    public ColorTest2StartResponse startColorTest2(
            UUID assessmentSessionId,
            UUID assessmentModuleInstanceId) {

        // 1. Find module instance and verify ownership
        AssessmentModuleInstance moduleInstance =
                assessmentModuleInstanceRepository
                        .findByAssessmentModuleInstanceIdAndAssessmentAttemptAssessmentSessionAssessmentSessionId(
                                assessmentModuleInstanceId,
                                assessmentSessionId
                        )
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Assessment module does not belong to this assessment"
                                )
                        );

        // 2. Get module configuration
        AssessmentModule module =
                assessmentModuleRepository
                        .findById(moduleInstance.getAssessmentModuleId())
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Assessment module configuration not found"
                                )
                        );

        // 3. Verify this is COLOR2
        if (!COLOR2_MODULE_CODE.equals(module.getModuleCode())) {

            throw new BadRequestException(
                    "This assessment module is not Color Test 2"
            );
        }

        // 4. COLOR2 must already be IN_PROGRESS.
        // The existing AssessmentService.startModule()
        // is responsible for making it IN_PROGRESS.
        if (!"IN_PROGRESS".equals(
                moduleInstance.getModuleStatus().getStatusCode())) {

            throw new BadRequestException(
                    "Color Test 2 must be started before requesting its configuration"
            );
        }

        // 5. Get assessment session
        AssessmentSession session =
                moduleInstance.getAssessmentAttempt()
                        .getAssessmentSession();

        // 6. Read COLOR2 timer configuration
        AssessmentTemplateRule timeRule =
                assessmentTemplateRuleRepository
                        .findByAssessmentTemplateIdAndRuleName(
                                session.getAssessmentTemplateId(),
                                TIME_LIMIT_RULE
                        )
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Color Test 2 time limit is not configured"
                                )
                        );

        int timeLimitSeconds;

        try {

            timeLimitSeconds =
                    Integer.parseInt(timeRule.getRuleValue());

        } catch (NumberFormatException e) {

            throw new BadRequestException(
                    "Invalid Color Test 2 time limit configuration"
            );
        }

        if (timeLimitSeconds <= 0) {

            throw new BadRequestException(
                    "Color Test 2 time limit must be greater than zero"
            );
        }

        // 7. Return timer configuration
        return new ColorTest2StartResponse(
                moduleInstance.getAssessmentModuleInstanceId(),
                module.getModuleCode(),
                module.getModuleName(),
                moduleInstance.getModuleStatus().getStatusCode(),
                moduleInstance.getStartedOn(),
                timeLimitSeconds
        );
    }

    /**
     * Submit Color Test 2 result.
     *
     * Frontend sends only correct and wrong counts.
     * Backend determines PASS / FAIL using configuration.
     */
    @Transactional
    public ColorTest2SubmitResponse submitColorTest2(
            ColorTest2SubmitRequest request) {

        // 1. Find module instance and verify ownership
        AssessmentModuleInstance moduleInstance =
                assessmentModuleInstanceRepository
                        .findByAssessmentModuleInstanceIdAndAssessmentAttemptAssessmentSessionAssessmentSessionId(
                                request.getAssessmentModuleInstanceId(),
                                request.getAssessmentSessionId()
                        )
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Assessment module does not belong to this assessment"
                                )
                        );

        // 2. Get module configuration
        AssessmentModule module =
                assessmentModuleRepository
                        .findById(moduleInstance.getAssessmentModuleId())
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Assessment module configuration not found"
                                )
                        );

        // 3. Verify this is COLOR2
        if (!COLOR2_MODULE_CODE.equals(module.getModuleCode())) {

            throw new BadRequestException(
                    "This assessment module is not Color Test 2"
            );
        }

        // 4. COLOR2 must be IN_PROGRESS
        if (!"IN_PROGRESS".equals(
                moduleInstance.getModuleStatus().getStatusCode())) {

            throw new BadRequestException(
                    "Color Test 2 is not in progress"
            );
        }

        // 5. Validate counts
        if (request.getCorrectAnswers() < 0) {

            throw new BadRequestException(
                    "Correct answer count cannot be negative"
            );
        }

        if (request.getWrongAnswers() < 0) {

            throw new BadRequestException(
                    "Wrong answer count cannot be negative"
            );
        }

        // 6. Get assessment session
        AssessmentSession session =
                moduleInstance.getAssessmentAttempt()
                        .getAssessmentSession();

        // 7. Read pass threshold
        AssessmentTemplateRule thresholdRule =
                assessmentTemplateRuleRepository
                        .findByAssessmentTemplateIdAndRuleName(
                                session.getAssessmentTemplateId(),
                                PASS_THRESHOLD_RULE
                        )
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Color Test 2 pass threshold is not configured"
                                )
                        );

        int passThreshold;

        try {

            passThreshold =
                    Integer.parseInt(thresholdRule.getRuleValue());

        } catch (NumberFormatException e) {

            throw new BadRequestException(
                    "Invalid Color Test 2 pass threshold configuration"
            );
        }

        if (passThreshold < 0) {

            throw new BadRequestException(
                    "Color Test 2 pass threshold cannot be negative"
            );
        }

        // 8. Determine PASS / FAIL
        boolean passed =
                request.getCorrectAnswers() >= passThreshold;

        String result =
                passed ? "PASS" : "FAIL";

        // 9. Get COMPLETED status
        ModuleStatus completedStatus =
                moduleStatusRepository
                        .findByStatusCode("COMPLETED")
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "COMPLETED module status is not configured"
                                )
                        );

        // 10. Complete COLOR2
        OffsetDateTime now =
                OffsetDateTime.now(ZoneOffset.UTC);

        moduleInstance.setModuleStatus(completedStatus);

        moduleInstance.setCompletedOn(now);
        BigDecimal maximumScore = BigDecimal.valueOf(
                request.getCorrectAnswers() + request.getWrongAnswers()
        );

        BigDecimal obtainedScore = BigDecimal.valueOf(
                request.getCorrectAnswers()
        );

        BigDecimal percentage = BigDecimal.ZERO;

        if (maximumScore.compareTo(BigDecimal.ZERO) > 0) {
            percentage = obtainedScore
                    .multiply(BigDecimal.valueOf(100))
                    .divide(maximumScore, 2, RoundingMode.HALF_UP);
        }

        moduleInstance.setMaximumScore(maximumScore);
        moduleInstance.setObtainedScore(obtainedScore);
        moduleInstance.setPercentage(percentage);

        assessmentModuleInstanceRepository.save(moduleInstance);

        // 11. Find next module
        List<AssessmentModuleInstance> moduleInstances =
                assessmentModuleInstanceRepository
                        .findByAssessmentAttemptAssessmentAttemptIdOrderBySequenceNo(
                                moduleInstance.getAssessmentAttempt()
                                        .getAssessmentAttemptId()
                        );

        String nextModuleCode = null;

        for (AssessmentModuleInstance nextModule :
                moduleInstances) {

            if (nextModule.getSequenceNo()
                    > moduleInstance.getSequenceNo()) {

                if ("LOCKED".equals(
                        nextModule.getModuleStatus().getStatusCode())) {

                    ModuleStatus availableStatus =
                            moduleStatusRepository
                                    .findByStatusCode("AVAILABLE")
                                    .orElseThrow(() ->
                                            new BadRequestException(
                                                    "AVAILABLE module status is not configured"
                                            )
                                    );

                    nextModule.setModuleStatus(availableStatus);

                    assessmentModuleInstanceRepository.save(nextModule);

                    AssessmentModule nextModuleConfig =
                            assessmentModuleRepository
                                    .findById(
                                            nextModule.getAssessmentModuleId()
                                    )
                                    .orElseThrow(() ->
                                            new BadRequestException(
                                                    "Next assessment module configuration not found"
                                            )
                                    );

                    nextModuleCode =
                            nextModuleConfig.getModuleCode();

                    break;
                }
            }
        }

        // 12. Return result
        return new ColorTest2SubmitResponse(
                moduleInstance.getAssessmentModuleInstanceId(),
                module.getModuleCode(),
                request.getCorrectAnswers(),
                request.getWrongAnswers(),
                passThreshold,
                result,
                completedStatus.getStatusCode(),
                nextModuleCode
        );
    }
}