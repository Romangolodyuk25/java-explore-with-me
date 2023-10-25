package ru.practicum;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Component
public class StatsClient {
    public static final String PATH = "http://localhost:9090";

    public static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private RestTemplate rest = new RestTemplate();

    public StatsDtoRequest createHit(StatsDtoRequest statsDtoRequest) {
        HttpEntity<StatsDtoRequest> request = new HttpEntity<>(statsDtoRequest);
        return rest.exchange(PATH + "/hit", HttpMethod.POST, request, StatsDtoRequest.class).getBody();
    }

    public List<StatsDtoResponse> getStats(LocalDateTime start, LocalDateTime end, List<String> uris , Boolean unique) throws URISyntaxException {
        String startTime = start.format(FORMAT);
        String endTime = end.format(FORMAT);
        String newUris = String.join(",", uris);

        Map<String, Object> parameters = Map.of(
                "start", startTime,
                "end", endTime,
                "uris", newUris,
                "unique", unique != null ? unique : false
        );

        return rest.getForObject(PATH + "/stats?start={start}&end={end}&uris={newUris}&unique={unique}", List.class, parameters);
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }
}
