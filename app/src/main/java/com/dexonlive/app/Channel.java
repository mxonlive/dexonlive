package com.dexonlive.app;

public class Channel {
    private String name;
    private String logo;
    private String group;
    private String url;
    private String referer;

    public Channel(String name, String logo, String group, String url, String referer) {
        this.name = name;
        this.logo = logo;
        this.group = group;
        this.url = url;
        this.referer = referer;
    }

    public String getName() { return name; }
    public String getLogo() { return logo; }
    public String getGroup() { return group; }
    public String getUrl() { return url; }
    public String getReferer() { return referer; }
}
