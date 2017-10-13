package gsn.base.session;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by niennguyen on 10/12/17.
 */
public class SessionManager {
    AtomicInteger sessionId;
    Map<Integer, Session> sessionMap;

    public SessionManager() {
        this.sessionMap = new ConcurrentHashMap<>();
        this.sessionId = new AtomicInteger(0);
    }

    public Map<Integer, Session> getSessionMap() {
        return sessionMap;
    }

    public void setSessionMap(Map<Integer, Session> sessionMap) {
        this.sessionMap = sessionMap;
    }

    public void addSession(Session session) throws IOException {
        int newId = sessionId.getAndIncrement();
        session.id = newId;
        session.socket.configureBlocking(false);
        sessionMap.put(newId, session);
    }

    public void remove(Session session) {
        sessionMap.remove(session.id);
    }
}
