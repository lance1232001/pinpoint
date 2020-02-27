package com.navercorp.pinpoint.plugin.spring.web;

import com.navercorp.pinpoint.bootstrap.config.ProfilerConfig;

public class SpringWebMvcConfig {

    private final boolean statEnable;

    public SpringWebMvcConfig(ProfilerConfig config) {
        if (config == null) {
            throw new NullPointerException("config");
        }

        this.statEnable = config.readBoolean("profiler.spring.webmvc.stat.enable", true);
    }


    public boolean isStatEnable() {
        return statEnable;
    }

    @Override
    public String toString() {
        return "SpringWebMvcConfig{" +
            "statEnable=" + statEnable +
            '}';
    }

}
