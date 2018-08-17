package com.shilun.block.domain;

import com.common.util.AbstractBaseEntity;
import com.shilun.block.domain.module.ChanTypeEnum;

import java.util.Map;

/**
 * 块高度
 */
public class BlockInfo extends AbstractBaseEntity {
    /**
     * 区块类型
     */
    private String  chanTypeEnum;

    /**
     * 区块高度
     */
    private Integer blockHeight;

    /**
     * 区块高度哈希
     */
    private String blockHash;

    private String parentHash;
    private String nextHash;

    public String getParentHash() {
        return parentHash;
    }

    public void setParentHash(String parentHash) {
        this.parentHash = parentHash;
    }

    public String getNextHash() {
        return nextHash;
    }

    public void setNextHash(String nextHash) {
        this.nextHash = nextHash;
    }


    private Map<String, Object> extendParams;

    public Map<String, Object> getExtendParams() {
        return extendParams;
    }

    public void setExtendParams(Map<String, Object> extendParams) {
        this.extendParams = extendParams;
    }

    public String getChanTypeEnum() {
        return chanTypeEnum;
    }

    public void setChanTypeEnum(String chanTypeEnum) {
        this.chanTypeEnum = chanTypeEnum;
    }

    public Integer getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(Integer blockHeight) {
        this.blockHeight = blockHeight;
    }

    public String getBlockHash() {
        return blockHash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }
}
