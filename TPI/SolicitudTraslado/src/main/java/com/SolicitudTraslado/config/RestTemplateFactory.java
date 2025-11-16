package com.SolicitudTraslado.config;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RestTemplateFactory {
    
    public RestTemplate conToken(String bearerToken) {
        RestTemplate rt = new RestTemplate();
        rt.setInterceptors(List.of((request, body, execution) -> {
            request.getHeaders().setBearerAuth(bearerToken);
            return execution.execute(request, body);
        }));
        return rt;
    }

}