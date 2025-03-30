package com.example.workpush.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
public abstract class AbstractWorkService {
    @Resource
    protected RestTemplate restTemplate;
    @Resource
    protected RedisTemplate<String, Object> redisTemplate;

    // 模板方法，增加一个获取请求头的步骤
    public String pushWork(String to, String categoryType, String key) {
        StringBuilder sb = new StringBuilder();

        Map<String, Object> params = buildRequestParams(key, categoryType);
        String url = getRequestUrl(categoryType);
        HttpHeaders headers = buildHeaders(categoryType);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
        List<Map<String, Object>> dataList = parseResponse(response.getBody());
        if(dataList == null) return null;
        LocalDate now = getCurrentTime();

        for (Map<String, Object> data : dataList) {
            Object id = extractId(data);
            LocalDate modifyTime = extractModifyTime(data);
            log.info("id: {}, modifyTime: {}, now：{}, company: {}", id, modifyTime, now, getCompanyName());
            if (modifyTime.isEqual(now)) {
                boolean isPush = Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(to + categoryType + key, getCompanyName() + id));
                if (!isPush) {
                    redisTemplate.opsForSet().add(to + categoryType + key, getCompanyName() + id);
                    sb.append(buildDetailUrl(id)).append("\n");
                }
            }
        }
        return sb.toString();
    }

    // 各个子类必须实现以下抽象方法，以适应不同平台的差异

    // 获取当前的公司名称
    protected abstract String getCompanyName();

    // 请求头实现，可以根据需要在子类中重写加入 token 等信息
    protected abstract HttpHeaders buildHeaders(String categoryType);

    // 构建请求参数
    protected abstract Map<String, Object> buildRequestParams(String key, String categoryType);

    // 返回请求 URL
    protected abstract String getRequestUrl(String categoryType);

    // 解析响应，提取数据列表
    protected abstract List<Map<String, Object>> parseResponse(String responseBody);

    // 提取唯一标识 ID
    protected abstract Object extractId(Map<String, Object> data);

    // 从数据中提取修改时间
    protected abstract LocalDate extractModifyTime(Map<String, Object> data);

    // 生成详情页 URL，返回对应链接
    protected abstract String buildDetailUrl(Object id);

    // 获取当前时间，可以统一用 LocalDate.now()
    protected LocalDate getCurrentTime() {
        return LocalDate.now();
    }

}
