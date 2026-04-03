package com.sagar.fds.seed;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.sagar.fds.entity.FinancialRecord;
import com.sagar.fds.entity.User;
import com.sagar.fds.entity.enums.RecordType;
import com.sagar.fds.entity.enums.Role;
import com.sagar.fds.entity.enums.UserStatus;
import com.sagar.fds.repository.FinancialRecordRepository;
import com.sagar.fds.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

	private final UserRepository userRepository;
	private final FinancialRecordRepository recordRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public void run(String... args) {
		if (userRepository.existsByUsername("admin")) {
			log.info("Seed data already exists — skipping");
			return;
		}

		// ── Users (one per role) ──────────────────────────────
		User admin = createUser("admin", "admin@finance.app", "admin123", Role.ADMIN);
		User analyst = createUser("analyst", "analyst@finance.app", "analyst123", Role.ANALYST);
		createUser("viewer", "viewer@finance.app", "viewer123", Role.VIEWER);

		log.info("Seeded 3 users: admin / analyst / viewer (passwords = username + '123')");

		// ── Financial records ─────────────────────────────────
		LocalDate now = LocalDate.now();

		// Current month
		createRecord(admin, "5000.00", RecordType.INCOME, "Salary", now.withDayOfMonth(1), "Monthly salary");
		createRecord(admin, "1200.00", RecordType.EXPENSE, "Rent", now.withDayOfMonth(2), "Monthly rent payment");
		createRecord(admin, "250.00", RecordType.EXPENSE, "Groceries", now.withDayOfMonth(5), "Weekly grocery run");
		createRecord(admin, "800.00", RecordType.INCOME, "Freelance", now.withDayOfMonth(10),
				"Website redesign project");
		createRecord(admin, "60.00", RecordType.EXPENSE, "Utilities", now.withDayOfMonth(8), "Electricity bill");
		createRecord(analyst, "150.00", RecordType.EXPENSE, "Transport", now.withDayOfMonth(6), "Monthly metro pass");

		// Previous month
		LocalDate prev = now.minusMonths(1);
		createRecord(admin, "5000.00", RecordType.INCOME, "Salary", prev.withDayOfMonth(1), "Monthly salary");
		createRecord(admin, "1200.00", RecordType.EXPENSE, "Rent", prev.withDayOfMonth(2), "Monthly rent payment");
		createRecord(admin, "320.00", RecordType.EXPENSE, "Groceries", prev.withDayOfMonth(4),
				"Weekly grocery shopping");
		createRecord(admin, "500.00", RecordType.INCOME, "Freelance", prev.withDayOfMonth(15), "Logo design work");
		createRecord(admin, "75.00", RecordType.EXPENSE, "Utilities", prev.withDayOfMonth(7), "Water and electricity");
		createRecord(admin, "200.00", RecordType.EXPENSE, "Entertainment", prev.withDayOfMonth(20), "Concert tickets");

		// Two months ago
		LocalDate twoAgo = now.minusMonths(2);
		createRecord(admin, "5000.00", RecordType.INCOME, "Salary", twoAgo.withDayOfMonth(1), "Monthly salary");
		createRecord(admin, "1200.00", RecordType.EXPENSE, "Rent", twoAgo.withDayOfMonth(2), "Monthly rent payment");
		createRecord(admin, "180.00", RecordType.EXPENSE, "Groceries", twoAgo.withDayOfMonth(6), "Groceries");
		createRecord(admin, "1500.00", RecordType.INCOME, "Freelance", twoAgo.withDayOfMonth(12),
				"Mobile app prototype");
		createRecord(admin, "90.00", RecordType.EXPENSE, "Utilities", twoAgo.withDayOfMonth(9), "Internet bill");
		createRecord(analyst, "400.00", RecordType.EXPENSE, "Travel", twoAgo.withDayOfMonth(18),
				"Weekend trip expenses");

		log.info("Seeded {} financial records across 3 months", recordRepository.count());
	}

	// ── Helpers ───────────────────────────────────────────────

	private User createUser(String username, String email, String password, Role role) {
		User user = User.builder().username(username).email(email).password(passwordEncoder.encode(password)).role(role)
				.status(UserStatus.ACTIVE).build();
		return userRepository.save(user);
	}

	private void createRecord(User createdBy, String amount, RecordType type, String category, LocalDate date,
			String description) {
		FinancialRecord record = FinancialRecord.builder().amount(new BigDecimal(amount)).type(type).category(category)
				.recordDate(date).description(description).createdBy(createdBy).build();
		recordRepository.save(record);
	}
}