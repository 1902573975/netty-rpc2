package com.api.grp.rest;

import com.api.grp.api.IAccountService;
import com.api.grp.api.IUserService;
import com.api.grp.bean.UserBean;
import com.api.grp.service.ProxyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    ProxyFactory proxyFactory;

    IAccountService accountService;
    IUserService userService;

    @GetMapping("/do")
    public String test(@RequestParam(required = false,defaultValue = "1") Long id,@RequestParam(required = false,defaultValue = "s") String type){
        return accountService.getAccount(id,type);
    }

    @GetMapping("/do2")
    public Object test2(){
        return userService.getUser("Li");
    }

    @GetMapping("/do1")
    public Object test1(){
        return userService.getUser("12","Yang");
    }

    @GetMapping("/do3")
    public Object test3(){
        UserBean b = new UserBean();
        b.setSex("girl");
        b.setId("3455");
        return userService.updateUser(b);
    }

    @GetMapping("/do4")
    public Object test4(){
        return userService.updateUser(null);
    }



    @PostConstruct
    public void init(){
        accountService = (IAccountService)proxyFactory.proxy(IAccountService.class);
        userService = (IUserService)proxyFactory.proxy(IUserService.class);
    }


}
