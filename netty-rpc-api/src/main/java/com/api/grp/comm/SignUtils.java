package com.api.grp.comm;

import java.util.UUID;

public final class SignUtils {

    public static String sign(String genericString){
        String uuid = UUID.nameUUIDFromBytes(genericString.getBytes()).toString();
        return  uuid;
    }
}
