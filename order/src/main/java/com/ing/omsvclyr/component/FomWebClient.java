package com.ing.omsvclyr.component;



import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class FomWebClient {

    private static final Logger logger = LogManager.getLogger(FomWebClient.class);

    private final GcpTokenProvider gcpTokenProvider;

    public FomWebClient(GcpTokenProvider gcpTokenProvider) {
        this.gcpTokenProvider = gcpTokenProvider;
    }

    @Bean
    public WebClient orderInfoWebClient() {

        return WebClient.builder()
                .baseUrl("endpointConfiguration.getFom().getUrl()")
                .filter(ExchangeFilterFunction.ofRequestProcessor(
                        (ClientRequest request) -> Mono.just(
                                ClientRequest.from(request)
                                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + gcpTokenProvider.getIdToken())
                                        .build()
                        )
                )).exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(codec -> codec.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
                        .build())
             //   .filter("webClientLoggerHelper.logRequest()")
                .build();
    }
}