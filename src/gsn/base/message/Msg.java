package gsn.base.message;

/**
 * Created by niennguyen on 10/12/17.
 */
public class Msg {
    public int senderId;
    public int receiverId;

    // header
    public int commandId;
    // data
    public String dataContent;

    @Override
    public String toString() {
        return "Msg{" +
                "senderId=" + senderId +
                ", commandId=" + commandId +
                ", dataContent='" + dataContent + '\'' +
                '}';
    }
}
