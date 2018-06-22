package org.penitence.stady.reactivemq.runner.receiver;

import org.penitence.stady.reactivemq.runner.sink.MqInputSink;
import org.penitence.stady.reactivemq.runner.sink.MqOutputSink;
import org.springframework.cloud.stream.annotation.*;
import org.springframework.cloud.stream.reactive.FluxSender;
import reactor.core.publisher.Flux;

@EnableBinding(value = {MqInputSink.class, MqOutputSink.class})
public class MessageReceiver {


    @StreamListener
    public void transmit(
            @Input(MqInputSink.INPUT) Flux<String> input
            , @Output(MqOutputSink.OUTPUT) FluxSender sender
    ) {
        sender.send(input.doOnNext(System.out::println));
    }
}
