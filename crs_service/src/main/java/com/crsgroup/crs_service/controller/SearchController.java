package com.crsgroup.crs_service.controller;

import com.crsgroup.crs_service.model.SlotDto;
import com.crsgroup.crs_service.model.Center;
import com.crsgroup.crs_service.service.CenterService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;
import lombok.extern.slf4j.Slf4j;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/crs")
@Slf4j
public class SearchController {

    @Autowired
    private CenterService centerService;

    @Autowired
    private WebClient.Builder webClientBuilder;

    private final ExecutorService pool = Executors.newFixedThreadPool(10);

    @GetMapping("/search")
    public List<SlotDto> search(@RequestParam String serviceType) {

        List<Center> centers = centerService.getActiveCentersFor(serviceType);

        List<CompletableFuture<List<SlotDto>>> futures = centers.stream()
            .map(center -> CompletableFuture.supplyAsync(() -> {
                try {
                    String url = center.getBaseUrl() + "/dcenter/search?serviceType=" +
                                 URLEncoder.encode(serviceType, StandardCharsets.UTF_8);

                    WebClient client = webClientBuilder.build();

                    SlotDto[] slots = client.get()
                            .uri(url)
                            .retrieve()
                            .bodyToMono(SlotDto[].class)
                            .block();  // blocking inside async task (safe here)

                    if (slots == null) return Collections.<SlotDto>emptyList();

                    for (SlotDto s : slots) {
                        s.centreId = center.getCentreId();
                        s.centreName = center.getName();
                    }
                    return Arrays.asList(slots);
                } catch (Exception ex) {
                    log.warn("call to {} failed: {}", center.getBaseUrl(), ex.getMessage());
                    return Collections.<SlotDto>emptyList();
                }
            }, pool))
            .collect(Collectors.toList());

        return futures.stream()
                .map(CompletableFuture::join)
                .flatMap(List::stream)
                .sorted(Comparator.comparing(s -> s.slotTime))
                .collect(Collectors.toList());
    }
}
