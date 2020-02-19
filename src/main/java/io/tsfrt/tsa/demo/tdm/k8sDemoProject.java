package io.tsfrt.tsa.demo.tdm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class k8sDemoProject {

	public static void main(final String[] args) {
		SpringApplication.run(k8sDemoProject.class, args);
	}

}
