package cn.edu.xmu.freight.service;

import cn.edu.xmu.freight.dao.FreightDao;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import cn.edu.xmu.freight.model.bo.FreightModelChangeBo;
import cn.edu.xmu.freight.model.bo.PieceFreightModelChangeBo;
import cn.edu.xmu.freight.model.bo.WeightFreightModelChangeBo;
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

@Service
public class FreightService {
    @Autowired
    private FreightDao freightDao;

    private Logger logger = LoggerFactory.getLogger(FreightService.class);



    /**
     * 新增店铺的运费模板
     * @author 24320182203227 李子晗
     * @param freightModel 运费模板视图
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


    /**
     * 分页查询店铺的所有运费模板
     *
     * @author 24320182203327 张湘君
     * @param page 页数
     * @param pageSize 每页大小
     * @return ReturnObject<PageInfo<VoObject>> 运费模板分页查询结果
     * createdBy 张湘君 2020/11/25 20:12
     * modifiedBy 张湘君 2020/11/25 20:12
     */
    public ReturnObject<PageInfo<VoObject>> getShopAllFreightModels(Long shopId, String name, Integer page, Integer pageSize) {
        ReturnObject<PageInfo<VoObject>> returnObject = freightDao.getShopAllFreightModels(shopId, name, page, pageSize);
        return returnObject;
    }

    /**
     * 通过id获得运费模板的概要
     *
     * @author 24320182203327 张湘君
     * @param id 运费模板id
     * @return Object 运费模板查询结果
     * createdBy 张湘君 2020/11/25 20:12
     * modifiedBy 张湘君 2020/11/25 20:12
     */
    public ReturnObject getFreightModelById(Long id) {
        return freightDao.getFreightModelById(id);
    }


    /**
     * 管理员克隆店铺的运费模板。
     *
     * @author 24320182203327 张湘君
     * @param shopId 店铺rid
     * @param id 模板id
     * @return Object 返回clone出来的对象
     * createdBy 张湘君 2020/11/27 20:12
     * modifiedBy 张湘君 2020/11/27 20:12
     */
    public ReturnObject cloneShopFreightModel(Long shopId, long id) {

        //获得克隆的运费模板
        ReturnObject<FreightModel> cloneFreightModelRetObj=freightDao.insertCloneFreightModel(shopId,id);

        if(cloneFreightModelRetObj.getCode().equals(ResponseCode.OK)){
            FreightModel cloneFreightModel=cloneFreightModelRetObj.getData();
            short type=cloneFreightModel.getType();
            long newId=cloneFreightModel.getId();
            //克隆运费模板明细
            ReturnObject cloneFreightModelInfoRetObj=freightDao.insertCloneFreightModelInfo(id,newId,type);
            if(cloneFreightModelInfoRetObj.getCode()==ResponseCode.OK){
                return cloneFreightModelRetObj;
            }else{
                return cloneFreightModelInfoRetObj;
            }
        }else{
            return cloneFreightModelRetObj;
        }

    }

    /**
     * 删除运费模板，需同步删除与商品的
     *
     * @author 24320182203327 张湘君
     * @param shopId 店铺id
     * @param id 模板id
     * @return ReturnObject<Object> 删除结果
     * createdBy 张湘君 2020/11/28 20:12
     * modifiedBy 张湘君 2020/11/28 20:12
     */
    public ReturnObject<Object> delShopFreightModel(Long shopId, Long id) {
        //物理删除
        ReturnObject returnObject=freightDao.delShopFreightModel(shopId,id);
        if (returnObject.getCode() == ResponseCode.OK) {

            //在这里调用商品模块的api修改相应商品的freight_id
            
        }

        return returnObject;
    }

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

    /**
     * 店家或管理员为商铺定义默认运费模板
     * @author 24320182203196 洪晓杰
     */
    @Transactional
    public ReturnObject<VoObject> createDefaultPieceFreight(Long id,Long shopId){
        ReturnObject<VoObject> retFrei;
        ReturnObject<FreightModel> retObj = freightDao.putDefaultPieceFreight(id,shopId);

        if (retObj.getCode().equals(ResponseCode.OK)) {
            retFrei = new ReturnObject<>(retObj.getData());
        } else {
            retFrei = new ReturnObject<>(retObj.getCode(), retObj.getErrmsg());
        }
        return retFrei;
    }


    /**
     * 管理员定义件数模板明细
     * @author 24320182203196 洪晓杰
     */
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

    /**
     * 管理员定义管理员定义重量模板明细
     * @author 24320182203196 洪晓杰
     */
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
     * 查询某个重量运费模板明细
     * @author li mingming
     * @param shopId 店铺Id
     * @param id 重量运费模板明细id
     * @return ReturnObject
     */
    @Transactional
    public ReturnObject<VoObject> getWeightItemsByFreightModelId(Long shopId, Long id)
    {
        return freightDao.getWeightItemByFreightModelId(shopId, id);
    }

    /**
     * 查询某个件数运费模板明细
     * @author li mingming
     * @param shopId 店铺Id
     * @param id 件数运费模板明细id
     * @return ReturnObject
     */
    @Transactional
    public ReturnObject<VoObject> getPieceItemsByFreightModelId(Long shopId, Long id)
    {
        return freightDao.getPieceItemByFreightModelId(shopId,id);
    }

    /**
     * 删除某个重量运费模板明细
     * @author li mingming
     * @param id 重量运费模板明细id
     * @return ReturnObject
     */
    @Transactional
    public ReturnObject<VoObject> delWeightItemById(Long shopId, Long id)
    {
        return freightDao.delWeightItemById(shopId, id);
    }

    /**
     * 删除某个件数运费模板明细
     * @author li mingming
     * @param id 件数运费模板明细id
     * @return ReturnObject
     */
    @Transactional
    public ReturnObject<VoObject> delPieceItemById(Long shopId, Long id)
    {
        return freightDao.delPieceItemById(shopId, id);
    }





}
