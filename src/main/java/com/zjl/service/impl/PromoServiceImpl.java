package com.zjl.service.impl;

import com.zjl.dao.PromoDOMapper;
import com.zjl.dataobject.PromoDO;
import com.zjl.enums.PromoStatusEnum;
import com.zjl.service.PromoService;
import com.zjl.service.model.PromoModel;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @author zhangjiling
 * @date 2023/7/28 21:40
 */

@Service
public class PromoServiceImpl implements PromoService {

    @Autowired
    private PromoDOMapper promoDOMapper;

    @Override
    public PromoModel gerPromoByItemId(Integer itemId) {
        PromoDO promoDO = promoDOMapper.selectByItemId(itemId);
        PromoModel promoModel = convertPromoModelFromPromoDO(promoDO);

        if(promoModel == null) {
            return null;
        }


        if(promoModel.getStartDate().isAfterNow()) {
            promoModel.setStatus(PromoStatusEnum.READY.getCode());
        } else if(promoModel.getEndDate().isBeforeNow()) {
            promoModel.setStatus(PromoStatusEnum.NOPROMO.getCode());
        } else {
            promoModel.setStatus(PromoStatusEnum.RUNING.getCode());
        }
        return promoModel;
    }

    private PromoModel convertPromoModelFromPromoDO(PromoDO promoDO) {
        if(promoDO == null) {
            return null;
        }
        PromoModel promoModel = new PromoModel();
        BeanUtils.copyProperties(promoDO, promoModel);
        promoModel.setPromoItemPrice(BigDecimal.valueOf(promoDO.getPromoItemPrice()));
        promoModel.setStartDate(new DateTime(promoDO.getStartDate()));
        promoModel.setEndDate(new DateTime(promoDO.getEndDate()));
        return promoModel;
    }
}
