package com.runnersteam.runners;

import com.runnersteam.runners.messaging.RunnerBinding;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableBinding(RunnerBinding.class)
@SpringBootApplication
@EnableJpaAuditing
public class RunnersApplication {

	public static void main(String[] args) {
		SpringApplication.run(RunnersApplication.class, args);
	}

}
