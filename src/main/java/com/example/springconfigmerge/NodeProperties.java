package com.example.springconfigmerge;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "node")
public class NodeProperties {

    private String id;

    public String getId()          { return id; }
    public void setId(String id)   { this.id = id; }
}
