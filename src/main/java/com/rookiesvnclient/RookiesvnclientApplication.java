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

	@Autowired
	private SvnReaderService svnReaderService;

	public static void main(String[] args) {
		SpringApplication.run(RookiesvnclientApplication.class, args);
	}

	@Override
	public void run(String... args) {
		try {
			List<String> folderPaths = List.of("SMMES/QMS/FQMS_QMM120", "SHMST_DEV/QMS/FQMS_QMM120", "MASUNGMES/QMS/FQMS_QMM120"); // 폴더 여러개
			svnReaderService.testConnection();
			svnReaderService.fetchLogs(folderPaths, 2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

