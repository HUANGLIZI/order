package cn.edu.xmu.freight.dao;

import cn.edu.xmu.freight.mapper.PieceFreightModelPoMapper;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.RandomCaptcha;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import cn.edu.xmu.freight.mapper.FreightModelMapper;
import cn.edu.xmu.freight.model.bo.FreightModelChangeBo;
import cn.edu.xmu.freight.model.bo.PieceFreightModelChangeBo;
import cn.edu.xmu.freight.model.bo.WeightFreightModelChangeBo;
import cn.edu.xmu.freight.model.po.*;
import io.swagger.annotations.Example;
import org.springframework.beans.factory.annotation.Autowired;
import cn.edu.xmu.freight.mapper.FreightModelPoMapper;
import cn.edu.xmu.freight.mapper.WeightFreightModelPoMapper;
import cn.edu.xmu.freight.model.bo.FreightModel;
import cn.edu.xmu.freight.model.bo.PieceFreightModel;
import cn.edu.xmu.freight.model.bo.WeightFreightModel;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.time.LocalDateTime;

@Repository
public class FreightDao{
    private static final Logger logger = LoggerFactory.getLogger(FreightDao.class);

    @Autowired
    private FreightModelMapper freightModelMapper;

    @Autowired
    private FreightModelPoMapper freightModelPoMapper;

    @Autowired
    private WeightFreightModelPoMapper weightFreightModelPoMapper;

    @Autowired
    private PieceFreightModelPoMapper pieceFreightModelPoMapper;

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
        FreightModelPoExample example=new FreightModelPoExample ();
        FreightModelPoExample.Criteria criteria=example.createCriteria();
        criteria.andShopIdEqualTo(shopId);
        criteria.andIdEqualTo(id);
        //根据id先找到对应的模板
        List<FreightModelPo> cloneFreightModelPoS=freightModelPoMapper.selectByExample(example);
        if(cloneFreightModelPoS.isEmpty()){
            //资源不存在
            retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE,"运费模板不属于该店铺");
            return retObj;
        }

        //获得模板
        FreightModelPo cloneFreightModelPo=cloneFreightModelPoS.get(0);
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

        cloneFreightModelPo.setDefaultModel((byte) 0);
        cloneFreightModelPo.setGmtCreate(LocalDateTime.now());
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
                retObj = new ReturnObject<>(ResponseCode.FREIGHTNAME_SAME, "运费模板名重复：" + cloneFreightModelPo.getName());
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
     * @param type 模板类型
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

                cloneWeightFreightModelPo.setGmtCreate(LocalDateTime.now());
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

                clonePieceFreightModelPo.setGmtCreate(LocalDateTime.now());
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
           returnObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE,"运费模板不属于该店铺");
        }else {
            returnObject=new ReturnObject<>();
            //删出对应的运费模板详细
            WeightFreightModelPoExample weightFreightModelPoExample=new WeightFreightModelPoExample ();
            WeightFreightModelPoExample.Criteria weightCriteria=weightFreightModelPoExample.createCriteria();
            weightCriteria.andFreightModelIdEqualTo(id);
            weightFreightModelPoMapper.deleteByExample(weightFreightModelPoExample);

            PieceFreightModelPoExample pieceFreightModelPoExample=new PieceFreightModelPoExample ();
            PieceFreightModelPoExample.Criteria pieceCriteria=pieceFreightModelPoExample.createCriteria();
            pieceCriteria.andFreightModelIdEqualTo(id);
            pieceFreightModelPoMapper.deleteByExample(pieceFreightModelPoExample);

        }

        return  returnObject;
    }

    

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

    /**
     * 增加一个店铺定义的运费模板
     *
     * @author 24320182203227 李子晗
     * @param freightModel 运费模板po
     * @return ReturnObject<FreightModelPo> 新增结果
     */
    public ReturnObject<FreightModel> insertFreightModel(FreightModel freightModel) {
        ReturnObject<FreightModel> retObj;
        //InternalLogger logger = null;
        try{
            FreightModelPo freightModelPo = freightModel.gotFreightModelPo();
            int ret = freightModelPoMapper.insertSelective(freightModelPo);
            if (ret == 0) {
                //插入失败
                logger.debug("insertFreightModel: insert freightModel fail " + freightModelPo.toString());
                retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("新增失败：" + freightModelPo.getName()));
            } else {
                //插入成功
                logger.debug("insertFreightModel: insert freightModel = " + freightModelPo.toString());
                //role.setId(rolePo.getId());
                retObj = new ReturnObject<>(freightModel);
            }
        }
        catch (DataAccessException e) {
//            if (Objects.requireNonNull(e.getMessage()).contains("auth_role.auth_role_name_uindex")) {
//                //若有重复的角色名则新增失败
//                logger.debug("updateFreightModel: have same freightModel name = " + freightModelPo.getName());
//                retObj = new ReturnObject<>(ResponseCode.ROLE_REGISTERED, String.format("模板名重复：" + freightModelPo.getName()));
//            } else {
//                // 其他数据库错误
//                logger.debug("other sql exception : " + e.getMessage());
//                retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
//            }
            // 其他数据库错误
            logger.debug("other sql exception : " + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
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


    /**
     * 管理员定义件数模板明细
     * @author 24320182203196 洪晓杰
     */
    public  ReturnObject<PieceFreightModel> insertPieceFreightModel(PieceFreightModel pieceFreightModel) {

        PieceFreightModelPo pieceFreightModelPo = pieceFreightModel.gotPieceFreightModelPo();

        ReturnObject<PieceFreightModel> retObj;

        try{
            int ret = pieceFreightModelPoMapper.insertSelective(pieceFreightModelPo);
            if (ret == 0) {
                //插入失败
                logger.debug("insertPieceFreightModel: insert pieceFreightModel fail " + pieceFreightModelPo.toString());
                retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("新增失败：" + pieceFreightModelPo.getId()));
            } else {
                //插入成功
                logger.debug("insertPieceFreightModel: insert pieceFreightModel = " + pieceFreightModelPo.toString());
                pieceFreightModel.setId(pieceFreightModelPo.getId());
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
        return retObj;


    }

    /**
     * 管理员定义重量模板明细
     * @author 24320182203196 洪晓杰
     */
    public  ReturnObject<WeightFreightModel> insertWeightFreightModel(WeightFreightModel weightFreightModel) {
        WeightFreightModelPo weightFreightModelPo = weightFreightModel.gotWeightFreightModelPo();
        ReturnObject<WeightFreightModel> retObj;
        try{
            int ret = weightFreightModelPoMapper.insertSelective(weightFreightModelPo);
            if (ret == 0) {
                //插入失败
                logger.debug("insertPieceFreightModel: insert weightFreightModel fail " + weightFreightModelPo.toString());
                retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("新增失败：" + weightFreightModelPo.getId()));
            } else {
                //插入成功
                logger.debug("insertPieceFreightModel: insert pieceFreightModel = " + weightFreightModelPo.toString());
                weightFreightModel.setId(weightFreightModelPo.getId());
                retObj = new ReturnObject<>(weightFreightModel);
            }
        }
        catch (DataAccessException e) {
            //其他数据库错误
            logger.debug("other sql exception : " + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
        return retObj;

    }

    /**
     * 店家或管理员为商铺定义默认运费模板
     * @author 24320182203196 洪晓杰
     */
    public ReturnObject<FreightModel> putDefaultPieceFreight(Long id, Long shopid){
        ReturnObject<FreightModel> retObj=null;
        try{
            String str;
            int ret ;
            //通过shopid查询
            FreightModelPoExample example = new FreightModelPoExample();
            FreightModelPoExample.Criteria criteria = example.createCriteria();
            criteria.andShopIdEqualTo(shopid);
            List<FreightModelPo> userProxyPos = freightModelPoMapper.selectByExample(example);
            if(userProxyPos.isEmpty()==true){
                retObj = new ReturnObject<>(ResponseCode.SHOP_ID_NOTEXIST, String.format("不存在对应的shopid" ));
                return retObj;
            }
            for(FreightModelPo userProxyPo : userProxyPos){
                str = userProxyPo.getDefaultModel();
                if(str.equals("true")&&userProxyPo.getId()==id){
                    //已存在对应的默认模板
                    logger.debug("updateFreightModel: update freightModel fail " + userProxyPo.toString() );
                    return new ReturnObject<>(ResponseCode.DEFAULTMODEL_EXISTED, String.format("已经存在对应的默认模板，新增失败" ));
                }
                else{
                    if(userProxyPo.getId()==id)
                    {//将对应id的模板设置为默认模板
                        userProxyPo.setDefaultModel("true");
                        userProxyPo.setGmtModified(LocalDateTime.now());
                        ret=freightModelPoMapper.updateByPrimaryKey(userProxyPo);
                        if (ret == 0) {
                            //更新失败
                            logger.debug("updateFreightModel: update freightModel fail " + userProxyPo.toString() );
                            retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("新增失败：" + userProxyPo.getName()));
                        } else {
                            //更新成功
                            logger.debug("updateFreightModel: update freightModel = " + userProxyPo.toString());
                            retObj = new ReturnObject<>(ResponseCode.OK,String.format("默认模板定义成功"));
                            for(FreightModelPo userAnotherProxyPo : userProxyPos){
                                if((userAnotherProxyPo.getDefaultModel()).equals("true")&&userAnotherProxyPo.getId()!=id){//将原商店的默认模板恢复为普通模板
                                    userAnotherProxyPo.setDefaultModel("false");
                                    userAnotherProxyPo.setGmtModified(LocalDateTime.now());
                                    ret=freightModelPoMapper.updateByPrimaryKeySelective(userAnotherProxyPo);
                                    if (ret == 0) {
                                        //更新失败
                                        logger.debug("updateFreightModel: update freightModel fail " + userAnotherProxyPo.toString() );
                                        retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("原默认模板新增失败：" + userAnotherProxyPo.getName()));
                                    } else {
                                        //更新成功
                                        logger.debug("updateFreightModel: update freightModel = " + userAnotherProxyPo.toString());
                                        retObj = new ReturnObject<>(ResponseCode.OK,String.format("原默认模板更新成功"));
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if(retObj==null)
                retObj = new ReturnObject<>(ResponseCode.MODEL_ID_NOTEXIST, String.format("shopid不存在对应的模板id，新增失败" ));
        }
        catch (DataAccessException e) {
            logger.debug("other sql exception : " + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
        return retObj;
    }


    /**
     * 查询某个重量运费模板明细
     * @author li mingming
     * @param shopId 店铺Id
     * @param id 重量运费模板明细id
     * @return ReturnObject
     */
    public ReturnObject<List> getWeightItemByFreightModelId(Long shopId, Long id) {
        WeightFreightModelPoExample example = new WeightFreightModelPoExample();
        WeightFreightModelPoExample.Criteria criteria = example.createCriteria();
        criteria.andFreightModelIdEqualTo(id);
        List<WeightFreightModelPo> weightFreightModelPos = weightFreightModelPoMapper.selectByExample(example);

        if (null == weightFreightModelPos || weightFreightModelPos.isEmpty()) {
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
        } else {
            List<WeightFreightModel> weightFreightModels = null;
            for (WeightFreightModelPo weightFreightModelPo:weightFreightModelPos)
            {
                WeightFreightModel weightFreightModel = new WeightFreightModel(weightFreightModelPo);
                weightFreightModels.add(weightFreightModel);
            }
            return new ReturnObject<>(weightFreightModels);
        }
    }

    /**
     * 查询某个件数运费模板明细
     * @author li mingming
     * @param shopId 店铺Id
     * @param id 件数运费模板明细id
     * @return ReturnObject
     */
    public ReturnObject<List> getPieceItemByFreightModelId(Long shopId, Long id) {
        PieceFreightModelPoExample example = new PieceFreightModelPoExample();
        PieceFreightModelPoExample.Criteria criteria = example.createCriteria();
        criteria.andFreightModelIdEqualTo(id);
        List<PieceFreightModelPo> pieceFreightModelPos = pieceFreightModelPoMapper.selectByExample(example);

        if (null == pieceFreightModelPos || pieceFreightModelPos.isEmpty()) {
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
        } else {
            List<PieceFreightModel> pieceFreightModels = null;
            for (PieceFreightModelPo pieceFreightModelPo:pieceFreightModelPos)
            {
                PieceFreightModel pieceFreightModel = new PieceFreightModel(pieceFreightModelPo);
                pieceFreightModels.add(pieceFreightModel);
            }
            return new ReturnObject<>(pieceFreightModels);
        }
    }

    /**
     * 删除某个重量运费模板明细
     * @author li mingming
     * @param id 重量运费模板明细id
     * @return ReturnObject
     */
    public ReturnObject<VoObject> delWeightItemById(Long shopId, Long id)
    {
        ReturnObject<VoObject> returnObject;
        int state = weightFreightModelPoMapper.deleteByPrimaryKey(id);
        if (state == 0) {
            logger.info("模板明细不存在或已被删除：id = " + id);
            returnObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        } else {
            logger.info("模板明细 id = " + id + " 已被永久删除");
            returnObject = new ReturnObject<>();
        }
        return returnObject;
    }

    /**
     * 删除某个件数运费模板明细
     * @author li mingming
     * @param id 件数运费模板明细id
     * @return ReturnObject
     */
    public ReturnObject<VoObject> delPieceItemById(Long shopId, Long id)
    {
        ReturnObject<VoObject> returnObject;
        int state = pieceFreightModelPoMapper.deleteByPrimaryKey(id);
        if (state == 0) {
            logger.info("模板明细不存在或已被删除：id = " + id);
            returnObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        } else {
            logger.info("模板明细 id = " + id + " 已被永久删除");
            returnObject = new ReturnObject<>();
        }
        return returnObject;
    }







}
