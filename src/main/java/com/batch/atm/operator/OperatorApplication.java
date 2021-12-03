package com.batch.atm.operator;

import com.batch.atm.operator.config.AppConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppConfig.class)
public class OperatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(OperatorApplication.class, args);
	}

}
