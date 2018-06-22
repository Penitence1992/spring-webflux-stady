package org.penitence.stady.reactivemq.runner.sink;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface MqInputSink {

    String INPUT = "flux-in";


    @Input(MqInputSink.INPUT)
    SubscribableChannel input();

}
