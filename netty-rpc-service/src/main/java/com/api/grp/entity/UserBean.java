package com.api.grp.entity;

import java.io.Serializable;

public class UserBean implements Serializable{

    private Long id;
    private String userName;
    private String password;
    private String sex;
    private String realName;
    private String cardNum;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getCardNum() {
        return cardNum;
    }

    public void setCardNum(String cardNum) {
        this.cardNum = cardNum;
    }

    @Override
    public String toString() {
        StringBuilder builder =new StringBuilder();
        builder.append(id).append(userName).append(password).append(sex).append(realName).append(cardNum);
        return builder.toString();
    }
}
