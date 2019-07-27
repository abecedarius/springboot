package com.optum.resilience.domain;

import com.optum.resilience.state.DownState;
import com.optum.resilience.state.ServerState;
import com.optum.resilience.state.UpState;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class to store the server state. This can be extended to have the login details and can be driven using the property binding
 */
@AllArgsConstructor
@Builder
@Setter
@Getter
@ToString
public class Server {

    private String host;
    private String status;
    private Set<String> files = new HashSet<>();
    private ServerState serverState;
    public Server(String host) {
        this.host = host;
    }


}
