package com.optum.resilience;

import com.optum.resilience.domain.Server;
import com.optum.resilience.domain.ServerFarm;
import com.optum.resilience.service.FileReplicaService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ServerStateTest {

    @Autowired
    ServerFarm farm;

    @Autowired
    FileReplicaService fileReplicaService;

    /**
    This Client Class will replicate the behaviour for the server farm to always
    maintain the state for 2 files( one Backup) on servers which are in "UP" State

     This can be refactored to continuously ping the remote hosts every 5 mins to load current state of the server farm.

     */

    @Test
    public void testServer(){

        String[] arr = new String[]{"host1","host2","host3","host4","host5"};

        farm.createServers(arr);
        for(String host : arr){
            fileReplicaService.replicateFiles(host,farm);
        }

        checkLogically2Files(farm);


        farm.flipandReplicate("host1",farm);
        checkLogically2Files(farm);

        farm.flipandReplicate("host2",farm);
        checkLogically2Files(farm);

        farm.flipandReplicate("host3",farm);
        checkLogically2Files(farm);

        farm.flipandReplicate("host2",farm);
        checkLogically2Files(farm);

        farm.flipandReplicate("host1",farm);
        checkLogically2Files(farm);

        farm.flipandReplicate("host4",farm);
        checkLogically2Files(farm);

        farm.flipandReplicate("host3",farm);
        checkLogically2Files(farm);

        farm.flipandReplicate("host4",farm);
        checkLogically2Files(farm);

        farm.flipandReplicate("host5",farm);
        checkLogically2Files(farm);
        printFarm(farm);

    }

    private void checkLogically2Files(ServerFarm farm) {
        List<String> allFiles = new ArrayList<>();

        Map<String,Server> newMap =
                farm.getAllServers().entrySet().stream()
                        .filter(u ->  u.getValue().getStatus()=="UP")
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));


        Iterator keys = newMap.keySet().iterator();
        while(keys.hasNext()){
            String key = (String)keys.next();
            Server server = farm.getAllServers().get(key);
            allFiles.addAll(server.getFiles());
        }

        HashMap<String,Integer> map = new HashMap();
        Iterator itr = allFiles.iterator();
        while(itr.hasNext()){
            String fileName = (String)itr.next();
            if(map.get(fileName) != null){
                map.put(fileName,map.get(fileName)+1);
            }
            else{
                map.put(fileName,1);
            }
        }

        Iterator filekeys = map.keySet().iterator();
        while(filekeys.hasNext()){
            String key = (String)filekeys.next();
            Integer value = map.get(key);
            Assert.assertTrue(value==2);
        }
    }


    private void printFarm(ServerFarm farm) {
        farm.getAllServers().forEach((key,value)->System.out.println(key+"::"+value.toString()));
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++");
    }


}
