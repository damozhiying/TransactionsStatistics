package com.kishlaly.interviews.transactions;

import com.kishlaly.interviews.transactions.service.TransactionsService;
import com.kishlaly.interviews.transactions.service.impl.TimedTransactionsService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Vladimir Kishlaly
 * @since 06.07.2017
 */
@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = {"com.kishlaly.interviews.transactions"})
@EnableJpaRepositories("com.kishlaly.interviews.transactions.repository")
@EntityScan("com.kishlaly.interviews.transactions.model")
@PropertySource("classpath:application.properties")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public TransactionsService transactionsService() {
        return new TimedTransactionsService();
    }

}
