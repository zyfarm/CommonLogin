package com.myelin.future.common.misc;
import org.apache.commons.lang3.StringUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by gabriel on 14-8-25.
 */
public class ParamsCheckUtils {
    private static Pattern cookiePattern = Pattern.compile("^[a-z0-9A-Z]+$");

    public static boolean verifyCookies(String value){
        if(StringUtils.isBlank(value)){
            return false;
        }

        Matcher matcher=cookiePattern.matcher(value);
        if(matcher.find()){
            return true;
        }else{
            return false;
        }
    }


}
