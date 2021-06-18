package com.kq.netty.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author kq
 * @date 2021-04-21 18:10
 * @since 2020-0630
 */
@RestController
public class ShoppingCartController {

    @GetMapping("/info")
    public String getInfo(){

        return "ShoppingCartController: getInfo";

    }

}
