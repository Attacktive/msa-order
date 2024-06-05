package com.github.attacktive.msaorder.common.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@RequiredArgsConstructor
public class WebMvcConfiguration implements WebMvcConfigurer {
	private final AppProperties appProperties;

	@Override
	public void addCorsMappings(@NonNull CorsRegistry corsRegistry) {
		corsRegistry.addMapping("/**")
			.allowedOrigins(appProperties.getAllowedOrigins())
			.allowedMethods("*");
	}
}
