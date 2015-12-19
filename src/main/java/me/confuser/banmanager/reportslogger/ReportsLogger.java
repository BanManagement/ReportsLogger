package me.confuser.banmanager.reportslogger;

import lombok.Getter;
import me.confuser.banmanager.BmAPI;
import me.confuser.banmanager.bukkitutil.BukkitPlugin;
import me.confuser.banmanager.internal.mcstats.MetricsLite;
import me.confuser.banmanager.reportslogger.configs.DefaultConfig;
import me.confuser.banmanager.reportslogger.listeners.LogServerAppender;
import me.confuser.banmanager.reportslogger.listeners.ReportListener;
import me.confuser.banmanager.reportslogger.storage.LogStorage;
import me.confuser.banmanager.reportslogger.storage.ReportLogStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

import java.io.IOException;
import java.sql.SQLException;

public class ReportsLogger extends BukkitPlugin {

  @Getter
  private static ReportsLogger plugin;

  @Getter
  private DefaultConfig configuration;
  @Getter
  private LogServerAppender appender;

  @Getter
  private LogStorage logStorage;
  @Getter
  private ReportLogStorage reportLogStorage;

  public void onEnable() {
    plugin = this;

    setupConfigs();
    try {
      setupStorage();
    } catch (SQLException e) {
      getLogger().warning("An error occurred attempting to enable the plugin");
      plugin.getPluginLoader().disablePlugin(this);
      e.printStackTrace();
      return;
    }

    setupListeners();

    try {
      MetricsLite metrics = new MetricsLite(this);
      metrics.start();
    } catch (IOException e) {
      // Failed to submit the stats :-(
    }
  }

  public void onDisable() {
    if (appender == null) return;
    Logger log = (Logger) LogManager.getRootLogger();
    log.removeAppender(appender);
  }

  @Override
  public String getPluginFriendlyName() {
    return "ReportsLogger";
  }

  @Override
  public String getPermissionBase() {
    return null;
  }

  @Override
  public void setupConfigs() {
    configuration = new DefaultConfig();
    configuration.load();
  }

  public void setupStorage() throws SQLException {
    logStorage = new LogStorage(BmAPI.getLocalConnection());
    reportLogStorage = new ReportLogStorage(BmAPI.getLocalConnection());
  }

  @Override
  public void setupCommands() {

  }

  @Override
  public void setupListeners() {
    appender = new LogServerAppender(plugin);

    Logger log = (Logger) LogManager.getRootLogger();
    log.addAppender(appender);

    new ReportListener().register();
  }

  @Override
  public void setupRunnables() {

  }
}
