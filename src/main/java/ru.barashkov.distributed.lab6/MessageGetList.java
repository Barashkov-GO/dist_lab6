package ru.barashkov.distributed.lab6;

import java.util.List;

public class MessageGetList {
    private final List<String> servers;

    public MessageGetList(List<String> servers) {
        this.servers = servers;
    }

    public List<String> getServers() {
        return this.servers;
    }
}
