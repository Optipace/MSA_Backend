package org.optipace.garmentService.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseMigrationConfig implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;
    @Override
    @Transactional
    public void run(ApplicationArguments args) {

        String step = "Initializing database migration";

        try {
            step = "Allow severity_level_id to be NULL";
            log.info("Starting migration: {}", step);

            jdbcTemplate.execute("""
                ALTER TABLE garment.garment_area_defect
                ALTER COLUMN severity_level_id DROP NOT NULL
                """);

            step = "Update garment_area_defect unique constraint";
            log.info("Starting migration: {}", step);

            jdbcTemplate.execute("""
                ALTER TABLE garment.garment_area_defect
                DROP CONSTRAINT IF EXISTS uk_garment_area_defect
                """);

            jdbcTemplate.execute("""
                ALTER TABLE garment.garment_area_defect
                ADD CONSTRAINT uk_garment_area_defect
                UNIQUE (
                    garment_batch_id,
                    garment_area_id,
                    defect_id
                )
                """);

            step = "Update garment_area_defect record_status check";
            log.info("Starting migration: {}", step);

            jdbcTemplate.execute("""
                ALTER TABLE garment.garment_area_defect
                DROP CONSTRAINT IF EXISTS
                garment_area_defect_record_status_check
                """);

            jdbcTemplate.execute("""
                ALTER TABLE garment.garment_area_defect
                ADD CONSTRAINT garment_area_defect_record_status_check
                CHECK (record_status IN ('A', 'U', 'D'))
                """);

            step = "Check garment_batch columns";
            log.info("Starting migration: {}", step);

            Boolean oldColumnExists = jdbcTemplate.queryForObject("""
                SELECT EXISTS (
                    SELECT 1
                    FROM information_schema.columns
                    WHERE table_schema = 'garment'
                      AND table_name = 'garment_batch'
                      AND column_name = 'garment_code'
                )
                """, Boolean.class);

            Boolean newColumnExists = jdbcTemplate.queryForObject("""
                SELECT EXISTS (
                    SELECT 1
                    FROM information_schema.columns
                    WHERE table_schema = 'garment'
                      AND table_name = 'garment_batch'
                      AND column_name = 'garment_name'
                )
                """, Boolean.class);

            if (Boolean.TRUE.equals(oldColumnExists)
                    && !Boolean.TRUE.equals(newColumnExists)) {

                jdbcTemplate.execute("""
                    ALTER TABLE garment.garment_batch
                    RENAME COLUMN garment_code TO garment_name
                    """);

            } else if (!Boolean.TRUE.equals(newColumnExists)) {
                throw new IllegalStateException(
                    "Neither garment_code nor garment_name exists "
                    + "in garment.garment_batch."
                );
            }

            step = "Remove old garment_batch unique constraint";
            log.info("Starting migration: {}", step);

            jdbcTemplate.execute("""
                ALTER TABLE garment.garment_batch
                DROP CONSTRAINT IF EXISTS uq_garment_batch_code
                """);

            step = "Check duplicate batch_id and garment_id combinations";
            log.info("Starting migration: {}", step);

            Integer duplicateCount = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM (
                    SELECT batch_id, garment_id
                    FROM garment.garment_batch
                    GROUP BY batch_id, garment_id
                    HAVING COUNT(*) > 1
                ) duplicates
                """, Integer.class);

            if (duplicateCount != null && duplicateCount > 0) {
                throw new IllegalStateException(
                    "Found " + duplicateCount
                    + " duplicate (batch_id, garment_id) combinations."
                );
            }

            step = "Create garment_batch unique constraint";
            log.info("Starting migration: {}", step);

            jdbcTemplate.execute("""
                ALTER TABLE garment.garment_batch
                DROP CONSTRAINT IF EXISTS uq_garment_batch_id_per_batch
                """);

            jdbcTemplate.execute("""
                ALTER TABLE garment.garment_batch
                ADD CONSTRAINT uq_garment_batch_id_per_batch
                UNIQUE (batch_id, garment_id)
                """);

            log.info("All database migrations completed successfully.");

        } catch (Exception e) {
            log.error("Migration failed at step: {}", step, e);

            throw new IllegalStateException(
                "Database migration failed at step: " + step
                + ". Cause: " + e.getMessage(),
                e
            );
        }
    }
    
}
