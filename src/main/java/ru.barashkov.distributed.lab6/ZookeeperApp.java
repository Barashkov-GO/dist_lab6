package ru.barashkov.distributed.lab6;


import akka.actor.ActorSystem;
import akka.actor.Props;

public class ZookeeperApp {
    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("routes");
        ActorRef storage = system.actorOf(Props.create(StorageActor.class));
        final ActorMaterializer materializer = ActorMaterializer.create(system);
    }
}