package gsn.base.session;

import gsn.base.message.Msg;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by niennguyen on 10/12/17.
 */
public class Session {
    public int id;
    public SocketChannel socket;

    public List<Msg> incomingMsgList;
    public Queue<Msg> outboundMsgQueue;

    public boolean endStream;

    public Session(int id, SocketChannel socket) {
        this.id = id;
        this.socket = socket;
        this.incomingMsgList = new ArrayList<>();
        this.outboundMsgQueue = new LinkedBlockingQueue<>();
        this.endStream = false;
    }

    @Override
    public String toString() {
        try {
            return "Session{" +
                    "id=" + id +
                    ", socket=" + socket.getRemoteAddress() +
                    '}';
        } catch (IOException e) {
            return "Session{" +
                    "id=" + id +
                    ", socket=" + "Exception" +
                    '}';
        }
    }
}
