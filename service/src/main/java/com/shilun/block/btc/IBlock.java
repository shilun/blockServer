package com.shilun.block.btc;

public interface IBlock {
    /**
     * 区块高度
     * @return
     */
    Integer getblockcount();

    /**
     * 获取区块哈希
     * @param index
     * @return
     */
    String getblockhash(Integer index);


    /**
     * 获取区块
     * @param hash
     * @return
     */
    Object getblock(String hash);


}
