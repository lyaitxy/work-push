package com.example.workpush.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.workpush.service.MeiTuanWorkService;
import com.example.workpush.utils.TimestampToLocalDateTimeDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
public class MeiTuanWorkServiceImpl extends AbstractWorkService implements MeiTuanWorkService {

    @Override
    protected HttpHeaders buildHeaders(String categoryType) {
        return new HttpHeaders();
    }

    @Override
    protected Map<String, Object> buildRequestParams(String key, String categoryType) {
        Map<String, Object> m = new HashMap<>();
        m.put("cityList", new int[]{});
        m.put("department", new int[]{});
        m.put("jfJgList", new int[]{});
        m.put("jobShareType", "1");
        List<Map<String, Object>> jobTypeList = new ArrayList<>();
        Map<String, Object> jobTypeEntry = new HashMap<>();
        if(categoryType.equals("应届校招")) {
            jobTypeEntry.put("code", "1");
            jobTypeEntry.put("subCode", Arrays.asList("1", "3", "4"));
            jobTypeList.add(jobTypeEntry);
            m.put("jobType", jobTypeList);
        } else if(categoryType.equals("暑期实习")) {
            jobTypeEntry.put("code", "2");
            jobTypeEntry.put("subCode", List.of("2"));
            jobTypeList.add(jobTypeEntry);
            m.put("jobType", jobTypeList);
        } else {
            jobTypeEntry.put("code", "2");
            jobTypeEntry.put("subCode", List.of("6"));
            jobTypeList.add(jobTypeEntry);
            m.put("jobType", jobTypeList);
        }
        m.put("keywords", key);
        Map<String, Integer> pageMap = new HashMap<>();
        pageMap.put("pageNo", 1);
        pageMap.put("pageSize", 200);
        m.put("page", pageMap);
        m.put("r_query_id", "174255515819120298142");
        m.put("u_query_id", "31518dffd7fc281bd78e82796c6f1e12");
        return m;
    }

    @Override
    protected String getRequestUrl(String categoryType) {
        return "https://zhaopin.meituan.com/api/official/job/getJobList";
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
        return data.get("jobUnionId");
    }

    @Override
    protected LocalDate extractModifyTime(Map<String, Object> data) {
        return TimestampToLocalDateTimeDeserializer.deserialize(data.get("refreshTime").toString());
    }

    @Override
    protected String buildDetailUrl(Object id) {
        return "https://zhaopin.meituan.com/web/position/detail?jobUnionId=" + id +"&highlightType=campus";
    }
}
