package cn.edu.xmu.freight.service;

import cn.edu.xmu.freight.dao.FreightDao;
import cn.edu.xmu.freight.model.bo.FreightModelChangeBo;
import cn.edu.xmu.freight.model.bo.PieceFreightModelChangeBo;
import cn.edu.xmu.freight.model.bo.WeightFreightModelChangeBo;
import cn.edu.xmu.freight.model.po.FreightModelPo;
import cn.edu.xmu.freight.model.vo.FreightModelChangeVo;
import cn.edu.xmu.freight.model.vo.PieceFreightModelChangeVo;
import cn.edu.xmu.freight.model.vo.WeightFreightModelChangeVo;
import cn.edu.xmu.ooad.util.ReturnObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 用户服务
 * @author Ming Qiu
 * Modified at 2020/11/5 10:39
 **/
@Service
public class FreightService {

    private Logger logger = LoggerFactory.getLogger(FreightService.class);

    @Autowired
    private FreightDao freightDao;

    public ReturnObject<Object> changeFreightModel(Long id, FreightModelChangeVo freightModelChangeVo,
                                                     Long shopId)
    {
        FreightModelChangeBo freightModelChangeBo = freightModelChangeVo.createFreightModelBo();
        freightModelChangeBo.setShopId(shopId);
        freightModelChangeBo.setId(id);

        return freightDao.changeFreightModel(freightModelChangeBo);
    }

    public ReturnObject<Object> changeWeightFreightModel(Long id, WeightFreightModelChangeVo weightFreightModelChangeVo,
                                                         Long shopId)
    {
        WeightFreightModelChangeBo weightFreightModelChangeBo = weightFreightModelChangeVo.createWeightFreightModelBo();
        weightFreightModelChangeBo.setId(id);

        return freightDao.changeWeightFreightModel(weightFreightModelChangeBo, shopId);
    }

    public ReturnObject<Object> changePieceFreightModel(Long id, PieceFreightModelChangeVo pieceFreightModelChangeVo,
                                                        Long shopId)
    {
        PieceFreightModelChangeBo pieceFreightModelChangeBo = pieceFreightModelChangeVo.createPieceFreightModelChangeBo();
        pieceFreightModelChangeBo.setId(id);

        return  freightDao.changePieceFreightModel(pieceFreightModelChangeBo, shopId);
    }
}
