package com.sagar.fds.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sagar.fds.dto.response.CategoryTotalResponse;
import com.sagar.fds.entity.FinancialRecord;
import com.sagar.fds.entity.enums.RecordType;

public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, Long> {

	Optional<FinancialRecord> findByIdAndDeletedFalse(Long id);

	@Query("""
			    SELECT r FROM FinancialRecord r
			    WHERE r.deleted = false
			      AND (:type IS NULL OR r.type = :type)
			      AND (:category IS NULL OR r.category = :category)
			      AND (r.recordDate >= COALESCE(:startDate, r.recordDate))
			      AND (r.recordDate <= COALESCE(:endDate, r.recordDate))
			    ORDER BY r.recordDate DESC, r.createdAt DESC
			""")
	Page<FinancialRecord> findWithFilters(@Param("type") RecordType type, @Param("category") String category,
			@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, Pageable pageable);

	// --- Dashboard aggregation queries ---

	@Query("""
			    SELECT COALESCE(SUM(r.amount), 0)
			    FROM FinancialRecord r
			    WHERE r.deleted = false AND r.type = :type
			""")
	BigDecimal sumByType(@Param("type") RecordType type);

	@Query("SELECT COUNT(r) FROM FinancialRecord r WHERE r.deleted = false")
	long countActive();

	@Query("SELECT new com.sagar.fds.dto.response.CategoryTotalResponse("
			+ "r.category, CAST(r.type AS string), SUM(r.amount)) " + "FROM FinancialRecord r WHERE r.deleted = false "
			+ "GROUP BY r.category, r.type ORDER BY SUM(r.amount) DESC")
	List<CategoryTotalResponse> getCategoryTotals();

	@Query("""
			    SELECT YEAR(r.recordDate) AS yr,
			           MONTH(r.recordDate) AS mo,
			           SUM(CASE WHEN r.type = 'INCOME'  THEN r.amount ELSE 0 END),
			           SUM(CASE WHEN r.type = 'EXPENSE' THEN r.amount ELSE 0 END)
			    FROM FinancialRecord r
			    WHERE r.deleted = false
			      AND r.recordDate >= :since
			    GROUP BY YEAR(r.recordDate), MONTH(r.recordDate)
			    ORDER BY yr DESC, mo DESC
			""")
	List<Object[]> getMonthlyTrendsSince(@Param("since") LocalDate since);

	@Query("""
			    SELECT r FROM FinancialRecord r
			    WHERE r.deleted = false
			    ORDER BY r.createdAt DESC
			    LIMIT :limit
			""")
	List<FinancialRecord> findRecentRecords(@Param("limit") int limit);
}