package cn.edu.xmu.freight.dao;

import cn.edu.xmu.freight.mapper.FreightModelPoMapper;
import cn.edu.xmu.freight.mapper.PieceFreightModelPoMapper;
import cn.edu.xmu.freight.mapper.WeightFreightModelPoMapper;
import cn.edu.xmu.freight.model.bo.FreightModel;
import cn.edu.xmu.freight.model.bo.PieceFreightModel;
import cn.edu.xmu.freight.model.bo.WeightFreightModel;
import cn.edu.xmu.freight.model.po.*;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.RandomCaptcha;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * @author Ming Qiu
 * @date Created in 2020/11/1 11:48
 * Modified in 2020/11/8 0:57
 **/
@Repository
public class FreightDao{
    private static final Logger logger = LoggerFactory.getLogger(FreightDao.class);

    @Autowired
    FreightModelPoMapper freightModelPoMapper;

    @Autowired
    WeightFreightModelPoMapper weightFreightModelPoMapper;

    @Autowired
    PieceFreightModelPoMapper pieceFreightModelPoMapper;

    /**
     * 分页查询店铺的所有运费模板
     *
     * @author 24320182203327 张湘君
     * @param page 页数
     * @param pageSize 每页大小
     * @return ReturnObject<PageInfo<VoObject>> 模板列表
     * createdBy 张湘君 2020/11/25 20:12
     * modifiedBy 张湘君 2020/11/25 20:12
     */
    public ReturnObject<PageInfo<VoObject>> getShopAllFreightModels(Long shopId,String name, Integer page, Integer pageSize){
        FreightModelPoExample example = new FreightModelPoExample();
        FreightModelPoExample.Criteria criteria = example.createCriteria();
        criteria.andShopIdEqualTo(shopId);
        if(!"".equals(name)&&name!=null){
            criteria.andNameLike("%"+name+"%");
        }

        //分页查询
        PageHelper.startPage(page, pageSize);
        logger.debug("page = " + page + "pageSize = " + pageSize);
        List<FreightModelPo> freightModelPos;
        try {
            //不加限定条件查询所有
            freightModelPos = freightModelPoMapper.selectByExample(example);
            List<VoObject> ret = new ArrayList<>(freightModelPos.size());
            for (FreightModelPo po : freightModelPos) {
                FreightModel freightModel = new FreightModel(po);
                ret.add(freightModel);
            }
            PageInfo<VoObject> freightModelPage = PageInfo.of(ret);
            return new ReturnObject<>(freightModelPage);
        }
        catch (DataAccessException e){
            logger.error("getShopAllFreightModels: DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
    }

    /**
     * 通过id获得运费模板的概要
     *
     * @author 24320182203327 张湘君
     * @param id 运费模板id
     * @return ReturnObject运费模板查询结果
     * createdBy 张湘君 2020/11/27 20:12
     * modifiedBy 张湘君 2020/11/27 20:12
     */
    public ReturnObject getFreightModelById(Long id) {
        FreightModelPo freightModelPo = freightModelPoMapper.selectByPrimaryKey(id);
        //po对象为空，没查到
        if (freightModelPo == null) {
            logger.error("getFreightModelById: 数据库不存在该运费模板 freightmodel_id=" + id);
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        FreightModel freightModel = new FreightModel(freightModelPo);

        return new ReturnObject<>(freightModel);
    }

    /**
     * 插入店铺的运费模板概要。
     *
     * @author 24320182203327 张湘君
     * @param shopId 店铺rid
     * @param id 模板id
     * @return Object 返回clone出来的对象
     * createdBy 张湘君 2020/11/27 20:12
     * modifiedBy 张湘君 2020/11/27 20:12
     */
    public ReturnObject insertCloneFreightModel(Long shopId, long id) {

        ReturnObject<FreightModel> retObj;

        //根据id先找到对应的模板
        FreightModelPo cloneFreightModelPo=freightModelPoMapper.selectByPrimaryKey(id);

        //id置为null
        cloneFreightModelPo.setId(null);

        //改变shopId
        cloneFreightModelPo.setShopId(shopId);

        //在模板name后面加随机数
        cloneFreightModelPo.setName(cloneFreightModelPo.getName()+ RandomCaptcha.getRandomString(6));
        //如果命名重复则更换，直到命名不重复
        while(isFreightModelNameExist(cloneFreightModelPo.getName())){
            cloneFreightModelPo.setName(cloneFreightModelPo.getName()+ RandomCaptcha.getRandomString(6));
        }

        cloneFreightModelPo.setDefaultModel("false");
        cloneFreightModelPo.setGmtCreated(LocalDateTime.now());
        cloneFreightModelPo.setGmtModified(LocalDateTime.now());

        try {
            //获得返回码
            int ret=freightModelPoMapper.insertSelective(cloneFreightModelPo);

            if(ret==0){
                //插入失败
                logger.debug("insertCloneFreightModel: insert cloneFreightModel fail " + cloneFreightModelPo.toString());
                retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, "新增运费模板失败：" + cloneFreightModelPo.getName());
            }else{
                //插入成功
                logger.debug("insertCloneFreightModel: insert cloneFreightModel = " +cloneFreightModelPo.toString());
                //po生成bo并返回
                retObj = new ReturnObject<>(new FreightModel(cloneFreightModelPo));
            }
        }
        catch (DataAccessException e) {
            if (Objects.requireNonNull(e.getMessage()).contains("name_uindex")) {
                //若有重复的角色名则新增失败
                logger.debug("insertCloneFreightModel: have same freight_model name = " + cloneFreightModelPo.getName());
                retObj = new ReturnObject<>(ResponseCode.ROLE_REGISTERED, "角色名重复：" + cloneFreightModelPo.getName());
            } else {
                // 其他数据库错误
                logger.debug("other sql exception : " + e.getMessage());
                retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
            }
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
        return retObj;

    }

    /**
     * 检查运费模板名重复
     * @param name 运费模板名称
     * @return boolean
     * createdBy 张湘君 2020/11/27 20:12
     * modifiedBy 张湘君 2020/11/27 20:12
     */
    public boolean isFreightModelNameExist(String name){
        logger.debug("is checking name in freight_model table");
        FreightModelPoExample example=new FreightModelPoExample ();
        FreightModelPoExample.Criteria criteria=example.createCriteria();
        criteria.andNameEqualTo(name);
        List<FreightModelPo> freightModelPo=freightModelPoMapper.selectByExample(example);
        return !freightModelPo.isEmpty();
    }


    /**
     * 插入店铺的运费模板详情。
     *
     * @author 24320182203327 张湘君
     * @param oldId 旧rid
     * @param newId 新id
     * @param type 末班类型
     * @return Object 返回clone出来的对象
     * createdBy 张湘君 2020/11/27 20:12
     * modifiedBy 张湘君 2020/11/27 20:12
     */
    public ReturnObject insertCloneFreightModelInfo(long oldId, long newId , short type) {
        ReturnObject retObj = null;

        //先判断类型，如果是0为重量，1为件数
        if(type==0){
            WeightFreightModelPoExample example=new WeightFreightModelPoExample();
            WeightFreightModelPoExample.Criteria criteria =example.createCriteria();
            criteria.andFreightModelIdEqualTo(oldId);
            //根据运费模板id先找到对应的模板
            List<WeightFreightModelPo> cloneWeightFreightModelPoS= weightFreightModelPoMapper.selectByExample(example);

            if(cloneWeightFreightModelPoS.isEmpty()){
                //插入失败
                logger.error("getFreightModelById: 数据库不存在该重量运费模板 freightmodel_id=" + oldId);
                retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, "新增重量运费模板失败：freightmodel_id=" + newId);
                return retObj;
            }
            for(WeightFreightModelPo cloneWeightFreightModelPo:cloneWeightFreightModelPoS){
                //id置为null
                cloneWeightFreightModelPo.setId(null);

                //运费模板id置为新的
                cloneWeightFreightModelPo.setFreightModelId(newId);

                cloneWeightFreightModelPo.setGmtCreated(LocalDateTime.now());
                cloneWeightFreightModelPo.setGmtModified(LocalDateTime.now());

                try {
                    //获得返回码
                    int ret=weightFreightModelPoMapper.insertSelective(cloneWeightFreightModelPo);

                    if(ret==0){
                        //插入失败
                        logger.debug("insertCloneFreightModelInfo: insert cloneFreightModelInfo fail " + cloneWeightFreightModelPo.toString());
                        retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, "新增重量运费模板失败：freightmodel_id=" + newId);
                    }else{
                        //插入成功
                        logger.debug("insertCloneFreightModelInfo: insert cloneFreightModelInfo = " +cloneWeightFreightModelPo.toString());
                        //po生成bo并返回
                        retObj = new ReturnObject<>(new WeightFreightModel(cloneWeightFreightModelPo));
                    }
                }
                catch (DataAccessException e) {
                    // 其他数据库错误
                    logger.debug("other sql exception : " + e.getMessage());
                    retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));

                }
                catch (Exception e) {
                    // 其他Exception错误
                    logger.error("other exception : " + e.getMessage());
                    retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
                }

            }
        }else if(type==1){
            PieceFreightModelPoExample example=new PieceFreightModelPoExample();
            PieceFreightModelPoExample.Criteria criteria =example.createCriteria();
            criteria.andFreightModelIdEqualTo(oldId);
            //根据运费模板id先找到对应的模板
            List<PieceFreightModelPo> clonePieceFreightModelPoS= pieceFreightModelPoMapper.selectByExample(example);

            if(clonePieceFreightModelPoS.isEmpty()){
                //插入失败
                logger.error("getFreightModelById: 数据库不存在该件数运费模板 freightmodel_id=" + oldId);
                retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, "新增件数运费模板失败：freightmodel_id=" + newId);
                return retObj;
            }

            for(PieceFreightModelPo clonePieceFreightModelPo:clonePieceFreightModelPoS){

                //id置为null
                clonePieceFreightModelPo.setId(null);

                //运费模板id置为新的
                clonePieceFreightModelPo.setFreightModelId(newId);

                clonePieceFreightModelPo.setGmtCreated(LocalDateTime.now());
                clonePieceFreightModelPo.setGmtModified(LocalDateTime.now());

                try {
                    //获得返回码
                    int ret=pieceFreightModelPoMapper.insertSelective(clonePieceFreightModelPo);

                    if(ret==0){
                        //插入失败
                        logger.debug("insertCloneFreightModelInfo: insert cloneFreightModelInfo fail " + clonePieceFreightModelPo.toString());
                        retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, "新增件数运费模板失败：freightmodel_id=" + newId);
                    }else{
                        //插入成功
                        logger.debug("insertCloneFreightModelInfo: insert cloneFreightModelInfo = " +clonePieceFreightModelPo.toString());
                        //po生成bo并返回
                        PieceFreightModel pieceFreightModel=new PieceFreightModel(clonePieceFreightModelPo);
                        retObj = new ReturnObject<>(pieceFreightModel);
                    }
                }
                catch (DataAccessException e) {
                    // 其他数据库错误
                    logger.debug("other sql exception : " + e.getMessage());
                    retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));

                }
                catch (Exception e) {
                    // 其他Exception错误
                    logger.error("other exception : " + e.getMessage());
                    retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
                }

            }
        }
        return retObj;
    }

    /**
     * 删除运费模板，需同步删除与商品的
     *
     * @author 24320182203327 张湘君
     * @param shopId 店铺rid
     * @param id 模板id
     * @return ReturnObject<Object> 删除结果
     * createdBy 张湘君 2020/11/28 20:12
     * modifiedBy 张湘君 2020/11/28 20:12
     */
    public ReturnObject<Object> delShopFreightModel(Long shopId, Long id) {
        ReturnObject<Object> returnObject;
        FreightModelPoExample example=new FreightModelPoExample ();
        FreightModelPoExample.Criteria criteria=example.createCriteria();
        criteria.andShopIdEqualTo(shopId);
        criteria.andIdEqualTo(id);

        int ret = freightModelPoMapper.deleteByExample(example);
        if(ret==0){
           //资源不存在
           returnObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }else {
            returnObject=new ReturnObject<>();
        }

        return  returnObject;
    }
}

