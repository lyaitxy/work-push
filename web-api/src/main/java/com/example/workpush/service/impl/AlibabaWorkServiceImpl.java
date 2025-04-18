package com.example.workpush.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.workpush.jobEnum.Ali.AliCompanyEnum;
import com.example.workpush.jobEnum.Ali.AliCategoryType;
import com.example.workpush.service.AlibabaWorkService;
import com.example.workpush.utils.TimestampToLocalDateTimeDeserializer;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class AlibabaWorkServiceImpl implements AlibabaWorkService {

    @Resource
    private RestTemplate restTemplate;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public String pushWork(String to, String categoryType, String key) {
        // 构建返回结果
        StringBuilder sb = new StringBuilder();

        for(AliCompanyEnum company : AliCompanyEnum.values()) {
            // 随机生成csrf token
            String csrf_token = UUID.randomUUID().toString();
            // 创建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            // 设置Cookie
            headers.add("Cookie", "XSRF-TOKEN="+ csrf_token +"; recruit-careers_USER_COOKIE_V3=ST180.7DCdmGB_qYV5QqYY52WbguD577Yk8kIGY7HkfZ36woWiXx7RRIZZfuipBrtTI2mj2dOtJeG94Xpt7zWHfunxhQs42u8gxHQEbkTPyJ1m8tJJPh3GmPWxtXiDkxZlzUnu7xLmSeL6GPE34Zst9L5UCO_x6rQR8vi795Nqx8opC6Ai3_fgvOGNVSPxeZSIHrgdkq1uH-1q7oMt44VGEEq-gGrHsCl64740RbkeXs0G9dh-gbwpCTTpckrXMVy2dYE0zM9zxc8GfPhwzyHUODFCwBIuWx0kDWvFVz9jwC2RjYySK78dZmB50f22urJ0hAFi9TXOPuE2cGKPRExBPsEZwZcLoD5m5RUa0AYPS9Wfm_2l90OBsgoosGmZ_6v5kAV868CeJ4tPw7Q0DkX0PG4zzQ2b2CqmtI5TeKqa89WJvjSEjho0XoZdAs5xIIpUzhvt7zDilBVgkI79KgTGnBNDnqGvWcKopF8DS910w_oH5dE.hKflQT33okyc6ciVDLx9SVRgXneql6RIXuhob5FWad_n8166gLFa_jUe77Iy2DaHv6IkU84QeerwC2SwtfhpFT8R7teSiPXvRt-x7lbtKsH5UBiE3Ic1__Z9JiYRn1d6wK9jd3HEX8mPFOyQ1-MeW7eZWnuRgAWa5k1Cs8-XBzHw4ckJNo610HQGqfOVrBo4l92jcTZIKbvZ7I-BVGQjkvANON6u3xmHttV3cjHGjIsbZuYAsb9pSlEfFkkrIyA8VPGZuUrJ2JEUQ2H6l5dxJsBuRA73z_oSwyG0SoUsCM-IraCLff52uWqv0yN69zQSYF5pa4zREn7VdUP__zECZg; recruit-careers_SSO_TOKEN_V3=ST180.eyJ0aW1lIjoxNzQwMDQ4MzIyNDQ1LCJ0b2tlbiI6ImVlNTE1NDJiYzQ1NTQzNDQ4NTRmNDYwNDBkZjljMTUyNmQ2NjQxMDEifQ.pvCPYfqZyC_F5mWzqSsKJ6SqaHoa3QGSULYgTzCi1I6DXL-lkujMPe0ySQgt1RrwT12hkQDepQaqd3JHlZX3WGMYJAoXGFHRcw_73q0QARIENqkvm_F0rBT7iyusDzOj9xHpWQ1VHDU529dA06H7Nf8WDZH-I0A1sr1lRWEyF4640CqU2K5nBojxbwYJd-bH5gAauypKFeYXtH14L_7NudaOXIyVLDmsHOEBOIh2n7FOl5k6gnj91fsjn_o0Q4z5JE7zLk_Z6OkI2IdyzwjgS2_AaiEdApiKtIgGNJhrAmcp9LOn5vdsC9C7haFfdyh-jgQJvbvYUGsuwwEaXiyRBg; SSO_LANG_V2=zh-CN; prefered-lang=zh; SESSION=MUFGMzM1REYyMDIwMTdDQTk2OTBBMDEwMzYwQ0U0OTA=; xlly_s=1; tfstk=gLwnEambXWlBUQUvJPkCMldBcc5tOHMS7zp-yY3P_Vu6eMaRA7mrSzcdAYErrz4t5JIQvYprzVrrJpFKyT4uSVD8v23JA5aY0pedOYpodYMPkZBAHHZIFYug8zhpRfomDTnETXkIQtSx_DXAHkZevDSYbteLGgDx-4krTb8abVikLQzrYhSi40LeapzzbG0s2DJrTLRw_moJzYkzzlSi0VoEBssEJyaYQNzbqaCUuSeixX04js9yEKmekqqFoLvzI5ctut3MULyiYlC7favcwqPjJj3b7OpnElouSjFhop4u4oNrs8YFvrrzRRDIn_IIi5PTMXVG8L0Tp0cqUfxyUlDzTbyo3t8if5rLaJaHqLoQp-holfjyFbHaHjP4-3sbbvozPjwRlpgU4owbMxXHlDVamAjy3CRqRKpSbgewNQGEfc0xDQqBL_BeWGjGjIlSTciCIGjMNQGEfc0AjGA4PXosAOf..; isg=BBQUwUmuDwlGhJu8WWkN8L1W5VKGbThX8XZUfK71KB8JmbXj1nwC56VfmZEBYXCv");
            // 获取参数体
            Map<String, Object> m = getStringObjectMap(key, AliCategoryType.getValue(categoryType));

            String url = "https://"+company.getEnglish()+"/position/search?_csrf=" + csrf_token;

            // 创建HttpEntity
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(m, headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            // 解析返回的数据
            JSONArray datas = JSONObject.parseObject(response.getBody()).getJSONObject("content").getJSONArray("datas");
            if(datas == null) continue;
            // 将返回的数据转换为map
            List<Map<String, Object>> dataList = JSON.parseObject(datas.toJSONString(), List.class);

            for(Map<String, Object> data : dataList) {
                // 获得id
                Object id = data.get("id");
                // 获取更新时间戳表示的时间
                LocalDate modifyTime = TimestampToLocalDateTimeDeserializer.deserialize(data.get("modifyTime").toString());
                // 获取当前时间戳表示的时间
                LocalDate now = TimestampToLocalDateTimeDeserializer.deserialize(System.currentTimeMillis() + "");
                log.info("id: {}, modifyTime: {}, now：{}", id, modifyTime, now);
                // 判断时间
                if(modifyTime.isEqual(now)) {
                    // 判断当前职位的id是否在redis中
                    boolean isPush = Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(to + categoryType + key, id));
                    if(!isPush) {
                        // 说明有职位更新， 将id存入redis中
                        redisTemplate.opsForSet().add(to + categoryType + key, id);
                        sb.append("https://"+ company.getEnglish()+"/campus/position-detail?positionId="+ id + "\n");
                    }
                } else {
                    // 如果两个时间不相等，跳出循环
                    break;
                }
            }
        }
        return sb.toString();
    }

    private Map<String, Object> getStringObjectMap(String key, String type) {
        Map<String, Object> m = new HashMap<>();
        m.put("batchId", "");
        m.put("categoryType", type); //internship 暑期实习， project 项目制日常实习  freshman 应届校招
        m.put("channel", "campus_group_official_site");
        m.put("corpCode", "");
        m.put("customDeptCode", "");
        m.put("key", key);
        m.put("language", "zh");
        m.put("pageIndex", 1);
        m.put("pageSize", 20);
        m.put("regions", "");
        m.put("subCategories", "");
        return m;
    }
}
