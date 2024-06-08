package com.github.attacktive.msaorder.common.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.ExchangeFunctions;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class WebClientConfiguration {
	private final AppProperties appProperties;

	@Bean
	public WebClient webClient() {
		return WebClient.builder()
			.baseUrl(appProperties.getBaseUrlToProducts())
			.defaultHeaders(httpHeaders -> httpHeaders.setContentType(MediaType.APPLICATION_JSON))
			.exchangeFunction(exchangeFunction())
			.codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(-1))
			.build();
	}

	@Bean
	public ExchangeFunction exchangeFunction() {
		return ExchangeFunctions.create(new ReactorClientHttpConnector());
	}
}
