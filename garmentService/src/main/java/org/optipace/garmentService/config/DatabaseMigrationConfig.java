package org.optipace.garmentService.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseMigrationConfig implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {

        try {
            // 1. Allow severity_level_id to be NULL
            jdbcTemplate.execute("""
                ALTER TABLE garment.garment_area_defect
                ALTER COLUMN severity_level_id DROP NOT NULL
                """);

            log.info("severity_level_id is now nullable.");

            // 2. Remove the old unique constraint, if present
            jdbcTemplate.execute("""
                ALTER TABLE garment.garment_area_defect
                DROP CONSTRAINT IF EXISTS uk_garment_area_defect
                """);

            log.info("Old uk_garment_area_defect constraint removed if present.");

            // 3. Apply the correct three-column unique constraint
            jdbcTemplate.execute("""
                ALTER TABLE garment.garment_area_defect
                ADD CONSTRAINT uk_garment_area_defect
                UNIQUE (
                    garment_batch_id,
                    garment_area_id,
                    defect_id
                )
                """);

            log.info(
                "Updated unique constraint: " +
                "garment_batch_id + garment_area_id + defect_id."
            );
         // Allow record status A, U, and D
            jdbcTemplate.execute("""
                ALTER TABLE garment.garment_area_defect
                DROP CONSTRAINT IF EXISTS garment_area_defect_record_status_check
                """);

            jdbcTemplate.execute("""
                ALTER TABLE garment.garment_area_defect
                ADD CONSTRAINT garment_area_defect_record_status_check
                CHECK (record_status IN ('A', 'U', 'D'))
                """);

            log.info("record_status now permits A, U, and D.");

        } catch (Exception e) {
            log.error("Database migration failed.", e);
            throw new IllegalStateException(
                "Failed to update garment_area_defect table.", e
            );
        }
    }
}
