package com.api.grp.service;

import com.api.grp.api.IAccountService;
import com.api.grp.api.IUserService;
import com.api.grp.bean.UserBean;
import com.api.grp.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class UserServiceImpl implements IUserService,IAccountService {

    @Autowired
    private UserMapper userMapper;


    Random random =new Random();
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
    public String getAccount(Long id,String type) {
        int i = random.nextInt(100);
        try{
            Thread.sleep(i);
        }catch (Exception e){
            e.printStackTrace();
        }
        com.api.grp.entity.UserBean user = null;
        if(type.equals("s")){
            user = userMapper.getUser(id);
        }else if(type.equals("i")){
            user = new com.api.grp.entity.UserBean();
            user.setUserName("userName_"+i);
            user.setPassword("password");
            user.setRealName("realName"+i);
            user.setSex("1");
            user.setCardNum("card");
            System.out.println(userMapper.saveUser(user));
        }else if(type.equals("u")){
            user= userMapper.getUser(id);
            user.setRealName(i+"");
            System.out.println(userMapper.updateUser(user));
        }else if(type.equals("d")){
            System.out.println(userMapper.delUser(id));
        }else{
            return "Li";
        }

        return user == null?"Li":user.toString();
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
