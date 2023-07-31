package com.zjl.service.impl;

import com.zjl.dao.OrderDOMapper;
import com.zjl.dao.SequenceDOMapper;
import com.zjl.dataobject.OrderDO;
import com.zjl.dataobject.SequenceDO;
import com.zjl.enums.PromoStatusEnum;
import com.zjl.error.BusinessException;
import com.zjl.error.EmBusinessError;
import com.zjl.service.ItemService;
import com.zjl.service.OrderService;
import com.zjl.service.UserService;
import com.zjl.service.model.ItemModel;
import com.zjl.service.model.OrderModel;
import com.zjl.service.model.UserMoldel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author zhangjiling
 * @date 2023/7/28 0:12
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ItemService itemService;


    @Autowired
    private UserService userService;

    @Autowired
    private OrderDOMapper orderDOMapper;

    @Autowired
    private SequenceDOMapper sequenceDOMapper;



    @Transactional
    @Override
    public OrderModel create(Integer userId, Integer itemId, Integer promoId, Integer amount) throws BusinessException {
        //1. 校验下单状态，下单的商品是否存在，用户是否合法，购买数量是否正确
        ItemModel itemModel = itemService.getItemById(itemId);
        if(itemModel == null ) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "商品信息不正确");
        }

        UserMoldel userMoldel = userService.getUserById(userId);
        if(userMoldel == null) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "用户信息不正确");
        }

        if(amount <= 0 || amount > 99) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "库存信息不正确");
        }

        //校验活动信息
        if(promoId != null) {
            if(promoId.intValue() != itemModel.getPromoModel().getId()) {
                throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "活动信息不正确");
            }else if(itemModel.getPromoModel().getStatus() != PromoStatusEnum.RUNING.getCode()) {
                throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "活动还未开始");
            }
        }

        //2. 落单减库存，支付减库存
        boolean result = itemService.decreaseStock(itemId, amount);
        if (!result) {
            throw new BusinessException(EmBusinessError.STOCK_NOT_ENOUGH, "库存不足");
        }

        //3. 订单入库
        OrderModel orderModel = new OrderModel();
        orderModel.setItemId(itemId);
        orderModel.setAmount(amount);
        orderModel.setUserId(userId);
        if(promoId != null) {
            orderModel.setItemPrice(itemModel.getPromoModel().getPromoItemPrice());
        } else {
            orderModel.setItemPrice(itemModel.getPrice());
        }
        orderModel.setOrderPrice(orderModel.getItemPrice().multiply(BigDecimal.valueOf(amount)));
        orderModel.setPromoId(promoId);

        //生成交易流水号,订单号
        orderModel.setId(generateOrderNo());
        OrderDO orderDO = convertOrderDOFromOrderModel(orderModel);

        //商品销量增加
        itemService.increaseSales(itemId, amount);

        orderDOMapper.insertSelective(orderDO);
        //4. 返回前端
        return orderModel;
    }

    private OrderDO convertOrderDOFromOrderModel(OrderModel orderModel) {
        if(orderModel == null) {
            return null;
        }
        OrderDO orderDO = new OrderDO();
        BeanUtils.copyProperties(orderModel, orderDO);
        orderDO.setOrderPrice(orderModel.getOrderPrice().doubleValue());
        orderDO.setItemPrice(orderModel.getItemPrice().doubleValue());
        return orderDO;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private String generateOrderNo() {
        StringBuilder key = new StringBuilder();

        //订单号有16位
        //前6位为时间信息，年月日，可以让订单拥有时间维度，可以切分
        LocalDateTime now = LocalDateTime.now();
        String nowDate = now.format(DateTimeFormatter.ISO_DATE).replace("-", "");
        key.append(nowDate);

        //中间6位为自增序列
        SequenceDO sequenceDO = sequenceDOMapper.getSequenceByName("order_info");
        Integer sequence = sequenceDO.getCurrentValue();
        sequenceDO.setCurrentValue(sequenceDO.getCurrentValue() + sequenceDO.getStep());
        sequenceDOMapper.updateByPrimaryKeySelective(sequenceDO);
        String sequenceStr = String.valueOf(sequence);
        for(int i = 0; i < 6 - sequenceStr.length(); i++) {
            key.append(0);
        }
        key.append(sequenceStr);


        //最后两位为分库分表位
        key.append("00");

        return key.toString();
    }
}
