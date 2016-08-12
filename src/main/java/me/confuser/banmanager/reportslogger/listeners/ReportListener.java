package me.confuser.banmanager.reportslogger.listeners;

import me.confuser.banmanager.bukkitutil.listeners.Listeners;
import me.confuser.banmanager.data.PlayerReportData;
import me.confuser.banmanager.events.PlayerReportDeletedEvent;
import me.confuser.banmanager.events.PlayerReportedEvent;
import me.confuser.banmanager.internal.ormlite.stmt.DeleteBuilder;
import me.confuser.banmanager.reportslogger.ReportsLogger;
import me.confuser.banmanager.reportslogger.data.LogData;
import me.confuser.banmanager.reportslogger.data.ReportLogData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.sql.SQLException;
import java.util.Iterator;

public class ReportListener extends Listeners<ReportsLogger> {

  @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
  public void notifyOnReport(PlayerReportedEvent event) {
    PlayerReportData report = event.getReport();

    Iterator<LogData> iterator = plugin.getAppender().getQueue().iterator();

    // Create many-to-many relationship
    while (iterator.hasNext()) {
      LogData log = iterator.next();

      try {
        plugin.getLogStorage().createIfNotExists(log);
        plugin.getReportLogStorage().create(new ReportLogData(report, log));
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  @EventHandler
  public void reportDeleted(PlayerReportDeletedEvent event) {
    int id = event.getReport().getId();

    DeleteBuilder<ReportLogData, Integer> builder = plugin.getReportLogStorage().deleteBuilder();

    try {
      builder.where().eq("report_id", id);
      builder.delete();
    } catch (SQLException e) {
      e.printStackTrace();
      plugin.getLogger().warning("Failed to delete report associations for " + id);
    }

  }
}
