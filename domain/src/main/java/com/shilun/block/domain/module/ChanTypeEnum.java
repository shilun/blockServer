package com.shilun.block.domain.module;

import com.common.util.IGlossary;

public enum ChanTypeEnum implements IGlossary {
    BTC,
    LTC,
    ETH,
    ETC,
    BCH,
    USDT_BTC(BTC),
    OTHER_ETH(ETH);

    ChanTypeEnum() {
    }

    ChanTypeEnum(ChanTypeEnum parentType) {
        this.parent = parentType;
    }

    ChanTypeEnum(ChanTypeEnum parentType, String contract) {
        this.parent = parentType;
    }

    private String contract;

    ChanTypeEnum parent;

    public ChanTypeEnum getParent() {
        return parent;
    }

    public String getContract() {
        return contract;
    }

    @Override
    public String getName() {
        return name();
    }

    @Override
    public Integer getValue() {
        return null;
    }
}
