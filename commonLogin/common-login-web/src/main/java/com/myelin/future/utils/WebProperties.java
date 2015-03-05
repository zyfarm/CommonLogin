package com.myelin.future.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

/**
 * Created by gabriel on 14-12-28.
 */
public class WebProperties {

    public static Map<String, String> props = null;
    public static List<String> authorizedHost = new ArrayList<String>();

    public static boolean isAutorizedHosts(String redirectUrl) {
        if (authorizedHost.size() == 0) {
            return true;
        }

        String redirectUrlDecode = null;
        try {
            redirectUrlDecode = URLDecoder.decode(redirectUrl, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            redirectUrlDecode = URLDecoder.decode(redirectUrl);

        }
        if (redirectUrlDecode != null) {
            String url = null;
            if (redirectUrlDecode.startsWith("http")) {
                url = redirectUrlDecode.substring(7);
            } else if (redirectUrl.startsWith("https")) {
                url = redirectUrlDecode.substring(8);
            }
            for (String authUrl : authorizedHost) {
                if (url.contains(authUrl)) {
                    return true;
                }
            }
        }
        return false;
    }


    static {
        Properties properties = new Properties();
        HashMap<String, String> tmpProps = new HashMap<String, String>();
        try {
            properties.load(WebProperties.class.getClassLoader().getResourceAsStream("login.properties"));
            Set<Map.Entry<Object, Object>> entries = properties.entrySet();
            Iterator<Map.Entry<Object, Object>> iter = entries.iterator();
            while (iter.hasNext()) {
                Map.Entry<Object, Object> item = iter.next();
                tmpProps.put(item.getKey().toString(), item.getValue().toString());
            }
            props = Collections.unmodifiableMap(tmpProps);


            String authoriedHost = props.get("authorizedHost");
            if (StringUtils.isNotBlank(authoriedHost) && !authoriedHost.equals("*")) {
                String[] authHosts = authoriedHost.split(",");
                authorizedHost = Arrays.asList(authHosts);
            }

        } catch (IOException e) {

        }
    }
}
