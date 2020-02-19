
package io.tsfrt.tsa.demo.tdm.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.tsfrt.tsa.demo.tdm.config.ApiConfig;
import io.tsfrt.tsa.demo.tdm.config.ConnectionConfig;

@RestController
public class DemoController {

    Logger logger = LoggerFactory.getLogger(DemoController.class);

    @Autowired
    private ConnectionConfig connectionConfig;

    @Autowired
    private ApiConfig apiConfig;

    @GetMapping("/api/demo")
    public ApiConfig getConfig(final String id) {
        return apiConfig;
    }

    @GetMapping("/api/config")
    public ConnectionConfig getConnectConfig() {
        return connectionConfig;
    }

    @Scheduled(fixedDelay = 30*1000)
    public void dumpConfig() {
        logger.info(String.format("Api Config:\n %s", apiConfig.toString()));
        logger.info(String.format("Connection Config:\n %s", connectionConfig.toString()));

    }

}
