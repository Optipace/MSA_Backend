package org.optipace.adminService.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeductPointsResponse {

    private Long organizationId;
    private Long factoryId;
    private Long assessedEmployeeId;
    private Long assessorId;
    private String moduleCode;
    private Long referenceId;

    private Long totalDeducted;
    private Long totalRemaining;
    private LocalDateTime deductedAt;

    private List<PackageDeduction> breakdown;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PackageDeduction {
        private Long organizationPackageId;
        private Long pointTransactionId;
        private Long deductedFromThisPackage;
        private Long remainingInThisPackage;
        private LocalDate packageExpiryDate;
        private String packageName;
    }
}