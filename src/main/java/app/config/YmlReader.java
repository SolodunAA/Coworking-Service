package app.config;

import org.springframework.beans.factory.annotation.Value;

public class YmlReader {
    @Value("${app.datasource.url}")
    private String url;
    @Value("${app.datasource.user}")
    private String username;
    @Value("${app.datasource.password}")
    private String password;
    @Value("${app.changelog.path}")
    private String changelog;
    @Value("${app.parameters.open_time}")
    private String openTime;

    @Value("${app.parameters.close_time}")
    private String closeTime;
    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getChangelog() {
        return changelog;
    }
    public String getOpenTime() {
        return openTime;
    }

    public String getCloseTime() {
        return closeTime;
    }



}
