package com.mihai.shorturl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
//@ImportResource("classpath:spring-config.xml")
//@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class,
//		DataSourceTransactionManagerAutoConfiguration.class, HibernateJpaAutoConfiguration.class })
public class ShorturlApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShorturlApplication.class, args);
	}

//	@Bean
//	@ConfigurationProperties("my.datasource")
//	public DataSource dataSource() {
//		return DataSourceBuilder.create().build();
//	}
}
