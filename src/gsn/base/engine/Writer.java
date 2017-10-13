package gsn.base.engine;

import gsn.base.CLogger;
import gsn.base.message.Msg;
import gsn.base.message.Protocol;
import gsn.base.session.Session;
import gsn.base.session.SessionManager;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by niennguyen on 10/12/17.
 */
public class Writer implements Runnable {

    public Queue<Msg> outboundMsgQueue;
    SessionManager sessionManager;

    Selector writeSelector;

    ByteBuffer writeBuffer = ByteBuffer.allocate(Protocol.SIZE); // 32KB

    Set<Session> emptytoNonEmptySessionSet;
    Set<Session> nonEmptyToEmptySessionSet;

    public Writer(SessionManager sessionManager) throws IOException {
        this.outboundMsgQueue = new LinkedBlockingQueue<>();
        this.writeSelector = Selector.open();
        this.sessionManager = sessionManager;
        this.emptytoNonEmptySessionSet = new HashSet<>();
        this.nonEmptyToEmptySessionSet = new HashSet<>();
    }

    @Override
    public void run() {
        while (true) {
            try {
                // take new outbound messages
                processOutBoundMsg();
                // unregister empty socketchannel
                unregisterEmptyChannel();
                // register non-empty socketchannel
                registerNonEmptyChannel();
                // process write data
                processOutputData();
                Thread.sleep(50);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void processOutputData() throws IOException {
        int readyKey = writeSelector.selectNow();
        if ( readyKey > 0 ) {
            Iterator<SelectionKey> keyIterator = writeSelector.selectedKeys().iterator();
            System.out.println("Total writer selectedKeys = " + readyKey);
            while ( keyIterator.hasNext() ) {
                SelectionKey selectionKey = keyIterator.next();
                Session session = (Session) selectionKey.attachment();
                if ( session != null ) {
                    if ( !session.outboundMsgQueue.isEmpty() ) {
                        Msg processingMsg = session.outboundMsgQueue.poll();
                        Protocol.write(session, processingMsg, writeBuffer);
                    }
                    if ( session.outboundMsgQueue.isEmpty() ) {
                        nonEmptyToEmptySessionSet.add(session);
                    }
                }
                keyIterator.remove();
            }
        }
    }

    private void registerNonEmptyChannel() throws ClosedChannelException {
        for (Session session : emptytoNonEmptySessionSet ) {
            session.socket.register(writeSelector, SelectionKey.OP_WRITE, session);
            CLogger.log("Register to writeSelector " + session);
        }
        emptytoNonEmptySessionSet.clear();
    }

    private void unregisterEmptyChannel() {
        for (Session session : nonEmptyToEmptySessionSet ) {
            SelectionKey selectionKey =session.socket.keyFor(writeSelector);
            selectionKey.cancel();
            CLogger.log("Remove from writeSelector " + session);
        }
        nonEmptyToEmptySessionSet.clear();
    }

    private void processOutBoundMsg() throws IOException {
        // enqueue and queueing for each session
        Msg outMsg = this.outboundMsgQueue.poll();
        while ( outMsg != null ) {
            Session receiverSession = this.sessionManager.getSessionMap().get(outMsg.receiverId);
            if ( receiverSession != null ) {
                if ( receiverSession.outboundMsgQueue.isEmpty()) {
                    this.emptytoNonEmptySessionSet.add(receiverSession);
                    this.nonEmptyToEmptySessionSet.remove(receiverSession);
                }
                receiverSession.outboundMsgQueue.add(outMsg);
            }
            outMsg = this.outboundMsgQueue.poll();
        }
    }

    public Queue<Msg> getOutboundMsgQueue() {
        return outboundMsgQueue;
    }

    public void addQueueMsg(Msg msg) {
        this.outboundMsgQueue.add(msg);
    }
}
