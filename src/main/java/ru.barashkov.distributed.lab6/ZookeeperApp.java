package ru.barashkov.distributed.lab6;


import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;

public class ZookeeperApp {
    private static final String SERVERS_INFO_1 = "Servers:\n";
    private static final String SERVERS_INFO_2 = "http://localhost:";
    private static final String SERVERS_INFO_NEWLINE = "/\n";
    private static final String SERVERS_INFO_ERROR = "No servers online\n";
    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("routes");
        ActorRef actorStorage = system.actorOf(Props.create(ActorStorage.class));
        final ActorMaterializer materializer = ActorMaterializer.create(system);

        final Http http = Http.get(system);
        ZooKeeper zk = null;

        try {
            zk = new ZooKeeper(args[0], 3000, null);
            new ZooWatcher(zk, actorStorage);
        } catch (IOException | InterruptedException | KeeperException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        List<CompletionStage<ServerBinding>> bindings = new ArrayList<>();
        StringBuilder serversInfo = new StringBuilder(SERVERS_INFO_1);


        for (int i = 1; i < args.length; i++) {
            try {
                ServerStorage server = new ServerStorage(http, actorStorage, zk, args[i]);
                final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = server.createRoute().flow(system, materializer);
                bindings.add(http.bindAndHandle(
                        routeFlow,
                        ConnectHttp.toHost("", Integer.parseInt(args[i])),
                        materializer
                ));
                serversInfo.append(SERVERS_INFO_2).append(args[i]).append(SERVERS_INFO_NEWLINE);
            } catch (InterruptedException | KeeperException e) {
                e.printStackTrace();
            }
        }

        if (bindings.size() == 0) {
            System.err.println();
        }
        System.out.println(serversInfo);

        for (CompletionStage<ServerBinding> binding : bindings) {
            binding
                    .thenCompose(ServerBinding::unbind)
                    .thenAccept(unbound -> system.terminate());
        }
    }
}