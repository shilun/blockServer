package com.shilun.block.rpccfg;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

/**
 *
 */
@Configuration
public class ETHConfigService {

    @Value("${eth.rpc.host}")
    String host;
    @Value("${eth.rpc.port}")
    String port;


    @Bean("web3j")
    public Web3j web3j() {
        return Web3j.build(new HttpService("http://"+host+":"+port));
    }



}
