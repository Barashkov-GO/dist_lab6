package ru.barashkov.distributed.lab6;

import akka.actor.ActorRef;
import akka.http.javadsl.Http;
import akka.http.javadsl.server.Route;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

public class ServerStorage {
    private Http http;
    private ActorRef actorStorage;
    private ZooKeeper zooKeeper;
    private String way;
    private static final String URL = "localhost:";
    private static final String SERVERS = "/servers/";

    public ServerStorage(Http http, ActorRef actorStorage, ZooKeeper zooKeeper, String port) throws InterruptedException, KeeperException {
        this.http = http;
        this.actorStorage = actorStorage;
        this.zooKeeper = zooKeeper;
        this.way = URL + port;
        zooKeeper.create(
                SERVERS + way,
                way.getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL_SEQUENTIAL
        );
    }

    public Route createRoute() {
        

    }
}
