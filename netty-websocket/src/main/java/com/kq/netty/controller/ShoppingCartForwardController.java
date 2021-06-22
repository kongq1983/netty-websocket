package com.kq.netty.controller;



import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;



/**
 * @author kq
 * @date 2021-04-21 18:10
 * @since 2020-0630
 */
@Controller
public class ShoppingCartForwardController {

    @GetMapping("/shoppingcart")
    public String shoppingCart(){

        return "ShoppingCartController: getInfo";

    }


}
