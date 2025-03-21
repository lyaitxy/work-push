package com.example.workpush.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.workpush.service.MeiTuanWorkService;
import com.example.workpush.utils.TimestampToLocalDateTimeDeserializer;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Slf4j
public class MeiTuanWorkServiceImpl implements MeiTuanWorkService {

    @Resource
    private RestTemplate restTemplate;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public String pushWork(String to, String categoryType, String key) {
        // 构建返回结果
        StringBuilder sb = new StringBuilder();

        // 获取参数体
        Map<String, Object> m = getStringObjectMap(key, categoryType);
        String url = "https://zhaopin.meituan.com/api/official/job/getJobList";

        ResponseEntity<String> response = restTemplate.postForEntity(url, m, String.class);
        // 解析返回的数据
        JSONArray datas = JSONObject.parseObject(response.getBody()).getJSONObject("data").getJSONArray("list");
        if(datas == null) return "";
        // 将返回的数据转换为map
        List<Map<String, Object>> dataList = JSON.parseObject(datas.toJSONString(), List.class);

        // 设置布尔变量判断是否加入有职位更新这条语句
        boolean flag = false;
        for(Map<String, Object> data : dataList) {
            // 获得id
            Object id = data.get("jobUnionId");
            // 获取更新时间戳表示的时间
            LocalDate modifyTime = TimestampToLocalDateTimeDeserializer.deserialize(data.get("refreshTime").toString());
            // 获取当前时间戳表示的时间
            LocalDate now = TimestampToLocalDateTimeDeserializer.deserialize(System.currentTimeMillis() + "");
            log.info("id: {}, modifyTime: {}, now：{}", id, modifyTime, now);
            // 判断时间
            if(modifyTime.isEqual(now)) {
                // 判断当前职位的id是否在redis中
                boolean isPush = Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(to + "id", id));
                if(!isPush) {
                    // 说明有职位更新， 将id存入redis中
                    redisTemplate.opsForSet().add(to + "id", id);
                    if(!flag) {
                        sb.append("美团").append("有职位更新：\n");
                    }
                    flag = true;
                    sb.append("https://zhaopin.meituan.com/web/position/detail?jobUnionId=" + id +"&highlightType=campus" + "\n");
                }
            }
        }

        return sb.toString();
    }

    private Map<String, Object> getStringObjectMap(String key, String categoryType) {
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
        m.put("pageNo", 1);
        m.put("pageSize", 200);
        m.put("page", pageMap);
        m.put("r_query_id", "174255515819120298142");
        m.put("u_query_id", "31518dffd7fc281bd78e82796c6f1e12");
        return m;
    }
}
