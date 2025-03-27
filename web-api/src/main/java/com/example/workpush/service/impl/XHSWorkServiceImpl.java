package com.example.workpush.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.workpush.service.JingDongWorkService;
import com.example.workpush.service.XHSWorkService;
import com.example.workpush.utils.TimestampToLocalDateTimeDeserializer;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class XHSWorkServiceImpl implements XHSWorkService {

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
        String url = "https://job.xiaohongshu.com/websiterecruit/position/pageQueryPosition";

        ResponseEntity<String> response = restTemplate.postForEntity(url, m, String.class);
        // 解析返回的数据
        JSONArray datas = JSONObject.parseObject(response.getBody()).getJSONObject("data").getJSONArray("list");
        if(datas == null) return "";
        // 将返回的数据转换为map
        List<Map<String, Object>> dataList = JSON.parseObject(datas.toJSONString(), List.class);

        for(Map<String, Object> data : dataList) {
            // 获得id
            Object id = data.get("positionId");
            // 获取更新时间戳表示的时间
            LocalDate modifyTime = LocalDate.parse((CharSequence) data.get("publishTime"));
            // 获取当前时间戳表示的时间
            LocalDate now = LocalDate.now();
            log.info("id: {}, modifyTime: {}, now：{}", id, modifyTime, now);
            // 判断时间
            if(modifyTime.isEqual(now)) {
                // 判断当前职位的id是否在redis中
                boolean isPush = Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(to + categoryType + key, id));
                if(!isPush) {
                    // 说明有职位更新， 将id存入redis中
                    redisTemplate.opsForSet().add(to + categoryType + key, id);;
                    sb.append("https://job.xiaohongshu.com/campus/position/" + id +"\n");
                }
            }
        }

        return sb.toString();
    }

    private Map<String, Object> getStringObjectMap(String key, String categoryType) {
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
}
