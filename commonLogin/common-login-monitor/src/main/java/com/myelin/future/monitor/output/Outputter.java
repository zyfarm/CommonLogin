package com.myelin.future.monitor.output;


import com.myelin.future.common.constant.CommonConstant;
import org.slf4j.Logger;


/**
 * 日志输出器
 * Created by gabriel on 14-12-15.
 */
public class Outputter {
    Logger logger;
    Integer level;


    public Outputter(Logger logger, Integer level) {
        this.logger = logger;
        this.level = level;
    }


    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }


    public void outPutData(String msg) {
        if (level.equals(CommonConstant.LOGGER_DEBUG_LEVEL)) {
            logger.debug(msg);
        } else if (level.equals(CommonConstant.LOGGER_INFO_LEVEL)) {
            logger.info(msg);
        } else if (level.equals(CommonConstant.LOGGER_WARN_LEVEL)) {
            logger.warn(msg);
        } else if (level.equals(CommonConstant.LOGGER_ERROR_LEVEL)) {
            logger.error(msg);
        }
    }
}
