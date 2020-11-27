package cn.edu.xmu.freight.service;

import cn.edu.xmu.freight.dao.FreightDao;
import cn.edu.xmu.freight.model.bo.FreightModel;
import cn.edu.xmu.freight.model.bo.PieceFreightModel;
import cn.edu.xmu.freight.model.bo.WeightFreightModel;
import cn.edu.xmu.freight.model.po.FreightModelPo;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

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
            retFrei = new ReturnObject<>(retObj.getData());
        } else {
            retFrei = new ReturnObject<>(retObj.getCode(), retObj.getErrmsg());
        }
        return retFrei;
    }


    @Transactional
    public ReturnObject<VoObject> insertPieceFreightModel(PieceFreightModel pieceFreightModel) {
        ReturnObject<VoObject> retPieceFreightModel;
        //pieceFreightModel.setId((long) 1);
        ReturnObject<PieceFreightModel> retObj = freightDao.insertPieceFreightModel(pieceFreightModel);

        if (retObj.getCode().equals(ResponseCode.OK)) {
            retPieceFreightModel = new ReturnObject<>(retObj.getData());
        } else {
            retPieceFreightModel = new ReturnObject<>(retObj.getCode(), retObj.getErrmsg());
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
            retWeightFreightModel = new ReturnObject<>(retObj.getData());
        } else {
            retWeightFreightModel = new ReturnObject<>(retObj.getCode(), retObj.getErrmsg());
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
    public ReturnObject<VoObject> insertFreightModel(FreightModelPo freightModelPo) {
        ReturnObject<FreightModelPo> retObj = freightDao.insertFreightModel(freightModelPo);
        ReturnObject<VoObject> retFreightModel;
        if (retObj.getCode().equals(ResponseCode.OK)) {
            retFreightModel = new ReturnObject<>((VoObject) retObj.getData());
        } else {
            retFreightModel = new ReturnObject<>( retObj.getCode(), retObj.getErrmsg());
        }
        return retFreightModel;
    }






}

