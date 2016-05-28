package me.confuser.banmanager.reportslogger.listeners;

import com.google.common.collect.EvictingQueue;
import lombok.Getter;
import me.confuser.banmanager.reportslogger.ReportsLogger;
import me.confuser.banmanager.reportslogger.data.LogData;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.util.regex.Pattern;

public class LogServerAppender extends AbstractAppender {

  private ReportsLogger plugin;
  @Getter
  private EvictingQueue<LogData> queue;

  public LogServerAppender(ReportsLogger plugin) {
    super("Log4JAppender", null,
            PatternLayout.createLayout(
                    "[%d{HH:mm:ss} %level]: %msg",
                    null, null, null, null), false);

    this.plugin = plugin;
    queue = EvictingQueue.create(plugin.getConfiguration().getAmount());
  }

  @Override
  public boolean isStarted() {
    return true;
  }

  @Override
  public void append(LogEvent log) {
    String message = log.getMessage().getFormattedMessage();

    if (message == null) return;

    boolean ignore = false;

    for (String check : plugin.getConfiguration().getContains()) {
      if (message.contains(check)) {
        ignore = true;
        break;
      }
    }

    if (!ignore) {
      for (Pattern pattern : plugin.getConfiguration().getPatterns()) {
        if (pattern.matcher(message).matches()) {
          ignore = true;
          break;
        }
      }
    }

    if (!ignore) {
      // Not thread safe, sync this if causes issues
      queue.add(new LogData(message, log.getMillis() / 1000L));
    }
  }
}
