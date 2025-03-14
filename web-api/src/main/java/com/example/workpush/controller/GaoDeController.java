package com.example.workpush.controller;

import ch.qos.logback.classic.Logger;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.workpush.pojo.entity.GaoDeWork;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/gao-de")
@Slf4j
public class GaoDeController {
    @Resource
    private RestTemplate restTemplate;

    @GetMapping("/getData")
    public void getData() {
        // 随机生成csrf token
        String csrf_token = "716b6eff-c544-453e-b630-a5c81e5e1653";
        // 创建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // 设置Cookie
        headers.add("Cookie", "XSRF-TOKEN="+ csrf_token +"; recruit-careers_USER_COOKIE_V3=ST180.7DCdmGB_qYV5QqYY52WbguD577Yk8kIGY7HkfZ36woWiXx7RRIZZfuipBrtTI2mj2dOtJeG94Xpt7zWHfunxhQs42u8gxHQEbkTPyJ1m8tJJPh3GmPWxtXiDkxZlzUnu7xLmSeL6GPE34Zst9L5UCO_x6rQR8vi795Nqx8opC6Ai3_fgvOGNVSPxeZSIHrgdkq1uH-1q7oMt44VGEEq-gGrHsCl64740RbkeXs0G9dh-gbwpCTTpckrXMVy2dYE0zM9zxc8GfPhwzyHUODFCwBIuWx0kDWvFVz9jwC2RjYySK78dZmB50f22urJ0hAFi9TXOPuE2cGKPRExBPsEZwZcLoD5m5RUa0AYPS9Wfm_2l90OBsgoosGmZ_6v5kAV868CeJ4tPw7Q0DkX0PG4zzQ2b2CqmtI5TeKqa89WJvjSEjho0XoZdAs5xIIpUzhvt7zDilBVgkI79KgTGnBNDnqGvWcKopF8DS910w_oH5dE.hKflQT33okyc6ciVDLx9SVRgXneql6RIXuhob5FWad_n8166gLFa_jUe77Iy2DaHv6IkU84QeerwC2SwtfhpFT8R7teSiPXvRt-x7lbtKsH5UBiE3Ic1__Z9JiYRn1d6wK9jd3HEX8mPFOyQ1-MeW7eZWnuRgAWa5k1Cs8-XBzHw4ckJNo610HQGqfOVrBo4l92jcTZIKbvZ7I-BVGQjkvANON6u3xmHttV3cjHGjIsbZuYAsb9pSlEfFkkrIyA8VPGZuUrJ2JEUQ2H6l5dxJsBuRA73z_oSwyG0SoUsCM-IraCLff52uWqv0yN69zQSYF5pa4zREn7VdUP__zECZg; recruit-careers_SSO_TOKEN_V3=ST180.eyJ0aW1lIjoxNzQwMDQ4MzIyNDQ1LCJ0b2tlbiI6ImVlNTE1NDJiYzQ1NTQzNDQ4NTRmNDYwNDBkZjljMTUyNmQ2NjQxMDEifQ.pvCPYfqZyC_F5mWzqSsKJ6SqaHoa3QGSULYgTzCi1I6DXL-lkujMPe0ySQgt1RrwT12hkQDepQaqd3JHlZX3WGMYJAoXGFHRcw_73q0QARIENqkvm_F0rBT7iyusDzOj9xHpWQ1VHDU529dA06H7Nf8WDZH-I0A1sr1lRWEyF4640CqU2K5nBojxbwYJd-bH5gAauypKFeYXtH14L_7NudaOXIyVLDmsHOEBOIh2n7FOl5k6gnj91fsjn_o0Q4z5JE7zLk_Z6OkI2IdyzwjgS2_AaiEdApiKtIgGNJhrAmcp9LOn5vdsC9C7haFfdyh-jgQJvbvYUGsuwwEaXiyRBg; SSO_LANG_V2=zh-CN; prefered-lang=zh; SESSION=MUFGMzM1REYyMDIwMTdDQTk2OTBBMDEwMzYwQ0U0OTA=; xlly_s=1; tfstk=gLwnEambXWlBUQUvJPkCMldBcc5tOHMS7zp-yY3P_Vu6eMaRA7mrSzcdAYErrz4t5JIQvYprzVrrJpFKyT4uSVD8v23JA5aY0pedOYpodYMPkZBAHHZIFYug8zhpRfomDTnETXkIQtSx_DXAHkZevDSYbteLGgDx-4krTb8abVikLQzrYhSi40LeapzzbG0s2DJrTLRw_moJzYkzzlSi0VoEBssEJyaYQNzbqaCUuSeixX04js9yEKmekqqFoLvzI5ctut3MULyiYlC7favcwqPjJj3b7OpnElouSjFhop4u4oNrs8YFvrrzRRDIn_IIi5PTMXVG8L0Tp0cqUfxyUlDzTbyo3t8if5rLaJaHqLoQp-holfjyFbHaHjP4-3sbbvozPjwRlpgU4owbMxXHlDVamAjy3CRqRKpSbgewNQGEfc0xDQqBL_BeWGjGjIlSTciCIGjMNQGEfc0AjGA4PXosAOf..; isg=BBQUwUmuDwlGhJu8WWkN8L1W5VKGbThX8XZUfK71KB8JmbXj1nwC56VfmZEBYXCv");
        Map<String, Object> m = new HashMap<>();
        m.put("batchId", "");
        m.put("categoryType", "internship");
        m.put("channel", "campus_group_official_site");
        m.put("corpCode", "");
        m.put("customDeptCode", "");
        m.put("key", "");
        m.put("language", "zh");
        m.put("pageIndex", 1);
        m.put("pageSize", 10);
        m.put("regions", "");
        m.put("subCategories", "");

        String url = "https://talent.amap.com/position/search?_csrf=" + csrf_token;

        // 创建HttpEntity
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(m, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        JSONArray datas = JSONObject.parseObject(response.getBody()).getJSONObject("content").getJSONArray("datas");

        List<Map<String, Object>> dataList = JSON.parseObject(datas.toJSONString(), List.class);
        for(Map<String, Object> data : dataList) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                GaoDeWork gaoDeJob = objectMapper.convertValue(data, GaoDeWork.class);
                if(data.get("categoryType") == "internship") {
                    gaoDeJob.setCategoryType("暑期实习");
                } else if(data.get("categoryType") == "project") {
                    gaoDeJob.setCategoryType("日常实习");
                } else if(data.get("categoryType") == "freshman") {
                    gaoDeJob.setCategoryType("应届校招");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }


//        for (Map.Entry<String, Object> entry : dataList.get(0).entrySet()) {
//            log.info("key为：{},value为：{}", entry.getKey(), entry.getValue());
//
//        }

    }
}
