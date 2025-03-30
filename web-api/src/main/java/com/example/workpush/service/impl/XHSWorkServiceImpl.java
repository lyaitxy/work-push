package com.example.workpush.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.workpush.service.XHSWorkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class XHSWorkServiceImpl extends AbstractWorkService implements XHSWorkService {

    @Override
    protected HttpHeaders buildHeaders(String categoryType) {
        return new HttpHeaders();
    }

    @Override
    protected Map<String, Object> buildRequestParams(String key, String categoryType) {
        Map<String, Object> m = new HashMap<>();
        if(categoryType.equals("应届校招")) {
            m.put("campusRecruitTypes", new String[]{"term_regular"});
        } else {
            m.put("campusRecruitTypes", new String[]{"term_intern"});
        }
        m.put("pageNum", 1);
        m.put("pageSize", 20);
        m.put("positionName", key);
        m.put("recruitType", "campus");
        return m;
    }

    @Override
    protected String getRequestUrl(String categoryType) {
        return "https://job.xiaohongshu.com/websiterecruit/position/pageQueryPosition";
    }

    @Override
    protected List<Map<String, Object>> parseResponse(String responseBody) {
        // 解析返回的数据
        JSONArray datas = JSONObject.parseObject(responseBody).getJSONObject("data").getJSONArray("list");
        if(datas == null) return null;
        // 将返回的数据转换为map
        return JSON.parseObject(datas.toJSONString(), List.class);
    }

    @Override
    protected Object extractId(Map<String, Object> data) {
        return data.get("positionId");
    }

    @Override
    protected LocalDate extractModifyTime(Map<String, Object> data) {
        return LocalDate.parse((CharSequence) data.get("publishTime"));
    }

    @Override
    protected String buildDetailUrl(Object id) {
        return "https://job.xiaohongshu.com/campus/position/" + id;
    }
}
