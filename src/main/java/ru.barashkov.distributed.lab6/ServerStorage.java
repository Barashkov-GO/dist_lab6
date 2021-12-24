package ru.barashkov.distributed.lab6;

import akka.actor.ActorRef;
import akka.http.javadsl.Http;
import org.apache.zookeeper.ZooKeeper;

public class ServerStorage {
    private Http http;
    private ActorRef actorStorage;
    private ZooKeeper zooKeeper;
    private String way;

    public ServerStorage(Http http, ActorRef actorStorage, ZooKeeper zooKeeper, String way) {
        this.http = http;
        this.actorStorage = actorStorage;
        this.zooKeeper = zooKeeper;
        this.way = way;
    }
}
