package org.optipace.masterService.repository;

import org.optipace.masterService.entity.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    
    // Fetch the latest 10 activities for the dashboard
    List<ActivityLog> findTop10ByOrderByCreatedOnDesc();
}