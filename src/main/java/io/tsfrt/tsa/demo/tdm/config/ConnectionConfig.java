package io.tsfrt.tsa.demo.tdm.config;

import javax.annotation.PostConstruct;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConfigurationProperties()
public class ConnectionConfig {

    @PostConstruct
    public void test() {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>"+this.toString());
    }

    private String username;
    private String password;
    private String connection;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConnection() {
        return connection;
    }

    public void setConnection(String connection) {
        this.connection = connection;
    }

    @Override
    public String toString() {
        return "ConnectionConfig [connection=" + connection + ", password=" + password + ", username=" + username + "]";
    }

}
