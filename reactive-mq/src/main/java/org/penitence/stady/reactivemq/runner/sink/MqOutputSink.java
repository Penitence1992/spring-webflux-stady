package org.penitence.stady.reactivemq.runner.sink;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface MqOutputSink {

    String OUTPUT = "flux-out";

    @Output(MqOutputSink.OUTPUT)
    MessageChannel output();
}
