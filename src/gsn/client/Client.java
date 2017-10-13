package gsn.client;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Date;

/**
 * Created by niennguyen on 10/12/17.
 */
public class Client implements Runnable {
    int id;
    Scenario scenario;

    static final int SIZE = 50;

    ByteBuffer writeByteBuffer = ByteBuffer.allocate(SIZE);

    public Client(int id, Scenario scenario) {
        this.id = id;
        this.scenario = scenario;
    }

    public int getId() {
        return id;
    }

    public void sendHelloMsg(OutputStream outputStream, int i) throws IOException {
        writeByteBuffer.putInt(i);
        putString("Hello ", writeByteBuffer);
        writeByteBuffer.flip();
        byte[] bytes = writeByteBuffer.array();
        outputStream.write(bytes);
        log("Sent msg ", i, bytesToHex(bytes));
        writeByteBuffer.clear();
    }

    public void receiveMsg(InputStream inputStream) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        if ( inputStream.available() <= 0 ) return;
        byte[] readByteArray = new byte[dataInputStream.available()];
        int bytes = dataInputStream.read(readByteArray);
        ByteBuffer byteBuffer = ByteBuffer.allocate(bytes);
        byteBuffer.put(readByteArray, 0, readByteArray.length);
        byteBuffer.flip();
        int socketId = byteBuffer.getInt();
        int cmdId = byteBuffer.getInt();
        String msg = getString(byteBuffer);
        log(String.format("Receive msg %s  - bytes %s", msg, bytesToHex(readByteArray)));
        byteBuffer.clear();
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

    @Override
    public void run() {
        try {
            Socket socket = new Socket("127.0.0.1", 9999);
            System.out.println("Status " + socket + " -> " + socket.isConnected() + " - " + socket.isInputShutdown());
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            int lifeTime = 0;
            int i = 0;
            while (lifeTime < scenario.lifeTimeSecond ) {
                // logic
                if (i < scenario.numberMessage) {
                    sendHelloMsg(outputStream, i);
                    i++;
                }

                receiveMsg(inputStream);

                Thread.sleep(1000);
                lifeTime++;
            }

            inputStream.close();
            outputStream.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }

    void log(Object... objects) {
        StringBuilder builder = new StringBuilder(String.format("%s - Client %d: ",(new Date()).toString(), id));
        for (Object o : objects) {
            builder.append(",").append(o);
        }
        System.out.println(builder.toString());
    }

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
