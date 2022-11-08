package com.lzh.financial.code.test.generator;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.lzh.financial.code.service.UserService;
import com.lzh.financial.code.test.generator.domain.JsonTest;
import com.lzh.financial.code.test.generator.mapper.JsonTestMapper;
import com.lzh.financial.code.test.generator.service.JsonTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class Controller {
    @Autowired
    private JsonTestService jsonTestService;
    @Autowired
    private UserService userService;

    @Autowired
    private JsonTestMapper jsonTestMapper;

    @GetMapping("test")
    @Transactional
    public  void amain() {
        JsonTest jsonTest = jsonTestService.select();


        jsonTestMapper.testUpdate("测试");
        System.out.println(jsonTest.getJson());

    }
}
