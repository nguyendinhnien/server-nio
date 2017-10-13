package gsn.base;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by niennguyen on 10/13/17.
 */
public class CLogger {
    public static void log(Object... objects) {
        TimeZone tz = TimeZone.getTimeZone("UTC+7");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm,SSS");
        df.setTimeZone(tz);
        StringBuilder builder = new StringBuilder(String.format("%s - Server %d: ", df.format(new Date()),  0));
        for (Object o : objects) {
            builder.append(", ").append(o);
        }
        System.out.println(builder.toString());
    }
}
