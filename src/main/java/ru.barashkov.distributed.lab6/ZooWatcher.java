package ru.barashkov.distributed.lab6;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.ArrayList;
import java.util.List;

public class ZooWatcher implements Watcher {
    private static final String SERVERS_PATH = "/servers";
    private ZooKeeper zooKeeper;

    @Override
    public void process(WatchedEvent watchedEvent) {
        try {
            zooKeeper.getChildren(SERVERS_PATH, this);
            sendAnswer();
        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
        }
    }

    private void sendAnswer() throws InterruptedException, KeeperException {
        List<String> servers = new ArrayList<>();
        for (String s : zooKeeper.getChildren(SERVERS_PATH, this)) {
            servers.add(new String(zooKeeper.getData(SERVERS_PATH + "/" + s, false, null)));
        }
    }
}
