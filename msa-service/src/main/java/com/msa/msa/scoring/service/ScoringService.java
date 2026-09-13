package com.msa.msa.scoring.service;

import com.msa.msa.assessment.entity.AssessmentAttempt;
import com.msa.msa.assessment.entity.AssessmentModule;
import com.msa.msa.assessment.entity.AssessmentModuleInstance;
import com.msa.msa.assessment.entity.AssessmentSession;
import com.msa.msa.assessment.repository.AssessmentAttemptRepository;
import com.msa.msa.assessment.repository.AssessmentModuleInstanceRepository;
import com.msa.msa.assessment.repository.AssessmentModuleRepository;
import com.msa.msa.assessment.repository.AssessmentTemplateRuleRepository;
import com.msa.msa.assessment.repository.AssessmentSessionRepository;
import com.msa.msa.scoring.dto.FinalScoreResponse;
import com.msa.msa.scoring.entity.CompetencyRating;
import com.msa.msa.scoring.entity.FinalScore;
import com.msa.msa.scoring.entity.ModuleScore;
import com.msa.msa.scoring.repository.CompetencyRatingRepository;
import com.msa.msa.scoring.repository.FinalScoreRepository;
import com.msa.msa.scoring.repository.ModuleScoreRepository;
import com.msa.msa.scoring.exception.*;


import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ScoringService {

    private final AssessmentSessionRepository assessmentSessionRepository;
    private final AssessmentAttemptRepository assessmentAttemptRepository;
    private final AssessmentModuleInstanceRepository assessmentModuleInstanceRepository;
    private final AssessmentModuleRepository assessmentModuleRepository;
    private final AssessmentTemplateRuleRepository assessmentTemplateRuleRepository;

    private final ModuleScoreRepository moduleScoreRepository;
    private final FinalScoreRepository finalScoreRepository;
    private final CompetencyRatingRepository competencyRatingRepository;

    public ScoringService(
            AssessmentSessionRepository assessmentSessionRepository,
            AssessmentAttemptRepository assessmentAttemptRepository,
            AssessmentModuleInstanceRepository assessmentModuleInstanceRepository,
            AssessmentModuleRepository assessmentModuleRepository,
            AssessmentTemplateRuleRepository assessmentTemplateRuleRepository,
            ModuleScoreRepository moduleScoreRepository,
            FinalScoreRepository finalScoreRepository,
            CompetencyRatingRepository competencyRatingRepository
    ) {
        this.assessmentSessionRepository = assessmentSessionRepository;
        this.assessmentAttemptRepository = assessmentAttemptRepository;
        this.assessmentModuleInstanceRepository = assessmentModuleInstanceRepository;
        this.assessmentModuleRepository = assessmentModuleRepository;
        this.assessmentTemplateRuleRepository = assessmentTemplateRuleRepository;
        this.moduleScoreRepository = moduleScoreRepository;
        this.finalScoreRepository = finalScoreRepository;
        this.competencyRatingRepository = competencyRatingRepository;
    }

    @Transactional
    public FinalScoreResponse calculateFinalScore(UUID assessmentSessionId) {

        // 1. Find assessment session
        AssessmentSession session =
                assessmentSessionRepository.findById(assessmentSessionId)
                        .orElseThrow(() ->
                        new AssessmentNotFoundException("Assessment not found"));
        // 2. Get latest attempt
        AssessmentAttempt attempt =
                assessmentAttemptRepository
                        .findFirstByAssessmentSessionAssessmentSessionIdOrderByAttemptNoDesc(
                                assessmentSessionId)
                        .orElseThrow(() ->
                                new RuntimeException("Assessment attempt not found"));

        // 3. Get module instances
        List<AssessmentModuleInstance> moduleInstances =
                assessmentModuleInstanceRepository
                        .findByAssessmentAttemptAssessmentAttemptIdOrderBySequenceNo(
                                attempt.getAssessmentAttemptId());

        // 4. Read module codes
        Map<String, AssessmentModuleInstance> modules = new HashMap<>();

        for (AssessmentModuleInstance instance : moduleInstances) {

            AssessmentModule module =
                    assessmentModuleRepository
                            .findById(instance.getAssessmentModuleId())
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Assessment module configuration not found"));

            modules.put(module.getModuleCode(), instance);
        }

        // 5. Required modules must be completed
        validateCompleted(modules, "IQ");
        validateCompleted(modules, "COLOR1");
        validateCompleted(modules, "COLOR2");
        validateCompleted(modules, "DEFECT");
        validateCompleted(modules, "AREA");

        // 6. Read module percentages
        BigDecimal iqScore = percentage(modules.get("IQ"));

        BigDecimal color1Score = percentage(modules.get("COLOR1"));
        BigDecimal color2Score = percentage(modules.get("COLOR2"));

        // Color is one final component
        BigDecimal colorScore =
                color1Score
                        .add(color2Score)
                        .divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);

        BigDecimal defectScore = percentage(modules.get("DEFECT"));
        BigDecimal areaScore = percentage(modules.get("AREA"));

        // 7. Save module scores
        saveModuleScore(modules.get("IQ"));
        saveModuleScore(modules.get("COLOR1"));
        saveModuleScore(modules.get("COLOR2"));
        saveModuleScore(modules.get("DEFECT"));
        saveModuleScore(modules.get("AREA"));

        // 8. Read weightages from DB
        BigDecimal iqWeightage =
                getRuleValue(session.getAssessmentTemplateId(), "IQ_WEIGHTAGE");

        BigDecimal colorWeightage =
                getRuleValue(session.getAssessmentTemplateId(), "COLOR_WEIGHTAGE");

        BigDecimal defectWeightage =
                getRuleValue(session.getAssessmentTemplateId(), "DEFECT_WEIGHTAGE");

        BigDecimal areaWeightage =
                getRuleValue(session.getAssessmentTemplateId(), "AREA_WEIGHTAGE");

        // 9. Calculate weighted final score
        BigDecimal finalScore =
                iqScore.multiply(iqWeightage)
                        .add(colorScore.multiply(colorWeightage))
                        .add(defectScore.multiply(defectWeightage))
                        .add(areaScore.multiply(areaWeightage))
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        // 10. Find competency rating from DB
        CompetencyRating competencyRating =
                findCompetencyRating(
                        session.getAssessmentTemplateId(),
                        finalScore
                );

        // 11. Read PASS percentage from DB
        BigDecimal passPercentage =
                getRuleValue(
                        session.getAssessmentTemplateId(),
                        "PASS_PERCENTAGE"
                );

        String overallResult =
                finalScore.compareTo(passPercentage) >= 0
                        ? "PASS"
                        : "FAIL";

        // 12. Save final score
        FinalScore finalScoreEntity =
                finalScoreRepository
                        .findByAssessmentSessionId(assessmentSessionId)
                        .orElseGet(FinalScore::new);

        finalScoreEntity.setAssessmentSessionId(assessmentSessionId);
        finalScoreEntity.setTotalScore(finalScore);
        finalScoreEntity.setPercentage(finalScore);
        finalScoreEntity.setCompetencyRatingId(
                competencyRating.getCompetencyRatingId()
        );
        finalScoreEntity.setOverallResult(overallResult);
        finalScoreEntity.setCalculatedOn(OffsetDateTime.now());

        finalScoreRepository.save(finalScoreEntity);

        // 13. Update assessment session
        session.setTotalScore(finalScore);
        session.setPercentage(finalScore);
        session.setCompetencyRatingId(
                competencyRating.getCompetencyRatingId()
        );
        session.setCompletedOn(OffsetDateTime.now());

        assessmentSessionRepository.save(session);

        // 14. Update attempt
        attempt.setTotalScore(finalScore);
        attempt.setPercentage(finalScore);
        attempt.setResult(overallResult);
        attempt.setCompletedOn(OffsetDateTime.now());

        assessmentAttemptRepository.save(attempt);

        // 15. Return response
        return new FinalScoreResponse(
                assessmentSessionId,
                iqScore,
                colorScore,
                defectScore,
                areaScore,
                iqWeightage,
                colorWeightage,
                defectWeightage,
                areaWeightage,
                finalScore,
                competencyRating.getCompetencyRatingId(),
                overallResult,
                "COMPLETED"
        );
    }

    private void validateCompleted(
            Map<String, AssessmentModuleInstance> modules,
            String moduleCode
    ) {

        AssessmentModuleInstance module = modules.get(moduleCode);

        if (module == null) {
            throw new RuntimeException(
                    "Required module not found: " + moduleCode);
        }

        if (module.getCompletedOn() == null
                || !"COMPLETED".equals(
                module.getModuleStatus().getStatusCode())) {

            throw new RuntimeException(
                    "Module is not completed: " + moduleCode);
        }
    }

    private BigDecimal percentage(
            AssessmentModuleInstance module
    ) {

        if (module.getPercentage() == null) {
            throw new RuntimeException(
                    "Percentage not available for module");
        }

        return module.getPercentage();
    }

    private void saveModuleScore(
            AssessmentModuleInstance module
    ) {

        ModuleScore score =
                moduleScoreRepository
                        .findByAssessmentModuleInstanceId(
                                module.getAssessmentModuleInstanceId())
                        .orElseGet(ModuleScore::new);

        score.setAssessmentModuleInstanceId(
                module.getAssessmentModuleInstanceId());

        score.setMaximumScore(module.getMaximumScore());
        score.setObtainedScore(module.getObtainedScore());
        score.setPercentage(module.getPercentage());

        CompetencyRating rating =
                findCompetencyRating(
                        module.getAssessmentAttempt()
                                .getAssessmentSession()
                                .getAssessmentTemplateId(),
                        module.getPercentage()
                );

        score.setCompetencyRatingId(
                rating.getCompetencyRatingId());

        score.setCalculatedOn(OffsetDateTime.now());

        moduleScoreRepository.save(score);
    }

    private BigDecimal getRuleValue(
            Long templateId,
            String ruleName
    ) {

        return assessmentTemplateRuleRepository
                .findByAssessmentTemplateIdAndRuleName(
                        templateId,
                        ruleName
                )
                .map(rule -> new BigDecimal(rule.getRuleValue()))
                .orElseThrow(() ->
                        new RuntimeException(
                                "Assessment rule not configured: "
                                        + ruleName));
    }

    private CompetencyRating findCompetencyRating(
            Long templateId,
            BigDecimal score
    ) {

        BigDecimal excellent =
                getRuleValue(
                        templateId,
                        "EXCELLENT_MIN_PERCENTAGE");

        if (score.compareTo(excellent) >= 0) {
            return competencyRatingRepository
                    .findByRatingCode("EXCELLENT")
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "EXCELLENT competency rating not configured"));
        }

        BigDecimal good =
                getRuleValue(
                        templateId,
                        "GOOD_MIN_PERCENTAGE");

        if (score.compareTo(good) >= 0) {
            return competencyRatingRepository
                    .findByRatingCode("GOOD")
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "GOOD competency rating not configured"));
        }

        BigDecimal average =
                getRuleValue(
                        templateId,
                        "AVERAGE_MIN_PERCENTAGE");

        if (score.compareTo(average) >= 0) {
            return competencyRatingRepository
                    .findByRatingCode("AVERAGE")
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "AVERAGE competency rating not configured"));
        }

        return competencyRatingRepository
                .findByRatingCode("BELOW_AVERAGE")
                .orElseThrow(() ->
                        new RuntimeException(
                                "BELOW_AVERAGE competency rating not configured"));
    }
}