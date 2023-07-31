package com.zjl.service;

import com.zjl.service.model.PromoModel;

/**
 * @author zhangjiling
 * @date 2023/7/28 21:38
 */
public interface PromoService {
    PromoModel gerPromoByItemId(Integer itemId);
}
