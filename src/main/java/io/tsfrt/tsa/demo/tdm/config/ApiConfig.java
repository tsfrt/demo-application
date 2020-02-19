package io.tsfrt.tsa.demo.tdm.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConfigurationProperties(prefix = "api")
public class ApiConfig {

    private String version = "v1";
    private String config = "default";
    private String format = "json";
    private String data;

    public String getVersion() {
        return version;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(final String config) {
        this.config = config;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(final String format) {
        this.format = format;
    }

    public String getData() {
        return data;
    }

    public void setData(final String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ApiConfig [config=" + config + ", data=" + data + ", format=" + format + ", version=" + version + "]";
    }

}