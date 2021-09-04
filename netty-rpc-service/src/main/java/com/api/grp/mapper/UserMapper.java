package com.api.grp.mapper;

import com.api.grp.entity.UserBean;

public interface UserMapper {

    UserBean getUser(Long id);
    int saveUser(UserBean userBean);
    int updateUser(UserBean userBean);
    int delUser(Long id);
}
