package ru.barashkov.distributed.lab6;


import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.stream.ActorMaterializer;

public class ZookeeperApp {
    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("routes");
        ActorRef storage = system.actorOf(Props.create(ActorStorage.class));
        final ActorMaterializer materializer = ActorMaterializer.create(system);

        final Http http = Http.get(system)
    }
}