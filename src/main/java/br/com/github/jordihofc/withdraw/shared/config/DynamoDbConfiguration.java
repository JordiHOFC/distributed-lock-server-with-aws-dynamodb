package br.com.github.jordihofc.withdraw.shared.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableDynamoDBRepositories(
       basePackages = "br.com.github.jordihofc.withdraw.**"
)
public class DynamoDbConfiguration {
    @Value("${aws.dynamodb.endpoint}")
    private String endpoint;
    @Value("${aws.dynamodb.region}")
    private String region;
    @Value("${aws.dynamodb.access-key}")
    private String accesskey;
    @Value("${aws.dynamodb.secret-key}")
    private String secretKey;
    @Bean
    public AmazonDynamoDB amazonDynamoDB() {

        AWSCredentialsProvider credentials = new AWSStaticCredentialsProvider(
                new BasicAWSCredentials(accesskey, secretKey)
        );

        AwsClientBuilder.EndpointConfiguration endpointConfig
                = new AwsClientBuilder.EndpointConfiguration(endpoint, region);

        AmazonDynamoDB dynamoDbClient = AmazonDynamoDBClientBuilder
                .standard()
                .disableEndpointDiscovery()
                .withCredentials(credentials)
                .withEndpointConfiguration(endpointConfig)
                .build();

        return dynamoDbClient;
    }


}
