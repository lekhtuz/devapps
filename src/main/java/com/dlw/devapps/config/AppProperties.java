package com.dlw.devapps.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * @author Dmitry Lekhtuz
 *
 */
@ConfigurationProperties("oauth")
@Component
@Data
public class AppProperties {
	private Duration tokenTtl;
	private String tokenType;
}
