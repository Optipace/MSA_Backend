package org.optipace.adminService.repository;
import org.optipace.adminService.entity.SubscriptionPackage;

import java.util.List;
import java.util.Optional;

import org.optipace.adminService.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionPackageRepository extends JpaRepository<SubscriptionPackage, Long> {

	//Optional<Payment> findById(Long organizationPackageId);
	
    List<SubscriptionPackage> findByRecordStatus(Character recordStatus);

}