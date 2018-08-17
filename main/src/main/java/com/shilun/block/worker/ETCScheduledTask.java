package com.shilun.block.worker;

import com.shilun.block.domain.BlockInfo;
import com.shilun.block.domain.TransInfo;
import com.shilun.block.domain.module.ChanTypeEnum;
import com.shilun.block.service.BlockInfoService;
import com.shilun.block.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.web3j.abi.EventValues;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.tx.Contract;
import org.web3j.tx.ReadonlyTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.utils.Convert;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class ETCScheduledTask {

    private static final Logger log = LoggerFactory.getLogger(ETCScheduledTask.class);

    @Resource
    private Web3j web3j;


    @Resource
    private BlockInfoService blockInfoService;

    @Resource
    private TransactionService transactionService;

    private volatile AtomicBoolean running = new AtomicBoolean(false);


    @Scheduled(fixedRate = 1000)
    public void reportCurrentTime() {
        if (!running.getAndSet(true)) {
            Integer blockHight = getLatestBlockNumber();
            blockHight = 50000;
            BlockInfo lastBlock = blockInfoService.findLastBlock(ChanTypeEnum.ETH);
            Integer dbHight = null;
            if (lastBlock == null) {
                dbHight = 0;
            } else {
                dbHight = lastBlock.getBlockHeight();
                dbHight++;
            }
            if (blockHight > dbHight.intValue()) {
                EthBlock block = getBlock(6157273);

                List<EthBlock.TransactionResult> transactions = block.getBlock().getTransactions();
                try {
                    for (EthBlock.TransactionResult item : transactions) {
                        Transaction transaction = getTransactionByHash((String) item.get()).getTransaction().get();
                        TransInfo tran = new TransInfo();
                        tran.setBlockHeight(blockHight);
                        tran.setTxId("");
                        tran.setTxHash((String) item.get());
                        tran.setFrom(transaction.getFrom());
                        tran.setTo(transaction.getTo());
                        //非代币
                        if (transaction.getValue().compareTo(BigInteger.ZERO) > 0) {
                            BigDecimal bigDecimal = Convert.fromWei(transaction.getValue().toString(), Convert.Unit.ETHER);
                            tran.setValue(bigDecimal);
                            tran.setChanTypeEnum(ChanTypeEnum.ETH);
                        //代币
                        } else {
                            AbstractContract contract = null;
                            TransactionManager transactionManager = new ReadonlyTransactionManager(web3j, transaction.getFrom());
                            contract = new AbstractContract(web3j, transaction.getTo(), transactionManager, BigInteger.ZERO, BigInteger.ONE) {
                            };
                            tran.setChanTypeEnum(ChanTypeEnum.OTHER_ETH);
                            Request<?, EthGetTransactionReceipt> ethGetTransactionReceiptRequest1 = web3j.ethGetTransactionReceipt(transaction.getHash());
                            List<EventValues> eventValues = contract.processEvent(ethGetTransactionReceiptRequest1.send().getResult());
                            BigDecimal bigDecimal = Convert.fromWei(eventValues.get(0).getNonIndexedValues().get(0).getValue().toString(), Convert.Unit.ETHER);

                            tran.setValue(bigDecimal);
                            tran.setChanTypeEnum(ChanTypeEnum.OTHER_ETH);
                        }
                        transactionService.insert(tran);
                    }
                } catch (Exception e) {
                    log.error("sync block error", e);
                }
            }
            running.set(false);
        }
    }


    public String getWeb3ClientVersion() {
        try {
            Web3ClientVersion web3ClientVersion = web3j.web3ClientVersion().send();
            return web3ClientVersion.getWeb3ClientVersion();
        } catch (Exception e) {
            return "";
        }
    }

    public Integer getLatestBlockNumber() {
        try {
            return getBlockNumber(DefaultBlockParameterName.LATEST).intValue();
        } catch (Exception e) {
            return 0;
        }
    }

    public BigInteger getBlockNumber(Integer index) {
        DefaultBlockParameter defaultBlockParameter = DefaultBlockParameter.valueOf(BigInteger.valueOf(index.longValue()));
        return getBlockNumber(defaultBlockParameter);
    }

    public BigInteger getBlockNumber(DefaultBlockParameter defaultBlockParameter) {
        if (defaultBlockParameter instanceof DefaultBlockParameterNumber) {
            return ((DefaultBlockParameterNumber) defaultBlockParameter).getBlockNumber();
        } else {
            return getBlock(defaultBlockParameter).getBlock().getNumber();
        }
    }

    public EthBlock getBlock(Integer defaultBlockParameter) {
        EthBlock latestEthBlock;
        try {
            latestEthBlock = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(BigInteger.valueOf(defaultBlockParameter.longValue())), false).send();
        } catch (Exception e) {
            throw new RuntimeException("Can't get block by number", e);
        }
        return latestEthBlock;
    }

    public EthBlock getBlock(DefaultBlockParameter defaultBlockParameter) {
        EthBlock latestEthBlock;
        try {
            latestEthBlock = web3j.ethGetBlockByNumber(defaultBlockParameter, false).send();
        } catch (Exception e) {
            throw new RuntimeException("Can't get block by number", e);
        }
        return latestEthBlock;
    }

    public EthTransaction getTransactionByHash(String hash) {
        EthTransaction ethTransaction;
        try {
            ethTransaction = web3j.ethGetTransactionByHash(hash).send();
        } catch (IOException e) {
            throw new RuntimeException("Can't get block by number", e);
        }
        return ethTransaction;
    }
}

abstract class AbstractContract extends Contract {

    private Web3j web3j;

    public AbstractContract(Web3j web3j, String contractAddress, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super("", contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }


    public List<EventValues> processEvent(TransactionReceipt transactionReceipt) {
        final Event event = new Event("Transfer",
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {
                }, new TypeReference<Address>() {
                }),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
                }));
        return extractEventParameters(event, transactionReceipt);
    }

}