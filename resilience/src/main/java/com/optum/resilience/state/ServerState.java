package com.optum.resilience.state;

import com.optum.resilience.domain.Server;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ServerState {

    void currentState();

    Server handleState();
}
