package com.api.grp.api;


import com.api.grp.comm.RPCService;

@RPCService(serviceName = "SNOW_SERVICE")
public interface IAccountService extends ICommService{

    String getAccount(String id);
}
