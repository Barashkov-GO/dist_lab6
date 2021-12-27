package ru.barashkov.distributed.lab6;

import akka.actor.ActorRef;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.server.Route;
import akka.pattern.Patterns;
import org.apache.zookeeper.*;

import java.time.Duration;

import static akka.http.javadsl.server.Directives.*;

public class ServerStorage implements Watcher {
    private static final String URL = "localhost:";
    private static final String SERVERS = "/servers/";
    private static final String URL_PARAM = "url";
    private static final String COUNT_PARAM = "count";
    private static final String NUMBER_STR = "Ping number = ";
    private static final String ON_STR = " on ";
    private static final String ZERO = "0";
    private static final String PATH = "";
    private static final String URL_FORMAT_STR = "http://%s/?url=%s&count=%d";
    private static final Duration TIMEOUT = Duration.ofMillis(5000);

    private final Http http;
    private final ActorRef actorStorage;
    private final ZooKeeper zooKeeper;
    private final String path;


    public ServerStorage(
                         Http http,
                         ActorRef actorStorage,
                         ZooKeeper zooKeeper,
                         String port
                        ) throws InterruptedException, KeeperException {
        this.http = http;
        this.actorStorage = actorStorage;
        this.zooKeeper = zooKeeper;
        this.path = URL + port;
        zooKeeper.create(
                SERVERS + path,
                path.getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL_SEQUENTIAL
        );
    }

    public Route createRoute() {
        return route(
                path(PATH, () ->
                    route(
                            get(() ->
                                parameter(URL_PARAM, (url) ->
                                parameter(COUNT_PARAM, (count) -> {
                                            System.out.println(NUMBER_STR + count + ON_STR + path);
                                            if (count.equals(ZERO)){
                                                return completeWithFuture(
                                                        http.singleRequest(HttpRequest.create(url))
                                                );
                                            }
                                            return completeWithFuture(
                                                    Patterns.ask(
                                                            actorStorage,
                                                            new MessageGetRandom(),
                                                            TIMEOUT
                                                    ).
                                                    thenCompose(
                                                            res ->
                                                                    http.singleRequest(HttpRequest.create(
                                                                            String.format(
                                                                                    URL_FORMAT_STR,
                                                                                    res,
                                                                                    url,
                                                                                    Integer.parseInt(count) - 1
                                                                            )
                                                                    ))
                                                    )
                                            );
                                }
                                )
                                )
                            )
                    )
                )
        );
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        try {
            zooKeeper.getData(path, this, null);
        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
        }
    }
}
