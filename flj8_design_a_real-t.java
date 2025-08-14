import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class BlockchainNotifier {
    private String nodeName;
    private String blockchainNetwork;
    private List<BlockchainEvent> eventQueue;
    private BlockchainSocket blockchainSocket;

    public BlockchainNotifier(String nodeName, String blockchainNetwork) {
        this.nodeName = nodeName;
        this.blockchainNetwork = blockchainNetwork;
        this.eventQueue = new ArrayList<>();
        this.blockchainSocket = new BlockchainSocket();
    }

    public void startNotifier() {
        Thread thread = new Thread(new BlockchainEventListener(blockchainSocket, eventQueue));
        thread.start();
    }

    public void broadcastEvent(BlockchainEvent event) {
        eventQueue.add(event);
    }

    public List<BlockchainEvent> getEventQueue() {
        return eventQueue;
    }

    public static class BlockchainEvent {
        private String eventName;
        private String eventData;

        public BlockchainEvent(String eventName, String eventData) {
            this.eventName = eventName;
            this.eventData = eventData;
        }

        public String getEventName() {
            return eventName;
        }

        public String getEventData() {
            return eventData;
        }
    }

    public static class BlockchainSocket {
        private BlockingQueue<String> messageQueue;

        public BlockchainSocket() {
            this.messageQueue = new LinkedBlockingQueue<>();
        }

        public void sendMessage(String message) {
            messageQueue.add(message);
        }

        public String receiveMessage() {
            try {
                return messageQueue.take();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static class BlockchainEventListener implements Runnable {
        private BlockchainSocket blockchainSocket;
        private List<BlockchainEvent> eventQueue;

        public BlockchainEventListener(BlockchainSocket blockchainSocket, List<BlockchainEvent> eventQueue) {
            this.blockchainSocket = blockchainSocket;
            this.eventQueue = eventQueue;
        }

        @Override
        public void run() {
            while (true) {
                String message = blockchainSocket.receiveMessage();
                BlockchainEvent event = parseEventMessage(message);
                eventQueue.add(event);
            }
        }

        private BlockchainEvent parseEventMessage(String message) {
            // implement event message parsing logic here
            return new BlockchainEvent("event_name", "event_data");
        }
    }
}