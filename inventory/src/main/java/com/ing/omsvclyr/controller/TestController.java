package com.ing.omsvclyr.controller;

import com.ing.omsvclyr.common.Constants;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.swagger.v3.oas.annotations.Hidden;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@RestController
@RequestMapping(Constants.REQUEST_MAPPING.TEST)
@Hidden
public class TestController {

    private static final Logger logger = LogManager.getLogger(TestController.class);

    private final Environment env;

    public TestController(Environment env) {
        this.env = env;
    }

    @GetMapping("test")
    @WithSpan
    public String test(@RequestParam(defaultValue = "4031") int threadSize) {

        long id = Thread.currentThread().getId();
        String name = Thread.currentThread().getName();

        String memDetails = getMemory();

        logger.error(memDetails);
        try {
            thread(threadSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return memDetails + ";" + id + ":" + name + runCommand();
    }

    private String runCommand() {
        String op = null;
        Process p;

        try {
            p = Runtime.getRuntime().exec("sysctl -a");
            op = readCommand(p);
            p.waitFor();
            p.destroy();
        } catch (Exception e) {
            logger.error("Command-ERROR", e);
        }
        return op;
    }

    private String getMemory() {
        com.sun.management.OperatingSystemMXBean bean =
                (com.sun.management.OperatingSystemMXBean)
                        java.lang.management.ManagementFactory.getOperatingSystemMXBean();
        float TotalMemorySize = bean.getTotalMemorySize() / (1024f * 1024f * 1024f);
        float freeMemorySize = bean.getFreeMemorySize() / (1024f * 1024f * 1024f);
        float freeMem = Runtime.getRuntime().freeMemory() / 1048576f;

        float totMem = Runtime.getRuntime().totalMemory() / (1024f * 1024f);
        float maxMem = Runtime.getRuntime().maxMemory() / (1024f * 1024f * 1024f);
        int cpu = Runtime.getRuntime().availableProcessors();
        String physicalRAM = freeMemorySize + "GB/" + TotalMemorySize;

        return "(MB)JFreeMem=" + freeMem +
                ",(MB)JTotMem=" + totMem +
                ",(GB)JMaxMem=" + maxMem +
                ",CPU=" + cpu +
                ",freeMem/TotRAM=" + physicalRAM;
    }

    private String readCommand(Process p) throws IOException {
        String s;
        StringBuilder op = new StringBuilder();
        BufferedReader br = new BufferedReader(
                new InputStreamReader(p.getInputStream()));
        while ((s = br.readLine()) != null) {
            op.append(s).append("\n:");
        }
        return op.toString();
    }

    private void thread(int threadSize) {
        int nbThreads = Thread.getAllStackTraces().keySet().size();

        System.out.println("initialThread:" + nbThreads);

        int i = 0;
        BlockingQueue<String> queue = new ArrayBlockingQueue<>(10);
        for (int j = 0; j < threadSize; j++) {
            i++;
            try {
                new Thread(() -> {
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println(i);
            }
        }
        nbThreads = Thread.getAllStackTraces().keySet().size();

        System.out.println("afterThread:" + nbThreads);
    }

    @GetMapping("env")
    @WithSpan
    public String test() {

        StringBuilder builder = new StringBuilder();
        for (PropertySource<?> propertySource : ((AbstractEnvironment) env).getPropertySources()) {
            if (propertySource instanceof MapPropertySource) {
                for (Map.Entry<String, Object> entry : ((MapPropertySource) propertySource).getSource().entrySet()) {
                    builder.append(entry.getKey()).append("=").append(entry.getValue()).append(";");
                }
            }
        }

        logger.info("sent!!!");

        return builder.toString();
    }

    @GetMapping("inventory")
    @WithSpan
    public String inventory() {

        WebClient client = WebClient.create("http://shipping:8082");

        return client.get()
                .uri("/test/shipping")
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
