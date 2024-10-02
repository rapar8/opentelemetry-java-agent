package com.ing.omsvclyr.configuration;


//import org.springframework.boot.actuate.health.HealthEndpoint;
//import org.springframework.boot.actuate.health.Status;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class HealthMetricsExportConfiguration {

//    private int getStatusCode(HealthEndpoint health) {
//        Status status = health.health().getStatus();
//        if (Status.UP.equals(status)) {
//            return 1;
//        }
//        if (Status.OUT_OF_SERVICE.equals(status)) {
//            return 2;
//        }
//        if (Status.DOWN.equals(status)) {
//            return 0;
//        }
//        return 3;
//    }
}
