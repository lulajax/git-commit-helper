package com.junjie.githelper.model;

public record LLMSettings(
    String provider,
    String api_key,
    String model,
    String base_url,
    String proxy_host,
    Integer proxy_port,
    Boolean use_proxy
) {
    // 默认构造器，不使用代理
    public LLMSettings(String provider, String api_key, String model, String base_url) {
        this(provider, api_key, model, base_url, null, null, false);
    }
    
    // 判断是否启用代理
    public boolean isProxyEnabled() {
        return Boolean.TRUE.equals(use_proxy) && proxy_host != null && !proxy_host.trim().isEmpty() && proxy_port != null;
    }
}
