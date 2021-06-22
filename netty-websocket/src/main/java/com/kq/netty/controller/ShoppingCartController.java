package com.kq.netty.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.kq.netty.connection.WebSocketConnectionFacade;
import com.kq.netty.dto.NotifyDataDto;
import com.kq.netty.dto.ProductOperateDto;
import com.kq.netty.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
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

    private static final ConcurrentMap<String,Integer> map = new ConcurrentHashMap();

    static {
        for(int i=1;i<10;i++) {
            map.put(String.valueOf(i),0);
        }
    }


    @GetMapping("/info")
    public String getInfo(){

        return "ShoppingCartController: getInfo";

    }

//    @GetMapping("/shoppingcart")
//    public String shoppingCart(){
//
//        return "ShoppingCartController: getInfo";
//
//    }

    @PostMapping("/product/jia")
    public String productAdd(ProductOperateDto dto){

        String productId = dto.getId();
        Integer shoppingSize = 0;

        synchronized (productId.intern()) {
           Integer size =  map.get(productId);

           if(size==null){
               size = 0;
           }

           shoppingSize = size+1;

            map.put(productId,shoppingSize);
        }

        NotifyDataDto notifyDataDto = new NotifyDataDto();
        notifyDataDto.setId(productId);
        notifyDataDto.setSize(shoppingSize);
        notifyDataDto.setType(1);

        webSocketConnectionFacade.notifyTable("1", JsonUtil.object2String(notifyDataDto));

        return "add:"+shoppingSize;

    }

    @PostMapping("/product/jian")
    public String productJian(ProductOperateDto dto){

        String productId = dto.getId();
        Integer shoppingSize = 0;

        synchronized (productId.intern()) {
            Integer size =  map.get(productId);

            if(size==null||size.intValue()<1){
                size = 0;
            }else {
                size = size - 1;
            }

            shoppingSize = size;

            map.put(productId,shoppingSize);
        }

        NotifyDataDto notifyDataDto = new NotifyDataDto();
        notifyDataDto.setId(productId);
        notifyDataDto.setSize(shoppingSize);
        notifyDataDto.setType(2);

        webSocketConnectionFacade.notifyTable("1", JsonUtil.object2String(notifyDataDto));

        return "jian";

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
