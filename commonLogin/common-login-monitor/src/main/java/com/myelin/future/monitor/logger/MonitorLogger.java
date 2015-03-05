package com.myelin.future.monitor.logger;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Properties;


/**
 * Created by gabriel on 14-12-16.
 */
public class MonitorLogger {
    public static Logger Cookie_LOGGER = (Logger) LoggerFactory.getLogger("cookie"); //记录用户行为日志
    public static Logger Backend_LOGGER = (Logger) LoggerFactory.getLogger("backend"); //记录用户行为日志
    public static Logger Error_LOGGER = (Logger) LoggerFactory.getLogger("error"); //记录用户行为日志
    public static LoggerContext loggerContext = Cookie_LOGGER.getLoggerContext();


    /**
     * 创建日志记录目录
     *
     * @param path
     * @return
     */
    private static File createLoggerDir(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            try {
                dir.mkdirs();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return dir;
    }

    /**
     * 初始化日志
     *
     * @param props
     */
    public static void initLogger(Properties props) {
        initSpecLogger(props, Cookie_LOGGER);
        initSpecLogger(props, Backend_LOGGER);
        initSpecLogger(props, Error_LOGGER);
    }

    /**
     * 初始化特定的日志
     *
     * @param properties
     * @param logger
     */
    private static void initSpecLogger(Properties properties, Logger logger) {
        LoggerContext context = initLoggerContext(logger);
        FileAppender<ILoggingEvent> pattern = initPatternLayOut(properties, context, logger);
        initLoggerLevel(logger, properties, pattern);
    }


    private static LoggerContext initLoggerContext(Logger logger) {
        logger.setAdditive(false);
        return loggerContext;
    }


    /**
     * 初始化日志的文件布局及轮转策略
     *
     * @param props
     * @param loggerContext
     */
    public static FileAppender<ILoggingEvent> initPatternLayOut(Properties props, LoggerContext loggerContext, Logger logger) {
        PatternLayoutEncoder layoutEncoder = new PatternLayoutEncoder();
        layoutEncoder.setCharset(Charset.forName((String) props.get("logger.charset")));
        layoutEncoder.setImmediateFlush(true);
        layoutEncoder.setPattern((String) props.get("logger." + logger.getName().toLowerCase() + ".layout"));
        layoutEncoder.setContext(loggerContext);
        layoutEncoder.start();


         /*设置appender及policy*/
        RollingFileAppender<ILoggingEvent> appender = new RollingFileAppender<ILoggingEvent>();
        TimeBasedRollingPolicy policy = new TimeBasedRollingPolicy();
        SizeAndTimeBasedFNATP timeTriggerPolicy = new SizeAndTimeBasedFNATP();
        timeTriggerPolicy.setMaxFileSize((String) props.get("logger." + logger.getName().toLowerCase() + ".maxFileSize"));
        policy.setMaxHistory(Integer.parseInt((String) props.get("logger." + logger.getName().toLowerCase() + ".maxHistory")));
        policy.setTimeBasedFileNamingAndTriggeringPolicy(timeTriggerPolicy);
        String fileNamePattern = (String) props.get("logger." + logger.getName().toLowerCase() + ".home") + (String) props.get("logger." + logger.getName().toLowerCase() + ".filePatternName");
        policy.setFileNamePattern(fileNamePattern);
        policy.setParent(appender);
        policy.setContext(loggerContext);

        createLoggerDir((String) props.get("logger." + logger.getName().toLowerCase() + ".home"));
        String path = (String) props.get("logger." + logger.getName().toLowerCase() + ".home") + (String) props.get("logger." + logger.getName().toLowerCase() + ".fileName");
        appender.setFile(path);
        appender.setRollingPolicy(policy);
        appender.setEncoder(layoutEncoder);
        appender.setContext(loggerContext);
        appender.setName(logger.getName().toLowerCase() + "-appender");

        policy.start();
        appender.start();
        return appender;
    }

    private static void initLoggerLevel(Logger logger, Properties props, FileAppender<ILoggingEvent> appender) {
        if (props.getProperty("logger.level").equalsIgnoreCase("info")) {
            logger.setLevel(Level.INFO);
        } else if (props.getProperty("logger.level").equalsIgnoreCase("debug")) {
            logger.setLevel(Level.DEBUG);
        } else if (props.getProperty("logger.level").equalsIgnoreCase("error")) {
            logger.setLevel(Level.ERROR);
        } else if (props.getProperty("logger.level").equalsIgnoreCase("warn")) {
            logger.setLevel(Level.WARN);
        }
        logger.addAppender(appender);
    }
}
