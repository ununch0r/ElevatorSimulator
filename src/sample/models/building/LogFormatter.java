package sample.models.building;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogFormatter extends Formatter {
    @Override
    public String format(LogRecord record) {
        return record.getInstant().atZone(ZoneId.of("+02:00:00")).format(DateTimeFormatter.ofPattern("H:m:s")) + record.getMessage();

    }
}
