package ru.practicum;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Component
public class StatsClient {

    static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Value("${stats-server.url}")
    private String path;
    private RestTemplate rest = new RestTemplate();


    public StatsDtoRequest createHit(StatsDtoRequest statsDtoRequest) {
        HttpEntity<StatsDtoRequest> request = new HttpEntity<>(statsDtoRequest, defaultHeaders());
        return rest.exchange(path + "/hit", HttpMethod.POST, request, StatsDtoRequest.class).getBody();
    }

    public List<StatsDtoResponse> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        String startTime = start.format(FORMAT);
        String endTime = end.format(FORMAT);

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(path)
                .path("/stats")
                .queryParam("start", startTime)
                .queryParam("end", endTime)
                .queryParam("uris", uris)
                .queryParam("unique", unique);

        URI uriString = uriComponentsBuilder.build().toUri();

        StatsDtoResponse[] response = rest.getForObject(uriString, StatsDtoResponse[].class);

        return response != null ? List.of(response) : Collections.emptyList();
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }
}
