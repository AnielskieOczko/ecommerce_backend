package com.rj.ecommerce_backend;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping()
public class TestController {

    @GetMapping("/test")
    @ResponseBody
    public String helloSpring() {
        return "hello spring";
    }
}
