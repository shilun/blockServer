package com.shilun.block.btc;

public interface ITransaction {
    String getrawtransaction(String hash);
    Object decoderawtransaction(String content);
}
