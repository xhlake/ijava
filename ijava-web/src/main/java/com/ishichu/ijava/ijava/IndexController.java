package com.ishichu.ijava.ijava;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by shichu.fl on 2018/4/30.
 */

@RestController
public class IndexController {
    @RequestMapping("/")
    @ResponseBody
    String test(){
        return "Hello SpringBoot!!!";
    }
}
