package ru.barashkov.distributed.lab6;


import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.stream.ActorMaterializer;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;

public class ZookeeperApp {
    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("routes");
        ActorRef actorStorage = system.actorOf(Props.create(ActorStorage.class));
        final ActorMaterializer materializer = ActorMaterializer.create(system);

        final Http http = Http.get(system);
        ZooKeeper zk = null;

        try {
            zk = new ZooKeeper(args[0], 3000, null);
            new ZooWatcher(zk, actorStorage);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        List<CompletionStage<ServerBinding>> bindings = new ArrayList<>();
    }
}