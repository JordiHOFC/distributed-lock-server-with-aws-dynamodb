package br.com.github.jordihofc.withdraw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration(proxyBeanMethods = false)
public class TestWithdrawApplication {

	public static void main(String[] args) {
		SpringApplication.from(WithdrawApplication::main).with(TestWithdrawApplication.class).run(args);
	}

}
