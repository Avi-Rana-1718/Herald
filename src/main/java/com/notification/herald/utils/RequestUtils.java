package com.notification.herald.utils;

import java.util.UUID;

public class RequestUtils {

  public static String generateRequestId(){
      return UUID.randomUUID() + "-" + System.currentTimeMillis();
  }

}
