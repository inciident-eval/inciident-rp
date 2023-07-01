
package inciident.util.logging;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;


public class TimeStampFormatter implements Formatter {

    private static final String DATE_FORMAT_STRING = "MM/dd/yyyy-HH:mm:ss";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_STRING);

    public static final String getCurrentTime() {
        return DATE_FORMAT.format(new Timestamp(System.currentTimeMillis()));
    }

    @Override
    public void format(StringBuilder message) {
        message.append(getCurrentTime());
        message.append(' ');
    }
}
