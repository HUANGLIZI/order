package cn.edu.xmu.freight.service;

import cn.edu.xmu.freight.dao.FreightDao;
import cn.edu.xmu.freight.model.bo.FreightModelChangeBo;
import cn.edu.xmu.freight.model.bo.PieceFreightModelChangeBo;
import cn.edu.xmu.freight.model.bo.WeightFreightModelChangeBo;
import cn.edu.xmu.freight.model.po.FreightModelPo;
import cn.edu.xmu.freight.model.vo.FreightModelChangeVo;
import cn.edu.xmu.freight.model.vo.PieceFreightModelChangeVo;
import cn.edu.xmu.freight.model.vo.WeightFreightModelChangeVo;
import cn.edu.xmu.freight.model.bo.FreightModel;
import cn.edu.xmu.freight.model.bo.PieceFreightModel;
import cn.edu.xmu.freight.model.bo.WeightFreightModel;
import cn.edu.xmu.freight.model.po.FreightModelPo;
import cn.edu.xmu.freight.model.vo.FreightModelRetVo;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class FreightService {
    //private Logger logger = LoggerFactory.getLogger(FreightService.class);

    @Autowired
    FreightDao freightDao;

    public boolean createDefaultPieceFreight(Long id,Long shopId){
        return freightDao.putDefaultPieceFreight(id,shopId);

    }

    @Transactional
    public ReturnObject<VoObject> createDefaultPieceFreight1(Long id,Long shopId){
        ReturnObject<FreightModel> retObj = freightDao.putDefaultPieceFreight1(id,shopId);
        ReturnObject<VoObject> retFrei = null;
        if (retObj.getCode().equals(ResponseCode.OK)) {
            retFrei = new ReturnObject<>((VoObject)retObj.getData());
        } else {
            retFrei = new ReturnObject(retObj.getCode(), retObj.getErrmsg());
        }
        return retFrei;
    }


    @Transactional
    public ReturnObject<VoObject> insertPieceFreightModel(PieceFreightModel pieceFreightModel) {
        ReturnObject<VoObject> retPieceFreightModel;
        //pieceFreightModel.setId((long) 1);
        ReturnObject<PieceFreightModel> retObj = freightDao.insertPieceFreightModel(pieceFreightModel);

        if (retObj.getCode().equals(ResponseCode.OK)) {
            retPieceFreightModel = new ReturnObject<>((VoObject)retObj.getData());
        } else {
            retPieceFreightModel = new ReturnObject(retObj.getCode(), retObj.getErrmsg());
        }
        return retPieceFreightModel;
    }

    //    public ReturnObject<VoObject> insertWeightFreightModel(WeightFreightModel weightFreightModel) {
//
//    }
//
    @Transactional
    public ReturnObject<VoObject> insertWeightFreightModel(WeightFreightModel weightFreightModel) {
        ReturnObject<WeightFreightModel> retObj = freightDao.insertWeightFreightModel(weightFreightModel);
        ReturnObject<VoObject> retWeightFreightModel;
        if (retObj.getCode().equals(ResponseCode.OK)) {
            retWeightFreightModel = new ReturnObject<>((VoObject)retObj.getData());
        } else {
            retWeightFreightModel = new ReturnObject(retObj.getCode(), retObj.getErrmsg());
        }
        return retWeightFreightModel;
    }
    /**
     * 新增店铺的运费模板
     * @author 24320182203227 李子晗
     * @param freightModelPo 运费模板视图
     * @return ReturnObject<VoObject> 运费模板返回视图
     */
    @Transactional
    public ReturnObject<VoObject> insertFreightModel(FreightModel freightModel) {
        ReturnObject<FreightModel> retObj = freightDao.insertFreightModel(freightModel);
        ReturnObject<VoObject> retFreightModel;
        if (retObj.getCode().equals(ResponseCode.OK)) {
            retFreightModel = new ReturnObject<>((VoObject) retObj.getData());
        } else {
            retFreightModel = new ReturnObject( retObj.getCode(), retObj.getErrmsg());
        }
        return retFreightModel;
    }

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
    /**
     * 计算运费
     * @author 24320182203227 李子晗
     * @param count 数量
     * @param skuId 对应的SkuId
     * @return ReturnObject<VoObject> 运费模板返回视图
     */
    @Transactional
    public ReturnObject<Integer> calcuFreightPrice(List<Integer> count, List<String> skuId) {
        Integer freightPrice = null;
        //根据skuId查询模板、重量,查询默认运费模板
        //根据重量、count并比较算出freightPrice
        ReturnObject<Integer> retFreightModel = new ReturnObject<>(freightPrice);
        //retFreightModel = new ReturnObject(ResponseCode.OK);
        return retFreightModel;
    }
}
