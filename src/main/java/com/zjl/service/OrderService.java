package com.zjl.service;

import com.zjl.error.BusinessException;
import com.zjl.service.model.OrderModel;

/**
 * @author zhangjiling
 * @date 2023/7/28 0:10
 */
public interface OrderService {
    OrderModel create(Integer userId, Integer itemId, Integer amount) throws BusinessException;
}
