package org.penitence.stady.reactivemq.runner.publisher;

import org.penitence.stady.reactivemq.runner.sink.MqOutputSink;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.reactive.FluxSender;
import org.springframework.cloud.stream.reactive.StreamEmitter;
import reactor.core.publisher.Flux;

import java.util.Random;

//@EnableBinding(MqOutputSink.class)
public class MessagePublisher {

    final Random random = new Random();

    @StreamEmitter
    @Output(MqOutputSink.OUTPUT)
    public void emit(FluxSender sender){
        sender.send(Flux.create(sink -> {
            while (true){
                sink.next(random.nextInt());
            }
        }));
    }
}
