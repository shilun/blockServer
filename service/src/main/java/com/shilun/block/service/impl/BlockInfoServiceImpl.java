package com.shilun.block.service.impl;

import com.common.mongo.AbstractMongoService;
import com.shilun.block.domain.BlockInfo;
import com.shilun.block.domain.module.ChanTypeEnum;
import com.shilun.block.service.BlockInfoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class BlockInfoServiceImpl extends AbstractMongoService<BlockInfo> implements BlockInfoService {
    @Override
    protected Class<BlockInfo> getEntityClass() {
        return BlockInfo.class;
    }


    @Override
    public BlockInfo findLastBlock(ChanTypeEnum chanTypeEnum) {
        BlockInfo query = new BlockInfo();
        query.setOrderTpe(2);
        query.setChanTypeEnum(chanTypeEnum.name());
        query.setOrderColumn("blockHeight");
        Page<BlockInfo> blockInfos = queryByPage(query, new PageRequest(0, 1));
        if (!blockInfos.hasContent()) {
            return null;
        }
        return blockInfos.getContent().get(0);
    }
}
