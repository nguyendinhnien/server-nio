package gsn.server;

import gsn.base.dispatcher.Dispatcher;
import gsn.base.engine.*;
import gsn.base.session.Session;
import gsn.base.session.SessionManager;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedTransferQueue;

/**
 * Created by niennguyen on 10/12/17.
 */
public class Server {
    int tcpPort;

    SessionManager sessionManager;
    Dispatcher dispatcher;

    Acceptor acceptor;
    Reader reader;
    Writer writer;

    public static Server instance;

    public Server(int tcpPort) {
        this.tcpPort = tcpPort;
        this.sessionManager = new SessionManager();
        this.dispatcher = new Dispatcher();
    }

    public static Server getInstance() {
        return instance;
    }

    public void start() throws IOException {
        // start core services
        // init inbound socket queue - using blocking_queue for synchronization purpose
        Queue<Session> socketQueue = new LinkedTransferQueue<>();

        // test on single thread
        this.acceptor = new Acceptor(this.tcpPort, socketQueue);
        Executors.newSingleThreadExecutor().execute(this.acceptor);
        this.reader = new Reader(socketQueue, this.sessionManager, this.dispatcher);
        Executors.newSingleThreadExecutor().execute(this.reader);
        this.writer = new Writer(this.sessionManager);
        Executors.newSingleThreadExecutor().execute(this.writer);
    }

    public int getTcpPort() {
        return tcpPort;
    }

    public void setTcpPort(int tcpPort) {
        this.tcpPort = tcpPort;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public Dispatcher getDispatcher() {
        return dispatcher;
    }

    public Acceptor getAcceptor() {
        return acceptor;
    }

    public Reader getReader() {
        return reader;
    }

    public Writer getWriter() {
        return writer;
    }
}
