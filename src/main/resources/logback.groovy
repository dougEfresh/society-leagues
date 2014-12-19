
import ch.qos.logback.core.rolling.RollingFileAppender
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.filter.ThresholdFilter
import ch.qos.logback.core.FileAppender

import static ch.qos.logback.classic.Level.DEBUG
import static ch.qos.logback.classic.Level.ERROR
import static ch.qos.logback.classic.Level.WARN

appender("FILE", RollingFileAppender) {
 def rootDir = new File("/data/logs");
  
  if (!rootDir.exists()) {
     rootDir = new File("build/logs");
  }

  file = rootDir.getAbsolutePath() + "/society-league.log";
  encoder(PatternLayoutEncoder) {
    pattern = "%-5p %d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %c{1} - %m%n"
  }
  filter(ThresholdFilter) {
  	level = DEBUG
  }
  rollingPolicy(TimeBasedRollingPolicy) {
    fileNamePattern = rootDir.getAbsolutePath()+ "/society-league.log.%d.gz"
  }
}

appender("CONSOLE", ch.qos.logback.core.ConsoleAppender) {
  encoder(PatternLayoutEncoder) {
    pattern = ".%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg %n"
  }
  filter(ThresholdFilter) {
  	level = DEBUG
  }
}

logger("com.society", DEBUG)
root(INFO, ["FILE", "CONSOLE"])
