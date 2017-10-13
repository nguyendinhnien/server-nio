package gsn.base.engine;

import gsn.base.session.Session;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Queue;

import static java.lang.Thread.sleep;

/**
 * Created by niennguyen on 10/12/17.
 */
public class Acceptor implements Runnable {

    int tcpPort;
    Selector selector;
    ServerSocketChannel serverSocketChannel;
    Queue<Session> socketQueue;

    public Acceptor(int tcpPort, Queue<Session> socketQueue) throws IOException {
        this.tcpPort = tcpPort;

        this.serverSocketChannel = ServerSocketChannel.open();
        this.serverSocketChannel.bind(new InetSocketAddress(this.tcpPort));
        this.serverSocketChannel.configureBlocking(false);

        selector = Selector.open();
        this.socketQueue = socketQueue;
    }

    @Override
    public void run() {
        while (true) {
            // run selector
            try {
                SocketChannel socketChannel = this.serverSocketChannel.accept();
                Thread.sleep(1000);
                if ( socketChannel != null ) {
                    System.out.println("Session was bound: " + socketChannel.toString());
                    this.socketQueue.add(new Session(0, socketChannel));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
