package ru.barashkov.distributed.lab6;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

public class ZooWatcher implements Watcher {
    private static final String SERVERS_PATH = "/servers";

    @Override
    public void process(WatchedEvent watchedEvent) {
        try {
            
        }
    }
}
