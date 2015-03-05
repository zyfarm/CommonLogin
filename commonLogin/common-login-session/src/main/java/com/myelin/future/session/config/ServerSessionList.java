package com.myelin.future.session.config;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by gabriel on 14-8-6.
 */
public class ServerSessionList {
    private static Set<String> nickKeySet 		= new HashSet<String>();


    public static Set<String> getNickKeySet() {
        return nickKeySet;
    }

    public static void setNickKeySet(Set<String> nickKeySet) {
        ServerSessionList.nickKeySet = nickKeySet;
    }
}
