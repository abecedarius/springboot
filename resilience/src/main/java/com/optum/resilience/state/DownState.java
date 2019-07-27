package com.optum.resilience.state;

import com.optum.resilience.domain.Server;

/*
To create the down state with the server
 */
public class DownState  implements ServerState{

    private Server server;

    public DownState(Server server){
        this.server = server;
    }

    @Override
    public void currentState() {
        this.server.setStatus("DOWN");
    }

    @Override
    public Server handleState() {
        this.server.setStatus("DOWN");
        return this.server;
    }
}
