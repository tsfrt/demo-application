package io.tsfrt.tsa.demo.tdm.model;

import java.time.LocalDateTime;

public class Foo {

    private String name = "Sample Name";
    private String version;
    private final LocalDateTime timestamp = LocalDateTime.now();
    private String description = "This is a sample response";

    public Foo(final String version) {
        System.out.println(String.format("Version>>>>>>>>>%s", version));
        switch (version) {
            case "v1":
                this.name = "Old One";
                this.description = "first attempt";

            case "v2":
                this.name = "New Version";
                this.description = "revised version";
            default:
                this.name = "Unknown";
                this.description = "This is unexpected";
        }
        this.version = version.isEmpty() ? "ER1" : version;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

}