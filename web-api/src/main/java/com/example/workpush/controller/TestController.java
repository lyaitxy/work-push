package com.example.workpush.controller;

import com.example.workpush.service.MeiTuanWorkService;
import jakarta.annotation.Resource;
import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @Resource
    private MeiTuanWorkService meiTuanWorkService;

    @GetMapping("/meituan")
    public String getData(@RequestParam String to,
                    @RequestParam(name = "type") String categoryType,
                    @RequestParam(required = false, defaultValue = "Java") String key) {
        return meiTuanWorkService.pushWork(to,categoryType,key);
    }
}
