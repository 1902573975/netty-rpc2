package com.api.grp.api;

import com.api.grp.bean.UserBean;
import com.api.grp.comm.RPCService;


@RPCService(serviceName = "SNOW_SERVICE")
public interface IUserService {

    UserBean getUser(String userName);

    UserBean getUser(String Id ,String name);

    UserBean updateUser(UserBean userBean);
}
