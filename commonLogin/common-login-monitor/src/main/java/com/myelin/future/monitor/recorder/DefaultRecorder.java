package com.myelin.future.monitor.recorder;


import com.myelin.future.common.constant.CommonConstant;
import com.myelin.future.monitor.logger.MonitorLogger;
import com.myelin.future.monitor.output.Outputter;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.*;

/**
 * Created by gabriel on 14-12-15.
 */
public class DefaultRecorder {

    private static DefaultRecorder recorder = new DefaultRecorder();

    private static ScheduledExecutorService scheduledExecutorService;

    private static LinkedBlockingQueue<Future<String>> outputRequestQueue = new LinkedBlockingQueue<Future<String>>();


    private static RequestAnalysis requestAnalysis;


    public static DefaultRecorder getInstance() {
        return recorder;
    }


    private DefaultRecorder() {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                ArrayList<Future<String>> arrayList = new ArrayList<Future<String>>();
                outputRequestQueue.drainTo(arrayList);
                for (Future<String> msg : arrayList) {
                    try {
                        String info = msg.get();
                        record(info, CommonConstant.LOGGER_ERROR_LEVEL);
                    } catch (InterruptedException e) {
                        record(e.getCause().getMessage(), CommonConstant.LOGGER_ERROR_LEVEL);
                    } catch (ExecutionException e) {
                        record(e.getCause().getMessage(), CommonConstant.LOGGER_ERROR_LEVEL);
                    }
                }
            }
        }, 1000, 5000, TimeUnit.MILLISECONDS);

        requestAnalysis = new RequestAnalysis();
    }


    public void putRequest(HttpServletRequest request) {
        try {
            if (requestAnalysis == null) {
                DefaultRecorder.getInstance();
            }

            Future<String> pendingResult = requestAnalysis.analysisRequest(request);
            if (pendingResult != null) {
                outputRequestQueue.offer(pendingResult, 3000, TimeUnit.MILLISECONDS);
            }
        } catch (InterruptedException e) {
            return;
        }
    }

    public void errorRecord(Exception e, int level) {
        Outputter outputter = new Outputter(MonitorLogger.Error_LOGGER, level);
        outputter.outPutData(e.getCause().getMessage());
    }

    public void backendRecord(String msg, int level) {
        Outputter outputter = new Outputter(MonitorLogger.Backend_LOGGER, level);
        outputter.outPutData(msg);
    }

    static {
        Properties props = new Properties();
        try {
            props.load(DefaultRecorder.class.getClassLoader().getResourceAsStream("logger.properties"));
            MonitorLogger.initLogger(props);
        } catch (IOException e) {

        }
    }


    public void record(String msg, Integer level) {
        Outputter outputter = new Outputter(MonitorLogger.Cookie_LOGGER, level);
        outputter.outPutData(msg);
    }
}
