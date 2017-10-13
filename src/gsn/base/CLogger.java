package gsn.base;

import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Created by niennguyen on 10/13/17.
 */
public class CLogger {
    public static void log(Object... objects) {
        StringBuilder builder = new StringBuilder(String.format("%s - Server %d: ", (new Date()).toString(),  0));
        for (Object o : objects) {
            builder.append(", ").append(o);
        }
        System.out.println(builder.toString());
    }
}
