package com.example.workpush.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.workpush.service.JingDongWorkService;
import com.example.workpush.utils.TimestampToLocalDateTimeDeserializer;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class JingDongWorkServiceImpl extends AbstractWorkService implements JingDongWorkService {

    @Override
    protected String getCompanyName() {
        return "JingDong";
    }

    @Override
    protected HttpHeaders buildHeaders(String categoryType) {
        return new HttpHeaders();
    }

    @Override
    protected Map<String, Object> buildRequestParams(String key, String categoryType) {
        Map<String, Object> m = new HashMap<>();
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("positionName", key);
        if(categoryType.equals("应届校招")) {
            parameterMap.put("planIdList", new int[]{46});
        } else {
            parameterMap.put("planIdList", new int[]{45});
        }
        parameterMap.put("jobDirectionCodeList", new int[]{});
        parameterMap.put("workCityCodeList", new int[]{});
        parameterMap.put("positionDeptList", new int[]{});
        m.put("pageIndex", 0);
        m.put("pageSize", 20);
        m.put("parameter", parameterMap);
        return m;
    }

    @Override
    protected String getRequestUrl(String categoryType) {
        return "https://campus.jd.com/api/wx/position/page";
    }

    @Override
    protected List<Map<String, Object>> parseResponse(String responseBody) {
        JSONArray datas = JSONObject.parseObject(responseBody).getJSONObject("body").getJSONArray("items");
        if(datas == null) return null;
        // 将返回的数据转换为map
        return JSON.parseObject(datas.toJSONString(), List.class);
    }

    @Override
    protected Object extractId(Map<String, Object> data) {
        return data.get("publishId");
    }

    @Override
    protected LocalDate extractModifyTime(Map<String, Object> data) {
        return TimestampToLocalDateTimeDeserializer.deserialize(data.get("publishTime").toString());
    }

    @Override
    protected String buildDetailUrl(Object id) {
        return "https://campus.jd.com/#/details?id=" + id;
    }
}
