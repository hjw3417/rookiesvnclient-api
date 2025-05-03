package com.rookiesvnclient;

import com.rookiesvnclient.service.SvnReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.rookiesvnclient")
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class}) // <<<<<< 요거 추가
public class SvnTestRunner {
    @Autowired
    private static SvnReaderService svnReaderService;

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SvnTestRunner.class);
        SvnReaderService service = context.getBean(SvnReaderService.class);
        try {
            service.testConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        context.close();
    }
}
