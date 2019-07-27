package com.optum.resilience.state;

import com.optum.resilience.domain.Server;
import java.util.UUID;

/*
To Create the up state with the server
 */
public class UpState implements ServerState {

    private Server server;

    public UpState(Server server){
        this.server = server;
        this.server.setStatus("UP");
        fileGenerator();
    }

    @Override
    public void currentState() {
        this.server.setStatus("UP");
    }

    @Override
    public Server handleState() {
        this.server.setStatus("UP");
        return this.server;
    }

    private void fileGenerator() {
        for(int i=0;i<2;i++){
            this.server.getFiles().add(fileNameGenerator()+i);
        }
    }

    private String fileNameGenerator(){
        return UUID.randomUUID().toString();
    }
}
