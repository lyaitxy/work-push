package com.example.workpush.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.workpush.config.SearchProperties;
import com.example.workpush.entity.AliJob;
import com.example.workpush.service.AlibabaWorkService;
import com.example.workpush.utils.TimestampToLocalDateTimeDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/Ali")
@Slf4j
public class AlibabaController {

    @Resource
    private AlibabaWorkService alibabaWorkService;
    @GetMapping("/getData")
    public void getData() {
        alibabaWorkService.pushwork();
    }
}
