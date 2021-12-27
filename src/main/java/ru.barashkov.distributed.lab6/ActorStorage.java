package ru.barashkov.distributed.lab6;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;

public class ActorStorage extends AbstractActor {
    private List<String> servers = new ArrayList<>();
    private final Random random = new Random();

    private String getRandomServer() {
        System.out.println(String.valueOf(servers));
        return servers.get(
                random.nextInt(servers.size())
        );
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().
                match(
                        MessageGetRandom.class,
                        message -> sender().tell(
                                getRandomServer(),
                                ActorRef.noSender()
                        )
                ).
                match(
                        MessageGetList.class,
                        message -> servers = message.getServers()
                ).
                build();
    }

}