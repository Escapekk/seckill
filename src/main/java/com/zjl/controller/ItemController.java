package com.zjl.controller;

import com.zjl.controller.viewobject.ItemVO;
import com.zjl.enums.PromoStatusEnum;
import com.zjl.error.BusinessException;
import com.zjl.response.CommonReturnType;
import com.zjl.service.ItemService;
import com.zjl.service.model.ItemModel;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhangjiling
 * @date 2023/7/26 23:36
 */
@RestController("item")
@RequestMapping("/item")
@CrossOrigin(allowCredentials = "true",allowedHeaders = "*")
public class ItemController extends BaseController {

    @Autowired
    private ItemService itemService;

    @PostMapping(value = "/create", consumes = {CONTENT_TYPE_FORMED})
    public CommonReturnType createItem(ItemModel itemModel) throws BusinessException {
        ItemModel item = itemService.createItem(itemModel);
        ItemVO itemVO = convertItemVOFromItemModel(item);
        return CommonReturnType.create(itemVO);
    }

    @GetMapping(value = "/get")
    public CommonReturnType get(@RequestParam(name = "id")Integer id) {
        ItemModel itemModel = itemService.getItemById(id);

        ItemVO itemVO = convertItemVOFromItemModel(itemModel);

        return CommonReturnType.create(itemVO);
    }

    @GetMapping(value = "/list")
    public CommonReturnType list() {
        List<ItemModel> itemModels = itemService.listItem();
        List<ItemVO> itemVOList = itemModels.stream().map(itemModel -> {
            ItemVO itemVO = convertItemVOFromItemModel(itemModel);
            return itemVO;
        }).collect(Collectors.toList());
        return CommonReturnType.create(itemVOList);
    }

    private ItemVO convertItemVOFromItemModel(ItemModel itemModel) {
        if(itemModel == null) {
            return null;
        }
        ItemVO itemVO = new ItemVO();
        BeanUtils.copyProperties(itemModel, itemVO);
        if(itemModel.getPromoModel() != null) {
            //有正在进行或即将进行的秒杀活动
            itemVO.setPromoStatus(itemModel.getPromoModel().getStatus());
            itemVO.setPromoId(itemModel.getPromoModel().getId());
            itemVO.setPromoPrice(itemModel.getPromoModel().getPromoItemPrice());
            itemVO.setStartDate(itemModel.getPromoModel().getStartDate().toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")));
        }else {
            itemVO.setPromoStatus(PromoStatusEnum.NOPROMO.getCode());
        }
        return itemVO;
    }
}
