package com.zjl.service.impl;

import com.zjl.dao.ItemDOMapper;
import com.zjl.dao.ItemStockDOMapper;
import com.zjl.dataobject.ItemDO;
import com.zjl.dataobject.ItemStockDO;
import com.zjl.enums.PromoStatusEnum;
import com.zjl.error.BusinessException;
import com.zjl.error.EmBusinessError;
import com.zjl.service.ItemService;
import com.zjl.service.PromoService;
import com.zjl.service.model.ItemModel;
import com.zjl.service.model.PromoModel;
import com.zjl.validator.ValidationResult;
import com.zjl.validator.ValidatorImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zhangjiling
 * @date 2023/7/26 22:11
 */
@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemDOMapper itemDOMapper;

    @Autowired
    private ItemStockDOMapper itemStockDOMapper;

    @Autowired
    private ValidatorImpl validator;

    @Autowired
    private PromoService promoService;

    @Override
    @Transactional
    public ItemModel createItem(ItemModel itemModel) throws BusinessException {
        ValidationResult validationResult = validator.validate(itemModel);
        if(validationResult.getHasErrors()) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, validationResult.getErrMsg());
        }

        ItemDO itemDO = convertItemDOFromItemModel(itemModel);
        itemDOMapper.insertSelective(itemDO);
        itemModel.setId(itemDO.getId());

        ItemStockDO itemStockDO = convertItemStockDOFromItemModel(itemModel);
        itemStockDOMapper.insertSelective(itemStockDO);
        return this.getItemById(itemModel.getId());
    }

    @Override
    public List<ItemModel> listItem() {
        List<ItemDO> itemDOs = itemDOMapper.listItem();
        List<ItemModel> itemModelList = itemDOs.stream().map(itemDO -> {
            ItemStockDO itemStockDO = itemStockDOMapper.selectByItemId(itemDO.getId());
            ItemModel itemModel = convertFromItemDOItemStockDO(itemDO, itemStockDO);
            return itemModel;
        }).collect(Collectors.toList());
        return itemModelList;
    }

    @Override
    public ItemModel getItemById(Integer id) {
        ItemDO itemDO = itemDOMapper.selectByPrimaryKey(id);
        if(itemDO == null) {
            return null;
        }
        ItemStockDO itemStockDO = itemStockDOMapper.selectByItemId(itemDO.getId());
        ItemModel itemModel = convertFromItemDOItemStockDO(itemDO, itemStockDO);

        //获取活动商品信息
        PromoModel promoModel = promoService.gerPromoByItemId(itemModel.getId());
        if(promoModel != null && promoModel.getStatus() != PromoStatusEnum.NOPROMO.getCode()) {
            itemModel.setPromoModel(promoModel);
        }

        return itemModel;
    }

    @Transactional
    @Override
    public boolean decreaseStock(Integer itemId, Integer amount) {
        int row = itemStockDOMapper.decreaseStock(itemId, amount);
        return row > 0;
    }

    @Transactional
    @Override
    public void increaseSales(Integer id, Integer amount) {
        itemDOMapper.increaseSales(id, amount);
    }

    private ItemDO convertItemDOFromItemModel(ItemModel itemModel) {
        ItemDO itemDO = new ItemDO();
        BeanUtils.copyProperties(itemModel, itemDO);
        itemDO.setPrice(itemModel.getPrice().doubleValue());
        return itemDO;
    }
    private ItemStockDO convertItemStockDOFromItemModel(ItemModel itemModel) {
        ItemStockDO itemStockDO = new ItemStockDO();
        itemStockDO.setItemId(itemModel.getId());
        itemStockDO.setStock(itemModel.getStock());
        return itemStockDO;
    }

    private ItemModel convertFromItemDOItemStockDO(ItemDO itemDO, ItemStockDO itemStockDO) {
        ItemModel itemModel = new ItemModel();
        BeanUtils.copyProperties(itemDO, itemModel);
        itemModel.setPrice(BigDecimal.valueOf(itemDO.getPrice()));
        itemModel.setStock(itemStockDO.getStock());
        return itemModel;
    }
}
