package org.optipace.masterService.repository;

import org.optipace.masterService.entity.Section;
import org.optipace.masterService.entity.Shift;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Long> {
    Page<Shift> findByRecordStatus(char recordStatus, Pageable pageable);
}
