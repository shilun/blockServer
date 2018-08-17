package com.shilun.block.worker;

import com.common.util.StringUtils;
import com.shilun.block.domain.TransInfo;
import com.shilun.block.rpccfg.BtcJsonRPCService;
import com.shilun.block.domain.BlockInfo;
import com.shilun.block.domain.module.ChanTypeEnum;
import com.shilun.block.service.BlockInfoService;
import com.shilun.block.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class BTCScheduledTask {

    private static final Logger log = LoggerFactory.getLogger(BTCScheduledTask.class);

    //
    @Resource
    private BtcJsonRPCService btcSync;


    @Resource
    private BlockInfoService blockInfoService;

    @Resource
    private TransactionService transactionService;

    private volatile AtomicBoolean running = new AtomicBoolean(false);

    @Scheduled(fixedRate = 1000)
    public void reportCurrentTime() {
        if (!running.getAndSet(true)) {
            Integer blockHight = btcSync.buildIBlock().getblockcount();
            BlockInfo lastBlock = blockInfoService.findLastBlock(ChanTypeEnum.ETC);
            Integer dbHight = null;
            if (lastBlock == null) {
                dbHight = 0;
            } else {
                dbHight = lastBlock.getBlockHeight();
                dbHight++;
            }
            if (blockHight > dbHight.intValue()) {
                String blockHash = btcSync.buildIBlock().getblockhash(dbHight);
                Map getblock = (Map) btcSync.buildIBlock().getblock(blockHash);
                BlockInfo info = new BlockInfo();
                info.setBlockHeight(dbHight);
                info.setBlockHash(blockHash);
                info.setSize((Integer) getblock.get("size"));
                info.setCreateTime(new Date(((Integer) getblock.get("time")).longValue()));
                info.setChanTypeEnum(ChanTypeEnum.BTC.name());
                Object previousblockhash = getblock.get("previousblockhash");
                if (previousblockhash != null)
                    info.setParentHash((String) previousblockhash);
                ArrayList<String> tx = (ArrayList) getblock.get("tx");
                for (String trItem : tx) {
                    String getrawtransaction = btcSync.buildITransaction().getrawtransaction(trItem);
                    if(StringUtils.isBlank(getrawtransaction)){
                        continue;
                    }
                    Map decoderawtransaction = (Map) btcSync.buildITransaction().decoderawtransaction(getrawtransaction);
                    ArrayList<Map<String, Object>> vout = (ArrayList<Map<String, Object>>) decoderawtransaction.get("vout");
                    for (Map<String, Object> trData : vout) {
                        Double value = (Double) trData.get("value");
                        Map<String, Object> scriptPubKey = (Map<String, Object>) trData.get("scriptPubKey");
                        if (value.doubleValue() > 0 && scriptPubKey.containsKey("addresses")) {
                            TransInfo tran = new TransInfo();
                            tran.setBlockHeight(dbHight);
                            tran.setTxId((String) decoderawtransaction.get("txid"));
                            tran.setTxHash((String) decoderawtransaction.get("hash"));
                            tran.setFrom("");
                            tran.setValue(BigDecimal.valueOf(value));
                            tran.setTo(((ArrayList<String>)scriptPubKey.get("addresses")).get(0));
                            transactionService.insert(tran);
                        }
                    }
                }
                blockInfoService.insert(info);
            }
            running.set(false);
        }
    }
}