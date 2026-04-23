package com.airport.airportdistanceservice.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

    private final AirportGapProperties airportGapProperties;

    @Bean
    public WebClient webClient() {
        //sadelik ucun tek sorugu ucun .timeout(Duration.ofSeconds(10)) yazmaq olardi
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .responseTimeout(Duration.ofSeconds(3))
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(10, TimeUnit.SECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(10, TimeUnit.SECONDS)));

        return WebClient.builder()
                .baseUrl(airportGapProperties.baseUrl())
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
