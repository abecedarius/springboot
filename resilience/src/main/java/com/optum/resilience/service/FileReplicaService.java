package com.optum.resilience.service;

import com.optum.resilience.domain.Server;
import com.optum.resilience.domain.ServerFarm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class FileReplicaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileReplicaService.class.getName());

    /**
     * Generic method to handle the replica,
     * this method can be run in the asynchronous independent thread, so that it can run in its own lifecycle,
     * independent of the server monitoring thread
     * @param host
     * @param farm
     */
    public void replicateFiles(String host, ServerFarm farm){

        backupfilesOnDifferentHost(host,farm);
    }

    /*
    This method replicates the logic to always maintain one backup file whether the server is changing state from up or down
     */
    public void backupfilesOnDifferentHost(String host, ServerFarm farm){
        Set<String> filesName = farm.getAllServers().get(host).getFiles();
        String status = farm.getAllServers().get(host).getStatus();
        Map<String,Server> newMap =
                farm.getAllServers().entrySet().stream()
                        .filter(u -> (!u.getKey().equals(host) && u.getValue().getStatus()=="UP" ))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        handleReplicateServerDown(host,filesName, status,newMap);
        handleReplicateServerUP(filesName, status, newMap);
    }
    /*
    This method handle the file replica if the current host goes down
     */
    private void handleReplicateServerDown(String hostName,Set<String> filesName, String status,Map<String, Server> newMap) {
        if(status == "DOWN") {
            for (String fileName : filesName) {
                long fileCount = newMap.entrySet().stream()
                        .filter(u -> u.getValue().getFiles().contains(fileName))
                        .count();
                if(fileCount == 0){
                    LOGGER.error("There is danger: System is not able to find Replica of file "+fileName+ " on server"+hostName);
                }
                if(fileCount == 1){
                    Optional<Map.Entry<String,Server>>  fileContainingMap= newMap.entrySet().stream()
                            .filter(u -> u.getValue().getFiles().contains(fileName))
                            .findFirst();
                    Optional<Map.Entry<String,Server>> firstServerWithoutFile = newMap.entrySet().stream()
                     .filter(u -> !u.equals(fileContainingMap.get()))
                     .findFirst();
                     firstServerWithoutFile.get().getValue().getFiles().add(fileName);
                }
            }
        }
    }

    /*
    This method maintains one file replica if the current host goes up
 */
    private void handleReplicateServerUP(Set<String> filesName, String status, Map<String, Server> newMap) {
        if(status == "UP"){

            for(String filename : filesName){
               long fileCount = newMap.entrySet().stream()
                       .filter(u -> u.getValue().getFiles().contains(filename))
                       .count();
               if(fileCount == 0){
                   Optional<Map.Entry<String,Server>> firstServer = newMap.entrySet().stream().findFirst();
                   if(firstServer.isPresent()) {
                       firstServer.get().getValue().getFiles().add(filename);
                   }
               }
               if(fileCount >1){
                    while(fileCount != 1){
                       Optional<Map.Entry<String,Server>>  fileContainingMap= newMap.entrySet().stream()
                        .filter(u -> u.getValue().getFiles().contains(filename))
                        .findFirst();
                       fileContainingMap.get().getValue().getFiles().remove(filename);
                       fileCount--;
                    }
               }
            }

        }
    }

    public void findSource(Server down, List<Server> serverFarm){

    }

    public void replicateFile(String file, String source, String destination){

    }

}
