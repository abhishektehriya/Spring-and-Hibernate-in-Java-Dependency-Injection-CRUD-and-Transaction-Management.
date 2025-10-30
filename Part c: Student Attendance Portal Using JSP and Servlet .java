package com.example.bankapp;

import jakarta.persistence.*;
import org.springframework.context.annotation.*;
import org.springframework.orm.jpa.*;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import javax.sql.DataSource;
import com.zaxxer.hikari.HikariDataSource;

// =======================
// ENTITY CLASS
// =======================
@Entity
@Table(name = "accounts")
class Account {
    @Id
    private int accountId;
    private String holderName;
    private double balance;

    public Account() {}

    public Account(int accountId, String holderName, double balance) {
        this.accountId = accountId;
        this.holderName = holderName;
        this.balance = balance;
    }

    // Getters and Setters
    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getHolderName() {
        return holderName;
    }

    public void setHolderName(String holderName) {
        this.holderName = holderName;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountId=" + accountId +
                ", holderName='" + holderName + '\'' +
                ", balance=" + balance +
                '}';
    }
}

// =======================
// SERVICE CLASS
// =======================
@Service
class BankService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void transferMoney(int fromAccId, int toAccId, double amount) {
        Account from = entityManager.find(Account.class, fromAccId);
        Account to = entityManager.find(Account.class, toAccId);

        if (from == null || to == null) {
            throw new IllegalArgumentException("Invalid account ID(s) provided.");
        }
        if (from.getBalance() < amount) {
            throw new IllegalArgumentException("Insufficient funds in account: " + fromAccId);
        }

        // Deduct and add money
        from.setBalance(from.getBalance() - amount);
        to.setBalance(to.getBalance() + amount);

        entityManager.merge(from);
        entityManager.merge(to);

        System.out.println("Transferred " + amount + " from " + from.getHolderName() +
                " to " + to.getHolderName());
    }
}

// =======================
// CONFIGURATION CLASS
// =======================
@Configuration
@EnableTransactionManagement
@ComponentScan("com.example.bankapp")
class AppConfig {

    @Bean
    public DataSource dataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:mysql://localhost:3306/bank_db");
        ds.setUsername("root");
        ds.setPassword("yourpassword");
        return ds;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan("com.example.bankapp");
        return em;
    }

    @Bean
    public JpaTransactionManager transactionManager() {
        return new JpaTransactionManager(entityManagerFactory().getObject());
    }
}

// =======================
// MAIN CLASS
// =======================
public class BankApp {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(AppConfig.class);

        BankService bankService = context.getBean(BankService.class);

        // Perform transaction
        try {
            bankService.transferMoney(101, 102, 5000);
            System.out.println("✅ Transaction completed successfully.");
        } catch (Exception e) {
            System.out.println("❌ Transaction failed: " + e.getMessage());
        }

        context.close();
    }
}
