---
layout: pattern
title: Resilience
folder: resilience
permalink: 
categories: Behavioural
tags:
 - Java
 - SpringBoot
---

## Intent
Always maintain the 2 replica of file name on different servers which are in "UP" state

## Applicability
Use the maintaining file replica on the following cases

* If server goes down, there should be one backup for all its file any other server,
    These files should be replicated to other hosts
* If a server goes up, the files need to be maintained 2 replicas.

## To-Do
* Instead of maintaining the filename, file copy can be performed and maintained. 
* There should be continuous ping on all the hosts to to get the current state of the servers.
    This monitor thread should be scheduled based on configured time , say 5 mins
* The file copy program should run in its own consumer asynchronous thread 
* Because of large data size of files , the files should be copied in chunks using NIO buffers.
* Can consider, file size or count to copy file in the new server
* If server comes UP from DOWN state, its files can be deleted to remove stale data file. 