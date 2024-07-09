package br.com.github.jordihofc.withdraw.shared.lockmanager;

import com.amazonaws.services.dynamodbv2.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Service
public class DistributedLockServer {
    private AmazonDynamoDBLockClient dynamoDBLockClient;
    private final AmazonDynamoDB dynamoDB;
    private static final Logger LOGGER = LoggerFactory.getLogger(DistributedLockServer.class);

    public DistributedLockServer(AmazonDynamoDB dynamoDB, AmazonDynamoDBLockClient dynamoDBLockClient) {
        this.dynamoDBLockClient = dynamoDBLockClient;
        this.dynamoDB = dynamoDB;
    }


    public Optional<LockItem> acquireLock(String key) {
        LOGGER.info("Tentando Obter um lock para o recurso:  {} in time: {}", key, LocalTime.now());
        AcquireLockOptions keyQuery = AcquireLockOptions.builder(key).build();

        try {
            Optional<LockItem> lockItem = dynamoDBLockClient.tryAcquireLock(keyQuery);
            LOGGER.info("Resultado da consulta this: {} :  as {}", lockItem, LocalTime.now());
            return lockItem;
        } catch (InterruptedException e) {
            LOGGER.error("Deu ruim aqui", e);
            return Optional.empty();
        }
    }

    public void releaseLock(LockItem lock) {
        dynamoDBLockClient.releaseLock(lock);
        LOGGER.info("Lock liberado com sucesso as {}", LocalTime.now());
    }

}
