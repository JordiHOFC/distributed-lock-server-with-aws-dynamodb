package br.com.github.jordihofc.withdraw;

import br.com.github.jordihofc.withdraw.account.AccountRepository;
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WithdrawApplication {

	public static void main(String[] args) {
		SpringApplication.run(WithdrawApplication.class, args);
	}

}
