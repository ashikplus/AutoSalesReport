package com.ashik.AutoSalesReport;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AutoSalesReportApplication {

	public static void main(String[] args) {
		SpringApplication.run(AutoSalesReportApplication.class, args);
	}

}
