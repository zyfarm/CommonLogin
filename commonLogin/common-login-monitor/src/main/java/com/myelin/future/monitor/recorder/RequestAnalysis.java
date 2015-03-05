package com.myelin.future.monitor.recorder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by gabriel on 14-12-16.
 */
public class RequestAnalysis {

    private ExecutorService workerPool;

    public RequestAnalysis() {
        workerPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), new ThreadFactory() {
            AtomicInteger tid = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, "sesssion-worker-pool-tid-" + tid);
                return t;
            }
        });
    }

    /*监控分析*/
    private String analysis(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        StringBuffer cookiesSB = new StringBuffer();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                cookiesSB.append("cookieName=");
                cookiesSB.append(cookie.getName());
                cookiesSB.append("|");
                cookiesSB.append("cookieValue=");
                cookiesSB.append(cookie.getValue());
                cookiesSB.append("|");
            }

        }
        StringBuffer sb = new StringBuffer();
        sb.append("[remoteIP]-")
                .append(request.getRemoteAddr())
                .append("[targetUrl]-")
                .append(request.getRequestURL())
                .append("[cookies]-")
                .append(cookiesSB.toString());

        return sb.toString();
    }

    private class AnaylysisRequestWorker implements Callable<String> {
        HttpServletRequest request;

        private AnaylysisRequestWorker(HttpServletRequest request) {
            this.request = request;
        }

        @Override
        public String call() throws Exception {
            return analysis(this.request);
        }
    }

    public Future<String> analysisRequest(HttpServletRequest request) {
        try {
            Future<String> result = workerPool.submit(new AnaylysisRequestWorker(request));
            return result;
        } catch (Exception e) {
            return null;
        }
    }
}
