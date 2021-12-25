package ru.barashkov.distributed.lab6;

import akka.actor.ActorRef;
import akka.http.javadsl.Http;
import akka.http.javadsl.server.Route;
import org.apache.zookeeper.*;

public class ServerStorage implements Watcher {
    private final Http http;
    private final ActorRef actorStorage;
    private final ZooKeeper zooKeeper;
    private final String way;
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
        return route(
                path(
                        way, () -> 
                )

        )


    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        try {
            zooKeeper.getData(way, this, null);
        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
        }
    }
}
