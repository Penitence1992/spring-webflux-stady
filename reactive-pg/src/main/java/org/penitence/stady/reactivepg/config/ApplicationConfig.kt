package org.penitence.stady.reactivepg.config

import org.penitence.stady.reactivepg.dao.ServiceContainerRepository
import org.penitence.stady.reactivepg.handler.ContainerHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.HandlerFunction
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.ServerResponse

import org.springframework.web.reactive.function.server.RequestPredicates.GET
import org.springframework.web.reactive.function.server.RequestPredicates.accept

@Configuration
open class ApplicationConfig(repository: ServiceContainerRepository) {

    val handler: ContainerHandler = ContainerHandler(repository)

    @Bean
    open fun routeHandler(): RouterFunction<ServerResponse> {
        return RouterFunctions.route(
                GET("/v1/containers").and(accept(MediaType.APPLICATION_JSON_UTF8)),
                HandlerFunction { request -> handler.list(request) }
        )
    }
}