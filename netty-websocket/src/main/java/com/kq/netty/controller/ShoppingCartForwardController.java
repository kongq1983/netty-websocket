package com.kq.netty.controller;

import com.kq.netty.connection.WebSocketConnectionFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author kq
 * @date 2021-04-21 18:10
 * @since 2020-0630
 */
@RestController
public class ShoppingCartController {

    @Autowired
    private WebSocketConnectionFacade webSocketConnectionFacade;

    private AtomicInteger atomicInteger = new AtomicInteger();

    @GetMapping("/info")
    public String getInfo(){

        return "ShoppingCartController: getInfo";

    }

    @GetMapping("/shoppingcart")
    public String shoppingCart(){

        return "ShoppingCartController: getInfo";

    }


    @GetMapping("/table/add")
    public String notifyByTable(@RequestParam("tableId") String tableId){

        webSocketConnectionFacade.notifyTable(tableId,"table notify "+atomicInteger.incrementAndGet());

        return "ShoppingCartController: getInfo";

    }

    @GetMapping("/org/add")
    public String notifyByOrgId(@RequestParam("orgId") String orgId){

        webSocketConnectionFacade.notifyOrg(orgId,"org notify "+atomicInteger.incrementAndGet());

        return "ShoppingCartController: getInfo";

    }

    @GetMapping("/common/add")
    public String notifyByOrgId(@RequestParam("tableId") String tableId,@RequestParam("orgId") String orgId){

        webSocketConnectionFacade.notifyAll(tableId,orgId,"table - org notify "+atomicInteger.incrementAndGet());

        return "ShoppingCartController: getInfo";

    }

}
