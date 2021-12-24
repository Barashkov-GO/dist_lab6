import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StorageActor extends AbstractActor {
    private List<String> servers = new ArrayList<>();
    private final Random random = new Random();

    private String getRandomServer() {
        ZookeeperApp.print(String.valueOf(servers));
        return this.servers.get(
                random.nextInt(servers.size())
        );
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().
                match(
                        MessageGetRandom.class,
                        message -> sender().tell(getRandomServer(),
                                ActorRef.noSender()
                        )
                ).
                match(
                        MessageGetList.class,
                        message -> servers = message.get
                )
    }

}