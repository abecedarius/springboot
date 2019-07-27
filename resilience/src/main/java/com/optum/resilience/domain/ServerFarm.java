package com.optum.resilience.domain;

import com.optum.resilience.service.FileReplicaService;
import com.optum.resilience.state.DownState;
import com.optum.resilience.state.ServerState;
import com.optum.resilience.state.UpState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/*
This class maintains the server repository with the current state of the complete infrastructure
 */

@Service
public class ServerFarm {

    @Autowired
    FileReplicaService fileReplicaService;

    Map<String,Server> allServers =new HashMap<>();


    public Map<String, Server> getAllServers() {
        return allServers;
    }

    public void setAllServers(Map<String, Server> allServers) {
        this.allServers = allServers;
    }


    public void createServers(String[] hosts) {
        for(String host : hosts) {
            if (this.allServers.get(host) == null) {
                allServers.put(host, new Server(host));
            }
            this.allServers.get(host).setServerState(new UpState(allServers.get(host)));
        }
    }

    public void flipandReplicate(String host,ServerFarm farm) {
        if (this.allServers.get(host).getServerState().getClass().equals(UpState.class)) {
            changeStateTo(host,new DownState(this.allServers.get(host)));
        } else {
            changeStateTo(host,new UpState(this.allServers.get(host)));
        }
        fileReplicaService.replicateFiles(host,farm);
    }

    private void changeStateTo(String host,ServerState newState) {
        this.allServers.get(host).setServerState( newState);
        this.allServers.get(host).getServerState().handleState();
    }

}
