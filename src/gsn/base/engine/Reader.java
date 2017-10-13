package gsn.base.engine;

import gsn.base.dispatcher.Dispatcher;
import gsn.base.message.Msg;
import gsn.base.message.Protocol;
import gsn.base.session.Session;
import gsn.base.session.SessionManager;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Queue;

/**
 * Created by niennguyen on 10/12/17.
 */
public class Reader implements Runnable {

    Queue<Session> inboundSessionQueue;
    SessionManager sessionManager;

    Selector readSelector;

    Dispatcher dispatcher;

    ByteBuffer readBuffer = ByteBuffer.allocate(Protocol.SIZE); // 32KB

    public Reader(Queue<Session> socketQueue, SessionManager sessionManager, Dispatcher dispatcher) throws IOException {
        this.inboundSessionQueue = socketQueue;
        this.sessionManager = sessionManager;
        this.readSelector = Selector.open();
        this.dispatcher = dispatcher;
    }

    @Override
    public void run() {
        while (true) {
            try {
                // enqueue inbound session
                acquireNewSession();
                // process read data from channels
                processInputData();
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void acquireNewSession() throws IOException {
        Session newSession = inboundSessionQueue.poll();
        while (newSession != null) {
            sessionManager.addSession(newSession);
            SelectionKey selectionKey = newSession.socket.register(readSelector, SelectionKey.OP_READ);
            selectionKey.attach(newSession);
            System.out.println("Acquired new session " + newSession.id);
            newSession = inboundSessionQueue.poll();
        }
    }

    private void processInputData() throws IOException {
        int numberReady = readSelector.selectNow();
        if (numberReady > 0) {
            Iterator<SelectionKey> keyIterator = readSelector.selectedKeys().iterator();
            while (keyIterator.hasNext()) {
                SelectionKey selectionKey = keyIterator.next();

                Session session = (Session) selectionKey.attachment();
                // process messages
                Protocol.read(session, readBuffer);
                // dispatch msg
                if (!session.incomingMsgList.isEmpty()) {
                    for (Msg msg : session.incomingMsgList) {
                        this.dispatcher.dispatch(session, msg);
                    }
                    session.incomingMsgList.clear();
                }

                if (session.endStream) {
                    System.out.println("Session is terminated: " + session);
                    this.sessionManager.remove(session);
                    selectionKey.attach(null);
                    selectionKey.cancel();
                }

                keyIterator.remove();
            }
        }
    }
}
