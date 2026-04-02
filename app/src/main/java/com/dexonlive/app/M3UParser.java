package com.dexonlive.app;

import java.util.ArrayList;
import java.util.List;

public class M3UParser {

    public static List<Channel> parse(String content, String playlistUrl) {
        List<Channel> channels = new ArrayList<>();
        String referer = playlistUrl.substring(0, playlistUrl.lastIndexOf('/') + 1);

        String[] lines = content.split("\\r?\\n");
        String name = "", logo = "", group = "", url = "";

        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("#EXTINF")) {
                // group-title
                if (line.contains("group-title=\"")) {
                    int s = line.indexOf("group-title=\"") + 13;
                    int e = line.indexOf("\"", s);
                    group = (e > s) ? line.substring(s, e) : "Uncategorized";
                } else {
                    group = "Uncategorized";
                }
                // tvg-logo
                if (line.contains("tvg-logo=\"")) {
                    int s = line.indexOf("tvg-logo=\"") + 10;
                    int e = line.indexOf("\"", s);
                    logo = (e > s) ? line.substring(s, e) : "";
                }
                // tvg-name or display name
                if (line.contains("tvg-name=\"")) {
                    int s = line.indexOf("tvg-name=\"") + 10;
                    int e = line.indexOf("\"", s);
                    name = (e > s) ? line.substring(s, e) : "";
                }
                if (name.isEmpty()) {
                    int comma = line.lastIndexOf(",");
                    name = (comma > 0) ? line.substring(comma + 1).trim() : "Unknown";
                }
            } else if (!line.isEmpty() && !line.startsWith("#")) {
                url = line;
                if (!name.isEmpty() && !url.isEmpty()) {
                    channels.add(new Channel(name, logo, group, url, referer));
                }
                name = ""; // reset
            }
        }
        return channels;
    }
}
