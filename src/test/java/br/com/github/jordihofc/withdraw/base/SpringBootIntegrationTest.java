package br.com.github.jordihofc.withdraw.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;


import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.DYNAMODB;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
public abstract class SpringBootIntegrationTest {

    @Container
    static LocalStackContainer LOCALSTACK_CONTAINER = new LocalStackContainer(
            DockerImageName.parse("localstack/localstack")

    ).withServices(DYNAMODB);

    protected static final Logger LOGGER = LoggerFactory.getLogger(SpringBootIntegrationTest.class);

    /**
     * Starts many threads concurrently to execute the <code>operation</code> at the same time.
     * This method only returns after all threads have been executed.
     */
    protected void doSyncAndConcurrently(int threadCount, Consumer<String> operation) throws InterruptedException {

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            String threadName = "Thread-" + i;
            new Thread(() -> {
                try {
                    startLatch.await();
                    operation.accept(threadName);
                } catch (Exception e) {
                    e.printStackTrace();
                    LOGGER.error("error while executing operation {}: {}", threadName, e.getMessage());
                } finally {
                    endLatch.countDown();
                }
            }).start();
        }

        startLatch.countDown();
        endLatch.await();
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("aws.dynamodb.endpoint",
                () -> LOCALSTACK_CONTAINER.getEndpointOverride(DYNAMODB).toString());
        registry.add("aws.dynamodb.region",
                () -> LOCALSTACK_CONTAINER.getRegion());
        registry.add("aws.dynamodb.access-key",
                () -> LOCALSTACK_CONTAINER.getAccessKey());
        registry.add("aws.dynamodb.secret-key",
                () -> LOCALSTACK_CONTAINER.getSecretKey());
    }
}