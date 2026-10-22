package org.optipace.adminService.repository;

import org.optipace.adminService.entity.Payment;
import org.optipace.adminService.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

	Optional<Payment> findByTransactionId(String transactionId);

	Optional<Payment> findByGatewayOrderId(String gatewayOrderId);

	Optional<Payment> findByGatewayPaymentId(String gatewayPaymentId);

	List<Payment> findByOrganizationIdOrderByCreatedOnDesc(Long organizationId);

	Page<Payment> findByOrganizationId(Long organizationId, Pageable pageable);

	Page<Payment> findByPaymentStatus(PaymentStatus paymentStatus, Pageable pageable);

	boolean existsByTransactionId(String transactionId);

	Optional<Payment> findFirstByOrganizationIdAndPaidByAndPaymentStatusOrderByCreatedOnDesc(Long organizationId,
			Long paidBy, PaymentStatus status);

	boolean existsByOrganizationIdAndPaidByAndPaymentStatusAndCreatedOnAfter(Long organizationId, Long paidBy,
			PaymentStatus status, LocalDateTime after);
	
	// ⭐ Added for Billing Report
    List<Payment> findByOrganizationIdOrderByPaymentDateDesc(Long organizationId);
    
    List<Payment> findByOrganizationIdAndPaymentStatus(Long organizationId, String status);

}