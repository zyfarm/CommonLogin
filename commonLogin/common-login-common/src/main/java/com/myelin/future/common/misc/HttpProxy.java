package com.myelin.future.common.misc;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.*;

/**
 * Created by gabriel on 14-12-8.
 */
public class HttpProxy {

    private static DefaultHttpClient createDefaultHttpClient() {
        HttpParams httpparams = new BasicHttpParams();
        httpparams.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
        DefaultHttpClient httpclient = new DefaultHttpClient(httpparams);
        HttpRequestRetryHandler httpretryhandler = new HttpRequestRetryHandler() {
            @Override
            public boolean retryRequest(IOException arg0, int arg1, HttpContext arg2) {
                // 重试连接三次
                if (arg1 >= 3) {
                    return false;
                }
                return true;
            }
        };

        httpclient.setHttpRequestRetryHandler(httpretryhandler);
        return httpclient;
    }


    public static JSONObject sendPost(String url, HashMap<String, String> hashmap) {
        HttpPost hp = new HttpPost(url);
        // 设置参数
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        Iterator<Map.Entry<String, String>> iter = hashmap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry<String, String>) iter.next();
            params.add(new BasicNameValuePair((String) entry.getKey(), (String) entry.getValue()));
        }

        // 发送HTTP请求
        try {
            hp.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            DefaultHttpClient httpclient = createDefaultHttpClient();
            HttpResponse httpResponse = httpclient.execute(hp);

            //处理http请求的返回值

            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String resContent = EntityUtils.toString(httpResponse.getEntity());
                JSONObject obj = JSONObject.parseObject(resContent);
                return obj;
            }
        } catch (Exception e) {

        }
        return null;
    }
}
