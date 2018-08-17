package com.shilun.block.rpccfg;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.shilun.block.btc.IBlock;
import com.shilun.block.btc.ITransaction;
import com.shilun.block.service.BlockInfoService;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Service
public class BtcJsonRPCService implements InitializingBean {
    private static Logger logger = LoggerFactory.getLogger(BtcJsonRPCService.class);

    @Value("${btc.rpc.host}")
    private String rpcHost;
    @Value("${btc.rpc.port}")
    private String rpcPort;
    @Value("${btc.rpc.user}")
    private String rpcUser;
    @Value("${btc.rpc.pass}")
    private String rpcPass;

    @Resource
    private BlockInfoService blockInfoService;
    private JsonRpcHttpClient client;

    @Override
    public void afterPropertiesSet() throws Exception {
        String cred = Base64.encodeBase64String((rpcUser + ":" + rpcPass).toString().getBytes());
        Map<String, String> headers = new HashMap();
        headers.put("Authorization", "Basic " + cred);
        client = new JsonRpcHttpClient(new URL("http://" + rpcHost + ":" + rpcPort), headers);
    }

    public IBlock buildIBlock() {
        return new IBlock() {
            @Override
            public Integer getblockcount() {
                return invoke("getblockcount", Integer.class);
            }

            @Override
            public String getblockhash(Integer index) {
                return invoke("getblockhash", new Object[]{index}, String.class);
            }

            @Override
            public Object getblock(String hash) {
                return invoke("getblock", new Object[]{hash}, Object.class);
            }
        };
    }

    public ITransaction buildITransaction() {
        return new ITransaction() {
            @Override
            public String getrawtransaction(String hash) {
                return invoke("getrawtransaction", new Object[]{hash}, String.class);
            }

            @Override
            public Object decoderawtransaction(String content) {
                return invoke("decoderawtransaction", new Object[]{content}, Object.class);
            }
        };
    }

    public String invoke(String methodName) {
        return invoke(methodName, new Object[]{});
    }


    public <T> T invoke(String methodName, Class<T> classz) {
        try {
            return client.invoke(methodName, new Object[]{}, classz);
        } catch (Throwable e) {
            logger.error("验证地址失败", e);
        }
        return null;

    }

    public String invoke(String methodName, Object argument) {
        try {
            Object invoke = client.invoke(methodName, argument, Object.class);
            return net.sf.json.JSONObject.fromObject(invoke).toString();
        } catch (Throwable e) {
            logger.error("验证地址失败", e);
        }
        return "";

    }

    public <T> T invoke(String methodName, Object argument, Class<T> classz) {
        try {
            return client.invoke(methodName, argument, classz);
        } catch (Throwable e) {
            logger.error("验证地址失败", e);
        }
        return null;
    }

}
