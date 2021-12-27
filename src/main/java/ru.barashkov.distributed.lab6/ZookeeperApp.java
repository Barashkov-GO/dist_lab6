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
import java.util.concurrent.CompletionStage;

public class ZookeeperApp {
    private static final String SERVERS_INFO_STR_1 = "Servers:\n";
    private static final String SERVERS_INFO_STR_2 = "localhost";
    private static final String SERVERS_INFO_NEWLINE = "/\n";
    private static final String SERVERS_INFO_ERROR = "No servers online\n";
    private static final String HOST_IP = "localhost";

    private static final int ZOOKEEPER_ADDRESS_ID = 0;
    private static final int ZOOKEEPER_PORT_ID = 1;
    private static final int ZOOKEEPER_TIMEOUT = 5000;

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("routes");
        ActorRef actorStorage = system.actorOf(Props.create(ActorStorage.class));
        final ActorMaterializer materializer = ActorMaterializer.create(system);

        final Http http = Http.get(system);
        ZooKeeper zooKeeper = null;

        try {
                zooKeeper = new ZooKeeper(args[ZOOKEEPER_ADDRESS_ID], ZOOKEEPER_TIMEOUT, null);
                new ZooWatcher(zooKeeper, actorStorage);
        } catch (IOException | InterruptedException | KeeperException e) {
                e.printStackTrace();
                System.exit(-1);
        }

        CompletionStage<ServerBinding> binding = null;
        StringBuilder serversInfo = new StringBuilder(SERVERS_INFO_STR_1);


        try {
            ServerStorage server = new ServerStorage(http, actorStorage, zooKeeper, args[ZOOKEEPER_PORT_ID]);
            final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = server.createRoute().flow(system, materializer);
            binding = http.bindAndHandle(
                        routeFlow,
                        ConnectHttp.toHost(HOST_IP, Integer.parseInt(args[ZOOKEEPER_PORT_ID])),
                        materializer
            );

            System.out.println("Server is starting at http://" + HOST_IP + ":" + Integer.parseInt(args[1]));
            System.in.read();
            System.out.println(serversInfo);

            binding
                    .thenCompose(ServerBinding::unbind)
                    .thenAccept(unbound -> system.terminate());

            serversInfo.
                    append(SERVERS_INFO_STR_2).
                    append(args[ZOOKEEPER_PORT_ID]).
                    append(SERVERS_INFO_NEWLINE);
        } catch (InterruptedException | KeeperException | IOException e) {
            e.printStackTrace();
        }
    }
}