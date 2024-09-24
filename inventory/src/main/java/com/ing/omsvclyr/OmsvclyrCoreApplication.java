package com.ing.omsvclyr;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@SpringBootApplication
@EnableCaching
@EnableRetry
@EnableConfigurationProperties
@OpenAPIDefinition(info = @Info(title = "OM Service Layer API", description = "Async and Sync API Doc"))
public class OmsvclyrCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(OmsvclyrCoreApplication.class, args);
    }


    @Bean
    public GroupedOpenApi syncApi() { // group all APIs with `user` in the path
        return GroupedOpenApi.builder().group("sync").pathsToMatch("/console/**").build();
    }

    @Bean
    public GroupedOpenApi asyncApi() { // group all APIs with `admin` in the path
        return GroupedOpenApi.builder().group("async").pathsToMatch("/sop/**").build();
    }

    @Bean
    public RetryTemplate createRetryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();

        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(5000); // initial delay in milliseconds
        backOffPolicy.setMultiplier(2.0); // double the delay for each retry
        backOffPolicy.setMaxInterval(86400000); // max delay of 1 day in milliseconds

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(Integer.MAX_VALUE); // retry indefinitely

        retryTemplate.setBackOffPolicy(backOffPolicy);
        retryTemplate.setRetryPolicy(retryPolicy);

        return retryTemplate;
    }
}