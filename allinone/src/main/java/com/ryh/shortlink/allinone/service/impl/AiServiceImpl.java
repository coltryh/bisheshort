package com.ryh.shortlink.allinone.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ryh.shortlink.allinone.dao.entity.ShortLinkDO;
import com.ryh.shortlink.allinone.service.AiService;
import com.ryh.shortlink.allinone.service.ShortLinkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

    private final ShortLinkService shortLinkService;

    @Value("${minimax.api-key}")
    private String apiKey;

    @Value("${minimax.group-id}")
    private String groupId;

    @Value("${minimax.base-url}")
    private String baseUrl;

    @Value("${minimax.chat-model}")
    private String chatModel;

    @Override
    public Map<String, Object> analyzeLinks(String username, String startDate, String endDate) {
        Map<String, Object> result = new HashMap<>();

        List<ShortLinkDO> links = shortLinkService.listByUsername(username);

        int totalPv = 0;
        int totalUv = 0;
        int totalUip = 0;

        for (ShortLinkDO link : links) {
            totalPv += link.getTotalPv() != null ? link.getTotalPv() : 0;
            totalUv += link.getTotalUv() != null ? link.getTotalUv() : 0;
            totalUip += link.getTotalUip() != null ? link.getTotalUip() : 0;
        }

        result.put("totalPv", totalPv);
        result.put("totalUv", totalUv);
        result.put("totalUip", totalUip);
        result.put("linkCount", links.size());
        result.put("startDate", startDate);
        result.put("endDate", endDate);

        return result;
    }

    @Override
    public String chat(String username, String message, String context) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(baseUrl + "/text/chatcompletion_v2");

            httpPost.setHeader("Authorization", "Bearer " + apiKey);
            httpPost.setHeader("Content-Type", "application/json");

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", chatModel);
            requestBody.put("tokens_to_generate", 1024);
            requestBody.put("temperature", 0.9);

            Map<String, Object> botSetting = new HashMap<>();
            botSetting.put("bot_name", "短链接分析助手");
            botSetting.put("bot_description", "帮助用户分析短链接数据，提供统计和建议");

            Map<String, Object> roleSetting = new HashMap<>();
            roleSetting.put("role", "system");
            roleSetting.put("content", "你是一个短链接管理分析助手，可以帮助用户分析短链接的访问数据，包括PV、UV、IP等统计信息。");

            requestBody.put("bot_setting", new Object[]{botSetting});
            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", context + "\n\n用户问题: " + message);
            requestBody.put("messages", new Object[]{userMessage});

            String jsonBody = JSON.toJSONString(requestBody);
            httpPost.setEntity(new StringEntity(jsonBody, ContentType.APPLICATION_JSON));

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                JSONObject jsonResponse = JSON.parseObject(responseBody);

                if (jsonResponse.containsKey("choices")) {
                    return jsonResponse.getJSONArray("choices")
                            .getJSONObject(0)
                            .getJSONObject("message")
                            .getString("content");
                }

                return "抱歉，AI服务暂时无法响应。";
            }
        } catch (Exception e) {
            log.error("AI chat error", e);
            return "AI服务调用失败: " + e.getMessage();
        }
    }
}
