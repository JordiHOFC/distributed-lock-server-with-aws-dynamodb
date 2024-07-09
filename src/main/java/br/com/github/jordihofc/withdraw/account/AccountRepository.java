package br.com.github.jordihofc.withdraw.account;

import br.com.github.jordihofc.withdraw.shared.lockmanager.DistributedLockServer;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBLockClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBLockClientOptions;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@EnableScan
@Repository("accountRepository")
public class AccountRepository {
    private final DynamoDBMapper dynamoDBMapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountRepository.class);

    public AccountRepository(DynamoDBMapper dynamoDBMapper) {
        this.dynamoDBMapper = dynamoDBMapper;
    }


    public Optional<Account> findById(UUID id) {
        LOGGER.info("find account by id: {} in time  : {}",id, LocalTime.now());
        DynamoDBQueryExpression<Account> queryExpression = new DynamoDBQueryExpression<>();
        Account account = new Account(id);
        queryExpression.withHashKeyValues(account)
                .withConsistentRead(true);

        Optional<Account> response = dynamoDBMapper.query(Account.class, queryExpression).stream().findFirst();
        LOGGER.info("result to search this: {}",response);
        return response;
    }


    public void save(Account account) {
        LOGGER.info("Save this account: {}",account);
        dynamoDBMapper.save(account);
        LOGGER.info("Account this saved: {}",account);
    }
}
