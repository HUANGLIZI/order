package cn.edu.xmu.freight.service;

import cn.edu.xmu.freight.dao.FreightDao;
import cn.edu.xmu.oomall.goods.model.GoodsFreightDTO;
import cn.edu.xmu.oomall.goods.service.GoodsService;
import cn.edu.xmu.oomall.order.service.IFreightService;
import com.github.pagehelper.PageInfo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
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

@DubboService
public class FreightService implements IFreightService {
    @Autowired
    private FreightDao freightDao;

    @DubboReference
    private GoodsService goodsService;


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
            goodsService.updateSpuFreightId(id);
            
        }

        return returnObject;
    }

    /**
     * 修改运费模板
     * @author Cai Xinlu
     * @date 2020-12-10 9:40
     */
    public ReturnObject<ResponseCode> changeFreightModel(Long id, FreightModelChangeVo freightModelChangeVo,
                                                     Long shopId, Long sId)
    {
        if (!shopId.equals(sId))
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
        FreightModelChangeBo freightModelChangeBo = freightModelChangeVo.createFreightModelBo();
        freightModelChangeBo.setShopId(shopId);
        freightModelChangeBo.setId(id);

        return freightDao.changeFreightModel(freightModelChangeBo);
    }

    /**
     * 修改重量运费模板明细
     * @author Cai Xinlu
     * @date 2020-12-10 9:40
     */
    public ReturnObject<ResponseCode> changeWeightFreightModel(Long id, WeightFreightModelChangeVo weightFreightModelChangeVo,
                                                         Long shopId, Long sId)
    {
        if (!shopId.equals(sId))
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
        WeightFreightModelChangeBo weightFreightModelChangeBo = weightFreightModelChangeVo.createWeightFreightModelBo();
        weightFreightModelChangeBo.setId(id);

        return freightDao.changeWeightFreightModel(weightFreightModelChangeBo, shopId);
    }

    /**
     * 修改件数运费模板
     * @author Cai Xinlu
     * @date 2020-12-10 9:40
     */
    public ReturnObject<ResponseCode> changePieceFreightModel(Long id, PieceFreightModelChangeVo pieceFreightModelChangeVo,
                                                        Long shopId, Long sId)
    {
        if (!shopId.equals(sId))
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
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
    @Override
    public ReturnObject<Long> calcuFreightPrice(List<Integer> count, List<Long> skuId,Long regionId) {
        Long freightPrice = 0L;
        //根据skuId查询模板、重量,查询默认运费模板
        //根据重量、count并比较算出freightPrice
        List<GoodsFreightDTO> goodsFreightDTO = null;
        List<Long> freightModelId = null;
        List<FreightModelPo> freightModelPos = null;//运费模板列表
        Long weightSum = 0L;
        Integer countSum=0;
        for (int i=0;i<skuId.size();i++) {
            goodsFreightDTO.add(goodsService.getGoodsFreightDetailBySkuId(skuId.get(i)).getData());
            weightSum+=goodsFreightDTO.get(i).getWeight();//计算总重量
            countSum+=count.get(i);//计算总件数
            if(goodsFreightDTO.get(i).getFreightModelId()==null) {//如果没有单品运费模板,采用默认模板
                Long shopId=goodsFreightDTO.get(i).getShopId();
                FreightModelPo defaultFreightModel = freightDao.getDefaultFreightModelByshopId(shopId);
                freightModelId.add(defaultFreightModel.getId());//获得默认运费模板id
                freightModelPos.add(defaultFreightModel);//将默认运费模板加入运费模板列表
            }
            else{
                freightModelId.add(goodsFreightDTO.get(i).getFreightModelId());//获得单品运费模板id列表
                freightModelPos.add((FreightModelPo)(freightDao.getFreightModelById(goodsFreightDTO.get(i).getFreightModelId()).getData()));//将单品运费模板加入运费模板列表
            }
        }
        for (int i=0;i<freightModelPos.size();i++) {
            FreightModelPo freightModelPo_temp=freightModelPos.get(i);
            Long price_temp=0L;
            if (freightModelPo_temp.getType() == 1)//获得按件数计算的运费模板明细
            {
                PieceFreightModel pieceFreightModel_temp=freightDao.getPieceItemByFreightModelIdRegionId(freightModelPo_temp.getShopId(),freightModelPo_temp.getId(),regionId);
                price_temp= pieceFreightModel_temp.getFirstItemsPrice()+pieceFreightModel_temp.getAdditionalItemsPrice()*(countSum-1);
                if(price_temp>freightPrice) {
                    freightPrice=price_temp;
                }
            }
            else if (freightModelPo_temp.getType() == 0)//获得按重量计算的运费模板明细
            {
                WeightFreightModel weightFreightModel=freightDao.getWeightItemByFreightModelIdRegionId(freightModelPo_temp.getShopId(),freightModelPo_temp.getId(),regionId);
                if(weightSum<=weightFreightModel.getFirstWeight())
                    price_temp=weightFreightModel.getFirstWeightFreight();
                else if(weightSum>weightFreightModel.getFirstWeight()&&weightSum<=10)
                    price_temp=weightFreightModel.getFirstWeightFreight()+weightFreightModel.getTenPrice()*(long)(Math.ceil((weightSum-weightFreightModel.getFirstWeight())/0.5));
                else if(weightSum>10&&weightSum<=50)
                    price_temp=weightFreightModel.getFirstWeightFreight()+weightFreightModel.getTenPrice()*(long)Math.ceil((weightSum-weightFreightModel.getFirstWeight())/0.5)+weightFreightModel.getFiftyPrice()*(long)Math.ceil((weightSum-10)/0.5);
                else if(weightSum>50&&weightSum<=100)
                    price_temp=weightFreightModel.getFirstWeightFreight()+weightFreightModel.getTenPrice()*(long)Math.ceil((weightSum-weightFreightModel.getFirstWeight())/0.5)+weightFreightModel.getFiftyPrice()*(long)Math.ceil((weightSum-10)/0.5)+weightFreightModel.getHundredPrice()*(long)Math.ceil((weightSum-50)/0.5);
                else if(weightSum>100&&weightSum<=300)
                    price_temp=weightFreightModel.getFirstWeightFreight()+weightFreightModel.getTenPrice()*(long)Math.ceil((weightSum-weightFreightModel.getFirstWeight())/0.5)+weightFreightModel.getFiftyPrice()*(long)Math.ceil((weightSum-10)/0.5)+weightFreightModel.getHundredPrice()*(long)Math.ceil((weightSum-50)/0.5)+weightFreightModel.getTrihunPrice()*(long)Math.ceil((weightSum-100)/0.5);
                else if(weightSum>300)
                    price_temp=weightFreightModel.getFirstWeightFreight()+weightFreightModel.getTenPrice()*(long)Math.ceil((weightSum-weightFreightModel.getFirstWeight())/0.5)+weightFreightModel.getFiftyPrice()*(long)Math.ceil((weightSum-10)/0.5)+weightFreightModel.getHundredPrice()*(long)Math.ceil((weightSum-50)/0.5)+weightFreightModel.getTrihunPrice()*(long)Math.ceil((weightSum-100)/0.5)+weightFreightModel.getAbovePrice()*(long)Math.ceil((weightSum-300)/0.5);
                if(price_temp>freightPrice)
                    freightPrice=price_temp;
            }
        }
        ReturnObject<Long> retFreightModel = new ReturnObject<>(freightPrice);
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
    public ReturnObject<List> getWeightItemsByFreightModelId(Long shopId, Long id)
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
    public ReturnObject<List> getPieceItemsByFreightModelId(Long shopId, Long id)
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
