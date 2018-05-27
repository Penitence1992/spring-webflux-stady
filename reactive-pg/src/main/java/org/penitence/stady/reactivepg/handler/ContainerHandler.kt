package org.penitence.stady.reactivepg.handler

import org.penitence.stady.reactivepg.dao.ServiceContainerRepository
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

class ContainerHandler(val repository: ServiceContainerRepository) {

    fun list(request: ServerRequest): Mono<ServerResponse> {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(repository.findAll(), Map::class.java)
    }
}