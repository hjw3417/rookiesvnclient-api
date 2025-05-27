//package com.rookiesvnclient;
//
//import com.rookiesvnclient.service.SvnReaderService;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.context.annotation.AnnotationConfigApplicationContext;
//
//@SpringBootApplication
//public class RookiesvnclientApplication {
//
//	public static void main(String[] args) {
//		SpringApplication.run(RookiesvnclientApplication.class, args);
//	}
//
//}



//테스트 실행용
package com.rookiesvnclient;

import com.rookiesvnclient.service.SvnReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import java.util.List;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class}) // DB 연결 막기
public class RookiesvnclientApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(RookiesvnclientApplication.class, args);
		System.out.println("RookiesvnclientApplication 실행 성공했음요!");
	}

	@Override
	public void run(String... args) {

	}
}

