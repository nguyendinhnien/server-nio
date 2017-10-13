package gsn.base.dispatcher;

import gsn.base.CLogger;
import gsn.base.message.Msg;
import gsn.base.session.Session;
import gsn.server.Server;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by niennguyen on 10/12/17.
 */
public class Dispatcher {
    public void dispatch(Session session, Msg msg) {
        // just broadcast
        Set<Session> sessionSet = new HashSet<>(Server.getInstance().getSessionManager().getSessionMap().values());
        msg.dataContent = msg.dataContent + " from " + session.id;
        for (Session s : sessionSet) {
            msg.receiverId = s.id;
            Server.getInstance().getWriter().addQueueMsg(msg);
            CLogger.log("Dispatching" , msg.commandId);
        }
    }
}
