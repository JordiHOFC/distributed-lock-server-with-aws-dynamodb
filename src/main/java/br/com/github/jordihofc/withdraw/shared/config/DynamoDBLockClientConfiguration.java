package br.com.github.jordihofc.withdraw.shared.config;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBLockClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBLockClientOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.annotation.RequestScope;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static java.lang.Long.MAX_VALUE;

@Configuration
public class DynamoDBLockClientConfiguration {
    private final AmazonDynamoDB dynamoDB;

    public DynamoDBLockClientConfiguration(AmazonDynamoDB dynamoDB) {
        this.dynamoDB = dynamoDB;
    }

    @Bean
    public AmazonDynamoDBLockClient dynamoDBLockClient() {
        AmazonDynamoDBLockClientOptions dynamoDBLockClientOptions = AmazonDynamoDBLockClientOptions.builder(dynamoDB, "lockTable")
                .withTimeUnit(TimeUnit.SECONDS)
                .withLeaseDuration(25l) //define o tempo de lock
                .withHeartbeatPeriod(12l)//define o tempo em que uma thread ficará tentando pegar um lock
                .withCreateHeartbeatBackgroundThread(true)//define uma thread assync para ficar tentando dar o refresh no lock (ref a linha de cima)
                .build();

        return new AmazonDynamoDBLockClient(dynamoDBLockClientOptions);
    }
}
