package ru.barashkov.distributed.lab6;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.ArrayList;
import java.util.List;

public class ZooWatcher implements Watcher {
    private static final String SERVERS_PATH = "/servers";

    @Override
    public void process(WatchedEvent watchedEvent) {
        try {
            zooKeeper.getChildren(SERVERS_PATH, this);
            sendAnswer();
        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
        }
    }

    private void sendAnswer() {
        List<String> servers = new ArrayList<>();
        for (String s : )
    }
}
