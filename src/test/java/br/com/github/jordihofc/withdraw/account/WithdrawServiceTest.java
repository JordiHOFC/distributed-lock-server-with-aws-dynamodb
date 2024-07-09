package br.com.github.jordihofc.withdraw.account;

import br.com.github.jordihofc.withdraw.base.SpringBootIntegrationTest;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class WithdrawServiceTest extends SpringBootIntegrationTest {
    @Autowired
    private WithdrawService withdrawService;
    @Autowired
    private AmazonDynamoDB dynamoDB;
    @Autowired
    private AccountRepository repository;


    @BeforeEach
    void setUp() {
        CreateTableRequest createTableRequest1 = new CreateTableRequest(
                "Account",
                List.of(
                        new KeySchemaElement("id", KeyType.HASH)
                )
        ).withAttributeDefinitions(List.of(
                new AttributeDefinition("id", ScalarAttributeType.S)
        )).withProvisionedThroughput(
                new ProvisionedThroughput(10l, 10l)
        );

        this.dynamoDB.createTable(
                createTableRequest1
        );
        this.dynamoDB.createTable(
                List.of(new AttributeDefinition("key", ScalarAttributeType.S)),
                "lockTable",
                List.of(
                        new KeySchemaElement("key", KeyType.HASH)
                ),
                new ProvisionedThroughput(1l, 1l)
        );
    }

    @AfterEach
    void tearDown() {
        this.dynamoDB.deleteTable("lockTable");
        this.dynamoDB.deleteTable("Account");
    }


    @Test
    @DisplayName("Must be withdraw in Account")
    void t1() throws InterruptedException {
        Account jordi = new Account("Jordi", BigDecimal.TEN);
        repository.save(jordi);

        withdrawService.withdraw(jordi.getId(), BigDecimal.ONE);

        Optional<Account> accountLatterWithdraw = repository.findById(jordi.getId());

        assertTrue(accountLatterWithdraw.isPresent());
        assertEquals(new BigDecimal("9"), accountLatterWithdraw.get().getBalance());
    }

    @Test
    @DisplayName("Must be withdraw in Account concunrrently")
    void t2() throws InterruptedException {
        Account jordi = new Account("Jordi", BigDecimal.TEN);
        repository.save(jordi);

        doSyncAndConcurrently(10, action -> {
            withdrawService.withdraw(jordi.getId(), BigDecimal.ONE);
        });


        Optional<Account> accountLatterWithdraw = repository.findById(jordi.getId());

        assertTrue(accountLatterWithdraw.isPresent());
        assertEquals(new BigDecimal("9"), accountLatterWithdraw.get().getBalance());

    }

    @Test
    @DisplayName("Must be withdraw in Account concunrrently to balance is ZERO")
    void t3() throws InterruptedException {
        Account jordi = new Account("Jordi", BigDecimal.TEN);
        repository.save(jordi);
        doSyncAndConcurrently(10, action -> {
            withdrawService.withdrawWithConcunrrencyControl(jordi.getId(), BigDecimal.ONE);
        });
        Optional<Account> accountLatterWithdraw = repository.findById(jordi.getId());

        assertTrue(accountLatterWithdraw.isPresent());
        assertEquals(BigDecimal.ZERO , accountLatterWithdraw.get().getBalance());
    }


}