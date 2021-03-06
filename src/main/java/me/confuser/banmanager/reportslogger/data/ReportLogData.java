package me.confuser.banmanager.reportslogger.data;

import lombok.Getter;
import lombok.Setter;
import me.confuser.banmanager.data.PlayerReportData;
import me.confuser.banmanager.internal.ormlite.field.DatabaseField;
import me.confuser.banmanager.internal.ormlite.table.DatabaseTable;

@DatabaseTable
public class ReportLogData {

  @Getter
  @Setter
  @DatabaseField(generatedId = true)
  private int id;

  @Getter
  @DatabaseField(index = true, canBeNull = false, foreign = true, foreignAutoRefresh = true)
  private PlayerReportData report;

  @Getter
  @DatabaseField(index = true, canBeNull = false, foreign = true, foreignAutoRefresh = true)
  private LogData log;

  ReportLogData() {
  }

  public ReportLogData(PlayerReportData report, LogData log) {
    this.report = report;
    this.log = log;
  }
}
