package com.example.springconfigmerge;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Bound key-by-key: url, user, and pool each resolve from whichever
 * property source owns that dotted key. There is no "db object" to replace.
 */
@ConfigurationProperties(prefix = "db")
public class DbProperties {

    private String url;
    private String user;
    private int pool;

    public String getUrl()       { return url; }
    public void setUrl(String u) { this.url = u; }

    public String getUser()        { return user; }
    public void setUser(String u)  { this.user = u; }

    public int getPool()         { return pool; }
    public void setPool(int p)   { this.pool = p; }
}
