package com.rookiesvnclient.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * application.properties에서 svn.servers.* 속성을 매핑하는 설정 클래스
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "svn")
public class SvnProperties {
    private Map<String, String> servers;
}
