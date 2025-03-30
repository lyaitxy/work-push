package com.example.workpush.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.workpush.service.KuaiShoService;
import com.example.workpush.utils.TimestampToLocalDateTimeDeserializer;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class KuaiShoServiceImpl extends AbstractWorkService implements KuaiShoService {
    @Override
    protected String getCompanyName() {
        return "KS";
    }

    @Override
    protected HttpHeaders buildHeaders(String categoryType) {
        return new HttpHeaders();
    }

    @Override
    protected Map<String, Object> buildRequestParams(String key, String categoryType) {
        Map<String, Object> m = new HashMap<>();
        m.put("recruitSubProjectCodes", new int[]{});
        m.put("pageSize", 20);
        m.put("pageNum", 1);
        m.put("name", key);
        if(categoryType.equals("应届校招")) {
            m.put("positionNatureCode", "fulltime");
        } else m.put("positionNatureCode", "intern");
        return m;
    }

    @Override
    protected String getRequestUrl(String categoryType) {
        return "https://campus.kuaishou.cn/recruit/campus/e/api/v1/open/positions/simple";
    }

    @Override
    protected List<Map<String, Object>> parseResponse(String responseBody) {
        // 解析返回的数据
        JSONArray datas = JSONObject.parseObject(responseBody).getJSONObject("result").getJSONArray("list");
        if(datas == null) return null;
        // 将返回的数据转换为map
        return JSON.parseObject(datas.toJSONString(), List.class);
    }

    @Override
    protected Object extractId(Map<String, Object> data) {
        return data.get("id");
    }

    @Override
    protected LocalDate extractModifyTime(Map<String, Object> data) {
        return TimestampToLocalDateTimeDeserializer.deserialize(data.get("updateTime").toString());
    }

    @Override
    protected String buildDetailUrl(Object id) {
        return "https://campus.kuaishou.cn/recruit/campus/e/#/campus/job-info/" + id;
    }
}
