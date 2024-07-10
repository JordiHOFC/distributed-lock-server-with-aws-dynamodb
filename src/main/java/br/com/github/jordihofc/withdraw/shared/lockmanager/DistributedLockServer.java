package br.com.github.jordihofc.withdraw.shared.lockmanager;

import com.amazonaws.services.dynamodbv2.AcquireLockOptions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBLockClient;
import com.amazonaws.services.dynamodbv2.LockItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.Optional;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.slf4j.LoggerFactory.getLogger;

@Service
public class DistributedLockServer {
    private AmazonDynamoDBLockClient dynamoDBLockClient;
    private static final Logger LOGGER = getLogger(DistributedLockServer.class);

    public DistributedLockServer(AmazonDynamoDBLockClient dynamoDBLockClient) {
        this.dynamoDBLockClient = dynamoDBLockClient;
    }

    public Optional<LockItem> acquireLock(String key) {
        LOGGER.info("Tentando Obter um lock para o recurso:  {} in time: {}", key, LocalTime.now());
        //no ambiente de testes, foi necessário equiparar os tempos de lease e hearbeat, pois, as threads sem locks, desistiam de executar retentativas
        AcquireLockOptions keyQuery = AcquireLockOptions.builder(key)
                .withTimeUnit(SECONDS)
                .withAdditionalTimeToWaitForLock(12L)//adicional um tempo extra para as threads que não possuem um lock, ficarem enviando retentativas
                .build();

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
