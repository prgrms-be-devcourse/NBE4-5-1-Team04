package com.team4.project1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot 애플리케이션의 진입점입니다.
 * 이 클래스는 Spring Boot 애플리케이션을 시작하는 역할을 합니다. {@link SpringApplication#run} 메서드를 호출하여
 * 애플리케이션의 실행을 시작합니다.
 */
@SpringBootApplication
public class Project1Application {

	/**
	 * Spring Boot 애플리케이션을 시작합니다.
	 * 애플리케이션의 진입점으로, {@link SpringApplication#run} 메서드를 호출하여 애플리케이션을 실행합니다.
	 *
	 * @param args 커맨드라인 인수
	 */
	public static void main(String[] args) {

		SpringApplication.run(Project1Application.class, args);
	}

}
