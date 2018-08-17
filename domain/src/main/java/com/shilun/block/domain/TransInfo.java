package com.shilun.block.domain;

import com.common.util.AbstractBaseEntity;
import com.shilun.block.domain.module.ChanTypeEnum;

import java.math.BigDecimal;

/**
 * 交易信息
 */
public class TransInfo extends AbstractBaseEntity {

    /**
     *
     */
    String txId;
    /**
     * 交易哈希
     */
    String txHash;
    /**
     * 区块高度
     */
    Integer blockHeight;
    /**
     * from地址
     */
    String from;
    /**
     * to地址
     */
    String to;
    /**
     * 金额
     */
    BigDecimal value;
    /**
     * 附加数据
     */
    String inputData;
    /**
     * token币种
     */
    ChanTypeEnum chanTypeEnum;

    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public Integer getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(Integer blockHeight) {
        this.blockHeight = blockHeight;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public String getInputData() {
        return inputData;
    }

    public void setInputData(String inputData) {
        this.inputData = inputData;
    }

    public ChanTypeEnum getChanTypeEnum() {
        return chanTypeEnum;
    }

    public void setChanTypeEnum(ChanTypeEnum chanTypeEnum) {
        this.chanTypeEnum = chanTypeEnum;
    }
}
