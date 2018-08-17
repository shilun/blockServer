package com.shilun.block.service;

import com.common.mongo.MongoService;
import com.shilun.block.domain.BlockInfo;
import com.shilun.block.domain.module.ChanTypeEnum;

public interface BlockInfoService extends MongoService<BlockInfo> {
    /**
     * 查询数据库中最后同步成功的真块
     * @return
     */
    BlockInfo findLastBlock(ChanTypeEnum chanTypeEnum);
}
