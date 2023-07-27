package com.zjl.service;

import com.zjl.error.BusinessException;
import com.zjl.service.model.ItemModel;

import java.util.List;

/**
 * @author zhangjiling
 * @date 2023/7/26 22:03
 */
public interface ItemService {

    //创建商品
    ItemModel createItem(ItemModel itemModel) throws BusinessException;

    //商品列表浏览
    List<ItemModel> listItem();

    //商品详情浏览
    ItemModel getItemById(Integer id);

    //减库存
    boolean decreaseStock(Integer itemId, Integer amount);

    //增加销量
    void increaseSales(Integer id, Integer amount);
}
