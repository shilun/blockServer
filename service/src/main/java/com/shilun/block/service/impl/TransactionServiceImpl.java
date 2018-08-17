package com.shilun.block.service.impl;

import com.common.mongo.AbstractMongoService;
import com.shilun.block.domain.TransInfo;
import com.shilun.block.service.TransactionService;
import org.springframework.stereotype.Service;

@Service
public class TransactionServiceImpl extends AbstractMongoService<TransInfo> implements TransactionService {
    @Override
    protected Class<TransInfo> getEntityClass() {
        return TransInfo.class;
    }
}
