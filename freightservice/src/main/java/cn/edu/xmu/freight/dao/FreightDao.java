package cn.edu.xmu.freight.dao;

import cn.edu.xmu.freight.mapper.FreightModelMapper;
import cn.edu.xmu.freight.mapper.FreightModelPoMapper;
import cn.edu.xmu.freight.mapper.PieceFreightModelPoMapper;
import cn.edu.xmu.freight.mapper.WeightFreightModelPoMapper;
import cn.edu.xmu.freight.model.bo.FreightModelChangeBo;
import cn.edu.xmu.freight.model.bo.PieceFreightModelChangeBo;
import cn.edu.xmu.freight.model.bo.WeightFreightModelChangeBo;
import cn.edu.xmu.freight.model.po.*;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import io.swagger.annotations.Example;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author Caixin
 * @date 2020-11-26 14:01
 */
@Repository
public class FreightDao {

    @Autowired
    private FreightModelMapper freightModelMapper;

    @Autowired
    private FreightModelPoMapper freightModelPoMapper;

    @Autowired
    private WeightFreightModelPoMapper weightFreightModelPoMapper;

    @Autowired
    private PieceFreightModelPoMapper pieceFreightModelPoMapper;

    public ReturnObject<Object> changeFreightModel(FreightModelChangeBo freightModelChangeBo) {
        FreightModelPo freightModelPo = freightModelChangeBo.gotFreightModelPo();
        ReturnObject<Object> retObj = null;

        String name = freightModelChangeBo.getName();
        if (name != null)
        {
            Long shopId = freightModelChangeBo.getShopId();
            FreightModelPoExample freightModelPoExample = new FreightModelPoExample();
            FreightModelPoExample.Criteria criteria = freightModelPoExample.createCriteria();
            criteria.andNameEqualTo(name);
            criteria.andShopIdEqualTo(shopId);
            List<FreightModelPo> freightModelPoList = freightModelPoMapper.selectByExample(freightModelPoExample);
            if (freightModelPoList.size() > 0) {
                retObj = new ReturnObject<>(ResponseCode.FREIGHTNAME_SAME);
                return retObj;
            }
        }

        int ret = freightModelMapper.updateFreightModel(freightModelPo);
        if (ret == 0) {
            retObj = new ReturnObject<>(ResponseCode.FREIGHTMODEL_SHOP_NOTFIT);
        } else {
            retObj = new ReturnObject<>();
        }
        return retObj;
    }

    public ReturnObject<Object> changeWeightFreightModel(WeightFreightModelChangeBo weightFreightModelChangeBo,
                                                         Long shopId) {
        ReturnObject<Object> retObj = null;

        WeightFreightModelPo weightFreightModelPo = weightFreightModelChangeBo.gotWeightFreightModelPo();
        WeightFreightModelPo retWeightFreightModel = weightFreightModelPoMapper.selectByPrimaryKey(weightFreightModelChangeBo.getId());
        if (retWeightFreightModel == null)
        {
            retObj = new ReturnObject<>(ResponseCode.FREIGHTMODEL_SHOP_NOTFIT);
            return retObj;
        }
        Long freightModelId = retWeightFreightModel.getFreightModelId();
        FreightModelPo freightModelPo = freightModelPoMapper.selectByPrimaryKey(freightModelId);

        // 判断商家与运费模板是否匹配
        if (freightModelPo.getShopId().equals(shopId)) {
            // 判断地区码是否已被定义
            if (weightFreightModelChangeBo.getRegionId() != null) {
                FreightModelPoExample freightModelPoExample = new FreightModelPoExample();
                FreightModelPoExample.Criteria criteria = freightModelPoExample.createCriteria();
                criteria.andShopIdEqualTo(shopId);
                // 以重量计的运费模板
                Byte type = 0;
                criteria.andTypeEqualTo(type);
                List<FreightModelPo> freightModelPoList = freightModelPoMapper.selectByExample(freightModelPoExample);
                for (FreightModelPo po : freightModelPoList) {
                    WeightFreightModelPoExample weightFreightModelPoExample = new WeightFreightModelPoExample();
                    WeightFreightModelPoExample.Criteria weightCriteria = weightFreightModelPoExample.createCriteria();
                    weightCriteria.andFreightModelIdEqualTo(po.getId());
                    weightCriteria.andRegionIdEqualTo(weightFreightModelChangeBo.getRegionId());
                    List<WeightFreightModelPo> weightFreightModelPoList = weightFreightModelPoMapper.selectByExample(weightFreightModelPoExample);
                    if (weightFreightModelPoList.size() > 0) {
                        retObj = new ReturnObject<>(ResponseCode.REGION_SAME);
                        return retObj;
                    }
                }
            }

        } else {
            retObj = new ReturnObject<>(ResponseCode.FREIGHTMODEL_SHOP_NOTFIT);
            return retObj;
        }


        int ret = freightModelMapper.updateWeightFreightModel(weightFreightModelPo);
        if (ret == 0) {
            retObj = new ReturnObject<>(ResponseCode.FREIGHTMODEL_ERROR);
        } else {
            retObj = new ReturnObject<>();
        }
        return retObj;
    }

    public ReturnObject<Object> changePieceFreightModel(PieceFreightModelChangeBo pieceFreightModelChangeBo,
                                                        Long shopId) {
        ReturnObject<Object> retObj = null;

        PieceFreightModelPo pieceFreightModelPo = pieceFreightModelChangeBo.gotPieceFreightModelPo();
        PieceFreightModelPo retPieceFreightModel = pieceFreightModelPoMapper.selectByPrimaryKey(pieceFreightModelChangeBo.getId());
        // 判断是否存在此运费模板明细，即数据库是否有此id
        if (retPieceFreightModel == null)
        {
            retObj = new ReturnObject<>(ResponseCode.FREIGHTMODEL_SHOP_NOTFIT);
            return retObj;
        }
        Long freightModelId = retPieceFreightModel.getFreightModelId();
        FreightModelPo freightModelPo = freightModelPoMapper.selectByPrimaryKey(freightModelId);

        // 判断商家与运费模板是否匹配
        if (freightModelPo.getShopId().equals(shopId)) {
            // 判断地区码是否已被定义
            if (pieceFreightModelChangeBo.getRegionId() != null) {
                FreightModelPoExample freightModelPoExample = new FreightModelPoExample();
                FreightModelPoExample.Criteria criteria = freightModelPoExample.createCriteria();
                criteria.andShopIdEqualTo(shopId);
                // 以数量计的运费模板
                Byte type = 1;
                criteria.andTypeEqualTo(type);
                List<FreightModelPo> freightModelPoList = freightModelPoMapper.selectByExample(freightModelPoExample);
                for (FreightModelPo po : freightModelPoList) {
                    PieceFreightModelPoExample pieceFreightModelPoExample = new PieceFreightModelPoExample();
                    PieceFreightModelPoExample.Criteria pieceCriteria = pieceFreightModelPoExample.createCriteria();
                    pieceCriteria.andFreightModelIdEqualTo(po.getId());
                    pieceCriteria.andRegionIdEqualTo(pieceFreightModelChangeBo.getRegionId());
                    List<PieceFreightModelPo> pieceFreightModelPoList = pieceFreightModelPoMapper.selectByExample(pieceFreightModelPoExample);
                    if (pieceFreightModelPoList.size() > 0) {
                        retObj = new ReturnObject<>(ResponseCode.REGION_SAME);
                        return retObj;
                    }
                }
            }

        } else {
            retObj = new ReturnObject<>(ResponseCode.FREIGHTMODEL_SHOP_NOTFIT);
            return retObj;
        }

        int ret = freightModelMapper.updatePieceFreightModel(pieceFreightModelPo);
        if (ret == 0) {
            retObj = new ReturnObject<>(ResponseCode.FREIGHTMODEL_ERROR);
        } else {
            retObj = new ReturnObject<>();
        }
        return retObj;
    }
}
