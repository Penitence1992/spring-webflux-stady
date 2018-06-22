package org.penitence.stady.sse.route.v1;

import org.penitence.stady.sse.service.LoggerService;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/v1/logs")
public class DockerLogSSEController {

    private final LoggerService loggerService;

    public DockerLogSSEController(LoggerService loggerService) {
        this.loggerService = loggerService;
    }

    @GetMapping(value = "/{ref}/logs")
    public Flux<ServerSentEvent<String>> containerLogs(@PathVariable("ref") String ref) {

        return loggerService.findContainerProcessor(ref)
                .map(str -> ServerSentEvent.builder(str).build())
                .doOnCancel(() -> {
                    System.out.println("执行取消");
                    loggerService.processorCancel(ref);
                });

    }
}
