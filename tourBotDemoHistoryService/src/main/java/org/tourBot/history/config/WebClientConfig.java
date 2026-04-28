package org.tourBot.history.config;

import io.netty.channel.ChannelOption;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient() {
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(3)) // 전체 응답 timeout
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 2000); // 연결 timeout

        return WebClient.builder()
                .baseUrl("http://ai-service:8090")
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
