
package io.tsfrt.tsa.demo.tdm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.tsfrt.tsa.demo.tdm.config.ApiConfig;
import io.tsfrt.tsa.demo.tdm.config.ConnectionConfig;
import io.tsfrt.tsa.demo.tdm.model.Foo;

@RestController
public class DemoController {

    @Autowired
    private ConnectionConfig connectionConfig;

    @Autowired
    private ApiConfig apiConfig;

    @GetMapping("/api/demo")
    public Foo getConfig(final String id) {
        return new Foo(apiConfig.getVersion());
    }

    @GetMapping("/api/config")
    public ConnectionConfig bar() {
        return connectionConfig;
    }

    @Scheduled(fixedDelay = 5000)
    public void hello() {
        System.out.println(String.format("Config = %s-%s", apiConfig.getFormat(), apiConfig.getData()));
    }

}
