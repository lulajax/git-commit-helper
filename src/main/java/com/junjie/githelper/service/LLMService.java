package com.junjie.githelper.service;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.junjie.githelper.model.LLMSettings;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;
import java.util.Map;

public class LLMService {

    public String generateCommitMessage(LLMSettings settings, String customPrompt, String diffContent) {
        String fullPrompt = customPrompt + "\n\n" + diffContent;

        // OpenAI API compatible request body
        Map<String, Object> requestBody = Map.of(
                "model", settings.model(),
                "messages", List.of(
                        Map.of("role", "user", "content", fullPrompt)
                )
        );
        Gson gson = new Gson();

        String jsonBody = gson.toJson(requestBody);

        // 构建 HTTP 请求
        HttpRequest request = HttpRequest.post(settings.base_url() + "/chat/completions")
                .header("Authorization", "Bearer " + settings.api_key())
                .header("Content-Type", "application/json")
                .body(jsonBody)
                .timeout(30000); // 30 seconds timeout

        // 如果启用了代理，则设置代理
        if (settings.isProxyEnabled()) {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, 
                new InetSocketAddress(settings.proxy_host(), settings.proxy_port()));
            request.setProxy(proxy);
        }

        HttpResponse response = request.execute();

        if (response.isOk()) {
            String responseBody = response.body();
            // Parse the response to get the content of the message
            JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
            return jsonResponse.getAsJsonArray("choices")
                               .get(0).getAsJsonObject()
                               .getAsJsonObject("message")
                               .get("content").getAsString();
        } else {
            throw new RuntimeException("Failed to generate commit message: " + response.getStatus() + " " + response.body());
        }
    }
}

