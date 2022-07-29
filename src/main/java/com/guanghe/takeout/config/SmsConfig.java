package com.guanghe.takeout.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 腾讯云短信验证配置类
 */
@Data
@Component
@ConfigurationProperties(prefix = "config")
public class SmsConfig {

    private String secretId;

    private String secretKey;

    private String smsSdkAppId;

    private String templateId;

    private String signName;

}
