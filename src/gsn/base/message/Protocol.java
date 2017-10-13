package gsn.base.message;

import gsn.base.CLogger;
import gsn.base.session.Session;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by niennguyen on 10/12/17.
 */
public class Protocol {
    public static final int SIZE = 50;

    public static void read(Session session, ByteBuffer byteBuffer) {
        int bytesRead = -1;
        try {
            bytesRead = session.socket.read(byteBuffer);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println(session.socket);
        }
        // switch to read mode
        byteBuffer.flip();
        if ( bytesRead == -1) {
            session.endStream = true;
            return;
        }
        if ( byteBuffer.remaining() <= 0 ) {
            byteBuffer.clear();
            return;
        }
        // don't care about big packet
        Msg msg = new Msg();
        msg.senderId = session.id;
        msg.commandId = byteBuffer.getInt();
        msg.dataContent = getString(byteBuffer);

        CLogger.log("Read Cmd", msg.commandId, "from", session.id);
        session.incomingMsgList.add(msg);

        byteBuffer.clear();
    }

    public static void write(Session session, Msg msg, ByteBuffer byteBuffer) {
        CLogger.log("Write Cmd", msg.commandId, "to", session.id);
        byteBuffer.putInt(msg.senderId);
        byteBuffer.putInt(msg.commandId);
        putString(msg.dataContent, byteBuffer);
        byteBuffer.flip();
        try {
            session.socket.write(byteBuffer);
        } catch (IOException e) {
            session.outboundMsgQueue.clear();
            e.printStackTrace();
        } finally {
            byteBuffer.clear();
        }
    }

    private static void putString(String s, ByteBuffer byteBuffer) {
        int size = s.length();
        byteBuffer.putInt(size);
        for (int i = 0 ; i < size ; i++) {
            byteBuffer.putChar(s.charAt(i));
        }
    }

    private static String getString(ByteBuffer byteBuffer) {
        int size = byteBuffer.getInt();
        StringBuilder s = new StringBuilder();
        for (int i = 0 ; i < size ; i++) {
            s.append(byteBuffer.getChar());
        }
        return s.toString();
    }
}
