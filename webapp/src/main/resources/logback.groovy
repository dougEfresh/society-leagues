import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.filter.ThresholdFilter
import ch.qos.logback.core.rolling.RollingFileAppender
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy

import static ch.qos.logback.classic.Level.*
import ch.qos.logback.classic.net.SyslogAppender

import static ch.qos.logback.classic.Level.DEBUG

appender("FILE", RollingFileAppender) {
 def rootDir = new File("/tmp/logs");

  if (!rootDir.exists()) {
     rootDir = new File("build/logs");
  }

  file = rootDir.getAbsolutePath() + "/society-league.log";
  encoder(PatternLayoutEncoder) {
    pattern = "%-5p %d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %c{1} - %m%n"
  }
  filter(ThresholdFilter) { level = TRACE  }
  rollingPolicy(TimeBasedRollingPolicy) {
    fileNamePattern = rootDir.getAbsolutePath()+ "/society-league.log.%d.gz"
  }
}

appender("CONSOLE", ch.qos.logback.core.ConsoleAppender) {
  encoder(PatternLayoutEncoder) {
    pattern = "%d{yyyy-MM-dd HH:mm:ss.SSS} %-5p  [%t] %c{1}:%M:%L - %m%n"
  }
  filter(ThresholdFilter) {
  	level = TRACE  }
}

appender("SYSLOG", SyslogAppender) {
  syslogHost = "localhost"
  facility = "DAEMON"
  suffixPattern = "[%thread] %logger %msg"
}

logger("com.society", DEBUG)
logger("org.springframework.web",INFO)
logger("org.springframework.security",INFO)
logger("org.springframework.web.authentication",DEBUG)
logger("org.springframework.web.social",DEBUG)
logger("org.springframework.data.mongodb",INFO)

root(INFO, ["FILE", "CONSOLE","SYSLOG"])
