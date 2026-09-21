package com.msa.msa.assessment.service;

import com.msa.msa.assessment.dto.CreateAssessmentRequest;
import com.msa.msa.common.exception.BadRequestException;
import com.msa.msa.assessment.dto.CreateAssessmentResponse;
import com.msa.msa.assessment.dto.ModuleProgressResponse;
import com.msa.msa.assessment.dto.StartModuleResponse;
import com.msa.msa.assessment.dto.AssessmentProgressResponse;
import com.msa.msa.assessment.dto.IqSubmitRequest;
import com.msa.msa.assessment.dto.IqSubmitResponse;
import com.msa.msa.assessment.entity.*;
import com.msa.msa.assessment.repository.*;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AssessmentService {

    private final EmployeeRepository employeeRepository;
    private final AssessmentTemplateRepository assessmentTemplateRepository;
    private final AssessmentTemplateModuleRepository assessmentTemplateModuleRepository;
    private final AssessmentAttemptRepository assessmentAttemptRepository;
    private final AssessmentModuleInstanceRepository assessmentModuleInstanceRepository;
    private final ModuleStatusRepository moduleStatusRepository;
    private final AssessmentSessionRepository assessmentSessionRepository;
    private final AssessmentStatusRepository assessmentStatusRepository;
    private final AssessmentModuleRepository assessmentModuleRepository;
    private final AssessmentTemplateRuleRepository assessmentTemplateRuleRepository;
    

    public AssessmentService(
            EmployeeRepository employeeRepository,
            AssessmentTemplateRepository assessmentTemplateRepository,
            AssessmentTemplateModuleRepository assessmentTemplateModuleRepository,
            AssessmentAttemptRepository assessmentAttemptRepository,
            AssessmentModuleInstanceRepository assessmentModuleInstanceRepository,
            ModuleStatusRepository moduleStatusRepository,
            AssessmentSessionRepository assessmentSessionRepository,
            AssessmentStatusRepository assessmentStatusRepository,
            AssessmentModuleRepository assessmentModuleRepository,
            AssessmentTemplateRuleRepository assessmentTemplateRuleRepository
    ) {
        this.employeeRepository = employeeRepository;
        this.assessmentTemplateRepository = assessmentTemplateRepository;
        this.assessmentTemplateModuleRepository = assessmentTemplateModuleRepository;
        this.assessmentAttemptRepository = assessmentAttemptRepository;
        this.assessmentModuleInstanceRepository = assessmentModuleInstanceRepository;
        this.moduleStatusRepository = moduleStatusRepository;
        this.assessmentSessionRepository = assessmentSessionRepository;
        this.assessmentStatusRepository = assessmentStatusRepository;
        this.assessmentModuleRepository = assessmentModuleRepository;
        this.assessmentTemplateRuleRepository = assessmentTemplateRuleRepository;
    }
    @Transactional
    public StartModuleResponse startModule(
            UUID assessmentSessionId,
            UUID assessmentModuleInstanceId) {

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

        String currentStatus =
                moduleInstance.getModuleStatus().getStatusCode();

        if (!"AVAILABLE".equals(currentStatus)) {

            if ("IN_PROGRESS".equals(currentStatus)) {
                throw new BadRequestException(
                        "Assessment module is already in progress"
                );
            }

            if ("COMPLETED".equals(currentStatus)) {
                throw new BadRequestException(
                        "Assessment module is already completed"
                );
            }

            throw new BadRequestException(
                    "Assessment module is currently locked"
            );
        }

        /*
         * Sequence enforcement
         */
        List<AssessmentModuleInstance> modules =
                assessmentModuleInstanceRepository
                        .findByAssessmentAttemptAssessmentAttemptIdOrderBySequenceNo(
                                moduleInstance.getAssessmentAttempt()
                                        .getAssessmentAttemptId()
                        );

        for (AssessmentModuleInstance module : modules) {

            if (module.getSequenceNo() < moduleInstance.getSequenceNo()) {

                String status =
                        module.getModuleStatus().getStatusCode();

                if (!"COMPLETED".equals(status)) {

                    throw new BadRequestException(
                            "Previous assessment module must be completed first"
                    );
                }
            }
        }

        ModuleStatus inProgressStatus =
                moduleStatusRepository
                        .findByStatusCode("IN_PROGRESS")
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "IN_PROGRESS module status is not configured"
                                )
                        );

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

        moduleInstance.setModuleStatus(inProgressStatus);
        moduleInstance.setStartedOn(now);

        assessmentModuleInstanceRepository.save(moduleInstance);

        /*
         * Update assessment status
         */
        AssessmentSession session =
                moduleInstance.getAssessmentAttempt()
                        .getAssessmentSession();

        AssessmentStatus assessmentInProgress =
                assessmentStatusRepository
                        .findByStatusCode("IN_PROGRESS")
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "IN_PROGRESS assessment status is not configured"
                                )
                        );

        if ("ASSIGNED".equals(
                session.getAssessmentStatus().getStatusCode())) {

            session.setAssessmentStatus(assessmentInProgress);
            session.setStartedOn(now);
            session.setUpdatedOn(now);

            assessmentSessionRepository.save(session);
        }

        AssessmentModule module =
                assessmentModuleRepository
                        .findById(moduleInstance.getAssessmentModuleId())
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Assessment module configuration not found"
                                )
                        );

        return new StartModuleResponse(
                moduleInstance.getAssessmentModuleInstanceId(),
                module.getModuleCode(),
                module.getModuleName(),
                inProgressStatus.getStatusCode(),
                moduleInstance.getStartedOn()
        );
    }
    @Transactional
    public IqSubmitResponse submitIqTest(IqSubmitRequest request) {

        // 1. Find the module instance and make sure it belongs
        //    to the requested assessment
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

        // 2. Verify module configuration
        AssessmentModule module =
                assessmentModuleRepository
                        .findById(moduleInstance.getAssessmentModuleId())
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Assessment module configuration not found"
                                )
                        );

        // 3. Make sure this is the IQ module
        if (!"IQ".equals(module.getModuleCode())) {
            throw new BadRequestException(
                    "This assessment module is not an IQ module"
            );
        }

        // 4. IQ must have been started before it can be submitted
        String moduleStatus =
                moduleInstance.getModuleStatus().getStatusCode();

        if (!"IN_PROGRESS".equals(moduleStatus)) {

            if ("COMPLETED".equals(moduleStatus)) {
                throw new BadRequestException(
                        "IQ module is already completed"
                );
            }

            throw new BadRequestException(
                    "IQ module has not been started"
            );
        }

        // 5. Get IQ time limit from configuration
        AssessmentSession session =
                moduleInstance.getAssessmentAttempt()
                        .getAssessmentSession();

        AssessmentTemplateRule iqTimeRule =
                assessmentTemplateRuleRepository
                        .findByAssessmentTemplateIdAndRuleName(
                                session.getAssessmentTemplateId(),
                                "IQ_TIME_LIMIT_SECONDS"
                        )
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "IQ time limit is not configured"
                                )
                        );

        BigDecimal timeLimitSeconds;

        try {
            timeLimitSeconds =
                    new BigDecimal(iqTimeRule.getRuleValue());
        } catch (NumberFormatException e) {
            throw new BadRequestException(
                    "Invalid IQ time limit configuration"
            );
        }

        // 6. Validate submitted completion time
        BigDecimal completionTimeSeconds =
                request.getCompletionTimeSeconds();

        if (completionTimeSeconds.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException(
                    "Completion time cannot be negative"
            );
        }

        // 7. Determine PASS / FAIL
        boolean passed =
                completionTimeSeconds.compareTo(timeLimitSeconds) <= 0;

        String result = passed ? "PASS" : "FAIL";

        // 8. Get COMPLETED status
        ModuleStatus completedStatus =
                moduleStatusRepository
                        .findByStatusCode("COMPLETED")
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "COMPLETED module status is not configured"
                                )
                        );

        // 9. Complete IQ module
        OffsetDateTime now =
                OffsetDateTime.now(ZoneOffset.UTC);

        moduleInstance.setModuleStatus(completedStatus);
        moduleInstance.setCompletedOn(now);

        // IQ is represented as a percentage score:
        // PASS = 100, FAIL = 0
        moduleInstance.setMaximumScore(BigDecimal.valueOf(100));
        moduleInstance.setObtainedScore(
                passed
                        ? BigDecimal.valueOf(100)
                        : BigDecimal.ZERO
        );
        moduleInstance.setPercentage(
                passed
                        ? BigDecimal.valueOf(100)
                        : BigDecimal.ZERO
        );

        assessmentModuleInstanceRepository.save(moduleInstance);

        // 10. Unlock the next module
        List<AssessmentModuleInstance> modules =
                assessmentModuleInstanceRepository
                        .findByAssessmentAttemptAssessmentAttemptIdOrderBySequenceNo(
                                moduleInstance.getAssessmentAttempt()
                                        .getAssessmentAttemptId()
                        );

        String nextModuleCode = null;

        for (AssessmentModuleInstance nextModule : modules) {

            if (nextModule.getSequenceNo()
                    > moduleInstance.getSequenceNo()) {

                String nextStatus =
                        nextModule.getModuleStatus()
                                .getStatusCode();

                if ("LOCKED".equals(nextStatus)) {

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
                                    .findById(nextModule.getAssessmentModuleId())
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

        // 11. Return result
        return new IqSubmitResponse(
                moduleInstance.getAssessmentModuleInstanceId(),
                module.getModuleCode(),
                completionTimeSeconds,
                timeLimitSeconds,
                result,
                completedStatus.getStatusCode(),
                nextModuleCode
        );
    }

    @Transactional
    public CreateAssessmentResponse createAssessment(
            CreateAssessmentRequest request
    ) {

        // 1. Validate employee
        Employee employee = employeeRepository
                .findById(request.getEmployeeId())
                .orElseThrow(() ->
                        new RuntimeException("Employee not found")
                );

        if (!"A".equals(employee.getRecordStatus())) {
            throw new RuntimeException("Employee is not active");
        }

        // 2. Validate assessment template
        AssessmentTemplate template = assessmentTemplateRepository
                .findById(request.getAssessmentTemplateId())
                .orElseThrow(() ->
                        new RuntimeException("Assessment template not found")
                );

        if (!"A".equals(template.getRecordStatus())) {
            throw new RuntimeException("Assessment template is not active");
        }

        // 3. Get configured modules
        List<AssessmentTemplateModule> templateModules =
                assessmentTemplateModuleRepository
                        .findByAssessmentTemplateIdOrderBySequenceNo(
                                template.getAssessmentTemplateId()
                        );

        if (templateModules.isEmpty()) {
            throw new BadRequestException(
                    "No modules are configured for assessment template: "
                            + request.getAssessmentTemplateId()
            );
        }

        // 4. Current time
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

        // 5. Calculate validity
        int validityDays = template.getValidityDays() != null
                ? template.getValidityDays()
                : 30;

        OffsetDateTime validUntil =
                now.plusDays(validityDays);

        // 6. Generate Test ID
        String assessmentNo =
                generateAssessmentNumber();

        // 7. Create assessment session
        AssessmentSession session = new AssessmentSession();

        session.setAssessmentNo(assessmentNo);
        session.setEmployeeId(employee.getEmployeeId());
        session.setAssessmentTemplateId(
                template.getAssessmentTemplateId()
        );
        session.setAssignedOn(now);
        session.setAssessmentCycleId(template.getAssessmentCycleId());
     
        AssessmentStatus assignedStatus =
                assessmentStatusRepository.findByStatusCode("ASSIGNED")
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "ASSIGNED assessment status not configured"
                                ));

        session.setAssessmentStatus(assignedStatus);
        session.setCreatedOn(now);
        session.setUpdatedOn(now);
        session.setVersionNo(1);
        session.setRecordStatus("A");
        session.setAssignedBy(1L);

        assessmentSessionRepository.save(session);

        // 8. Create first attempt
        AssessmentAttempt attempt = new AssessmentAttempt();

        attempt.setAssessmentSession(session);
        attempt.setAttemptNo(1);
        attempt.setCreatedOn(now);
        attempt.setUpdatedOn(now);
        attempt.setVersionNo(1);
        attempt.setRecordStatus("A");

        assessmentAttemptRepository.save(attempt);

        // 9. Get module statuses
        ModuleStatus lockedStatus =
                moduleStatusRepository.findByStatusCode("LOCKED")
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "LOCKED module status not configured"
                                ));

        ModuleStatus availableStatus =
                moduleStatusRepository.findByStatusCode("AVAILABLE")
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "AVAILABLE module status not configured"
                                ));
       
        for (AssessmentTemplateModule templateModule :
                templateModules) {

            AssessmentModuleInstance moduleInstance =
                    new AssessmentModuleInstance();

            moduleInstance.setAssessmentModuleId(
                    templateModule
                            .getAssessmentModule()
                            .getAssessmentModuleId()
            );

            moduleInstance.setSequenceNo(
                    templateModule.getSequenceNo()
            );

            /*
             * Only the first sequence is available initially.
             * All remaining sequences are locked.
             */
            if (templateModule.getSequenceNo() == 1) {
                moduleInstance.setModuleStatus(
                        availableStatus
                );
            } else {
                moduleInstance.setModuleStatus(
                        lockedStatus
                );
            }

            moduleInstance.setCreatedOn(now);
            moduleInstance.setAssessmentAttempt(attempt);

            assessmentModuleInstanceRepository.save(
                    moduleInstance
            );    
        }

        // 11. Employee name
        String employeeName =
                buildEmployeeName(employee);

        // 12. Return response
        return new CreateAssessmentResponse(
                session.getAssessmentSessionId().toString(),
                session.getAssessmentNo(),
                session.getAssessmentStatus().getStatusCode(),
                validUntil,
                templateModules.get(0)
                        .getAssessmentModule()
                        .getModuleCode()
        );
    }

    private String generateAssessmentNumber() {

        return "TEST-" +
                java.time.LocalDate.now() +
                "-" +
                UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase();
    }

    private String buildEmployeeName(Employee employee) {

        StringBuilder name = new StringBuilder();

        if (employee.getFirstName() != null) {
            name.append(employee.getFirstName());
        }

        if (employee.getMiddleName() != null
                && !employee.getMiddleName().isBlank()) {

            name.append(" ")
                    .append(employee.getMiddleName());
        }

        if (employee.getLastName() != null
                && !employee.getLastName().isBlank()) {

            name.append(" ")
                    .append(employee.getLastName());
        }

        return name.toString();
    }
    @Transactional
    public AssessmentProgressResponse getAssessmentProgress(
            UUID assessmentSessionId) {

        // 1. Find assessment
        AssessmentSession session =
                assessmentSessionRepository.findById(assessmentSessionId)
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Assessment not found"));

        // 2. Find current attempt
        AssessmentAttempt attempt =
                assessmentAttemptRepository
                        .findFirstByAssessmentSessionAssessmentSessionIdOrderByAttemptNoAsc(
                                assessmentSessionId)
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Assessment attempt not found"));

        // 3. Get module instances
        List<AssessmentModuleInstance> moduleInstances =
                assessmentModuleInstanceRepository
                        .findByAssessmentAttemptAssessmentAttemptIdOrderBySequenceNo(
                                attempt.getAssessmentAttemptId());

        // 4. Convert to response
        List<ModuleProgressResponse> modules =
                new ArrayList<>();

        for (AssessmentModuleInstance moduleInstance : moduleInstances) {

            AssessmentModule module =
                    assessmentModuleRepository
                            .findById(moduleInstance.getAssessmentModuleId())
                            .orElseThrow(() ->
                                    new BadRequestException(
                                            "Assessment module configuration not found"));

            modules.add(
                    new ModuleProgressResponse(
                            moduleInstance.getAssessmentModuleInstanceId(),
                            module.getModuleCode(),
                            moduleInstance.getModuleStatus().getStatusCode()
                    )
            );
        }

        // 5. Determine current module
        String currentModule = null;

        for (ModuleProgressResponse module : modules) {

            if ("AVAILABLE".equals(module.getStatus())
                    || "IN_PROGRESS".equals(module.getStatus())) {

                currentModule = module.getModuleCode();
                break;
            }
        }

        // 6. Return response
        return new AssessmentProgressResponse(
                session.getAssessmentSessionId(),
                session.getAssessmentNo(),
                session.getAssessmentStatus().getStatusCode(),
                currentModule,
                modules
        );
    }
}