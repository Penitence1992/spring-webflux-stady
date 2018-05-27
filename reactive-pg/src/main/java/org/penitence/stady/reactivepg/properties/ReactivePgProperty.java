package org.penitence.stady.reactivepg.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "reactive.pg")
@Getter
@Setter
@Component
public class ReactivePgProperty {

    private String username;
    private String password;
    private String host;
    private String name;
    private int maxPoolSize;
    private int port;
}
