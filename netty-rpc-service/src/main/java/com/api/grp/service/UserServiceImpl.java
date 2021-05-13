package com.api.grp.service;

import com.api.grp.api.IAccountService;
import com.api.grp.api.IUserService;
import com.api.grp.bean.UserBean;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements IUserService,IAccountService {

    @Override
    public UserBean getUser(String userName) {
        UserBean b = new UserBean();
        b.setId("1");
        return b;
    }

    @Override
    public UserBean getUser(String Id, String name) {
        UserBean b = new UserBean();
        b.setId("2");
        return b;
    }

    @Override
    public boolean status() {
        return false;
    }

    @Override
    public String getAccount(String id) {
        System.out.println("This is acount");
        return "Li";
    }

    public String getUserName(){
        return "3";
    }

    public String getPassword(){
        return "4";
    }

    @Override
    public UserBean updateUser(UserBean userBean) {
        if(userBean != null){
            System.out.println("Id"+userBean.getId());
        }
        UserBean u =new UserBean();
        u.setId("1");
        u.setName("name");
        return u;
    }
}
