package com.ryh.shortlink.allinone.service;

import java.util.Map;

public interface AiService {

    Map<String, Object> analyzeLinks(String username, String startDate, String endDate);

    String chat(String username, String message, String context);
}
