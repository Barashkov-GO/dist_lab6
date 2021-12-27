package ru.barashkov.distributed.lab6;

import akka.actor.ActorRef;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.ArrayList;
import java.util.List;

public class ZooWatcher implements Watcher {
    private static final String SERVERS_PATH = "/servers";
    private final ZooKeeper zooKeeper;
    private final ActorRef actorStorage;

    public ZooWatcher(ZooKeeper zooKeeper, ActorRef actorStorage) throws InterruptedException, KeeperException {
        this.zooKeeper = zooKeeper;
        this.actorStorage = actorStorage;
        sendAnswer(zooKeeper.getChildren(SERVERS_PATH, this));
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        try {
            sendAnswer(zooKeeper.getChildren(SERVERS_PATH, this));
        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
        }
    }

    private void sendAnswer(List<String> children) throws InterruptedException, KeeperException {
        List<String> servers = new ArrayList<>();
        for (String s : children) {
            servers.add(new String(zooKeeper.getData(SERVERS_PATH + "/" + s, false, null)));
        }
        actorStorage.tell(
                new MessageGetList(servers),
                ActorRef.noSender()
        );
    }
}
