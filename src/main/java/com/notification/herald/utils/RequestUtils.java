package com.notification.herald.utils;

import java.util.UUID;

public class RequestUtils {

  public static String generateRequestId(){
    StringBuilder requestId = new StringBuilder(UUID.randomUUID().toString());
    requestId.append("-").append(System.currentTimeMillis());
    return requestId.toString();
  }

}
