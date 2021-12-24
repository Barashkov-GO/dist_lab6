package ru.barashkov.distributed.lab6;


import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.Http;
import akka.stream.ActorMaterializer;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

public class ZookeeperApp {
    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("routes");
        ActorRef storage = system.actorOf(Props.create(ActorStorage.class));
        final ActorMaterializer materializer = ActorMaterializer.create(system);

        final Http http = Http.get(system);
        ZooKeeper zk = null;

        try {
            zk = new ZooKeeper(args[0], 3000, null);
            new ZooWatcher
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}