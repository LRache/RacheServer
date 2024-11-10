package com.rache.racheserver.music;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "music")
public class MusicProperties {
    @Value("coverDir")
    private String coverDir;

    @Value("audioDir")
    private String audioDir;

    public String getCoverDir() {
        return coverDir;
    }

    public void setCoverDir(String coverDir) {
        this.coverDir = coverDir;
    }

    public String getAudioDir() {
        return audioDir;
    }

    public void setAudioDir(String audioDir) {
        this.audioDir = audioDir;
    }
}
