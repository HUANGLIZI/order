package cn.edu.xmu.freight.dao;

import cn.edu.xmu.freight.mapper.PieceFreightModelPoMapper;
import cn.edu.xmu.freight.model.vo.FreightModelRetVo;
import cn.edu.xmu.freight.model.vo.FreightModelReturnVo;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

import java.awt.datatransfer.Clipboard;
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


        try {
            //不加限定条件查询所有
            //分页查询

            logger.debug("page = " + page + "pageSize = " + pageSize);
            PageHelper.startPage(page, pageSize);

            List<FreightModelPo> freightModelPos = freightModelPoMapper.selectByExample(example);
            List<VoObject> ret = new ArrayList<>(freightModelPos.size());
            for (FreightModelPo po : freightModelPos) {
                FreightModelReturnVo freightModelReturnVo = new FreightModelReturnVo(po);
                ret.add(freightModelReturnVo);
            }

            PageInfo<FreightModelPo> freightModelPoPageInfo = PageInfo.of(freightModelPos);
            PageInfo<VoObject> freightModelPage = new PageInfo<>(ret);
            freightModelPage.setPages(freightModelPoPageInfo.getPages());
            freightModelPage.setPageNum(freightModelPoPageInfo.getPageNum());
            freightModelPage.setPageSize(freightModelPoPageInfo.getPageSize());
            freightModelPage.setTotal(freightModelPoPageInfo.getTotal());
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
    public ReturnObject<FreightModelPo> getFreightModelById(Long id) {
        FreightModelPo freightModelPo = freightModelPoMapper.selectByPrimaryKey(id);
        //po对象为空，没查到
        if (freightModelPo == null) {
            logger.error("getFreightModelById: 数据库不存在该运费模板 freightmodel_id=" + id);
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
        }

        return new ReturnObject<>(freightModelPo);
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

        ReturnObject<FreightModelReturnVo> retObj;

        //获得模板
        FreightModelPo cloneFreightModelPo=freightModelPoMapper.selectByPrimaryKey(id);
        if(cloneFreightModelPo==null){
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        if(!(cloneFreightModelPo.getShopId().equals(shopId))){
            return new ReturnObject(ResponseCode.RESOURCE_ID_OUTSCOPE);
        }
        if(cloneFreightModelPo.getShopId().toString().equals("null")){
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
        }
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
                retObj = new ReturnObject<>(new FreightModelReturnVo(freightModelPoMapper.selectByPrimaryKey(cloneFreightModelPo.getId())));
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
    public ReturnObject delShopFreightModel(Long shopId, Long id) {
        ReturnObject<Object> returnObject;
        //获得模板
        FreightModelPo po=freightModelPoMapper.selectByPrimaryKey(id);
        if(po==null){
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        if(!(po.getShopId().equals(shopId))){
            return new ReturnObject(ResponseCode.RESOURCE_ID_OUTSCOPE);
        }
        if(po.getShopId().toString().equals("null")){
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
        }

        int ret = freightModelPoMapper.deleteByPrimaryKey(id);
        if(ret==0){
           //资源不存在
           returnObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST,"操作的资源id不存在");
        }else {
            returnObject=new ReturnObject<>(ResponseCode.OK);
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



    /**
     * @param
     * @return
     * @author Cai Xinlu
     * @date 2020-12-12 17:45
     */
    public ReturnObject<ResponseCode> changeFreightModel(FreightModelChangeBo freightModelChangeBo) {
        //获得模板
        FreightModelPo freightModelPo=freightModelPoMapper.selectByPrimaryKey(freightModelChangeBo.getId());
        if(freightModelPo==null){
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        if(!(freightModelPo.getShopId().equals(freightModelChangeBo.getShopId()))){
            return new ReturnObject(ResponseCode.RESOURCE_ID_OUTSCOPE);
        }
        if(freightModelPo.getShopId().toString().equals("null")){
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
        }

        String name = freightModelChangeBo.getName();
        if (name != null)
        {
            FreightModelPoExample freightModelPoExample = new FreightModelPoExample();
            FreightModelPoExample.Criteria criteria = freightModelPoExample.createCriteria();
            criteria.andNameEqualTo(name);
            List<FreightModelPo> freightModelPoList = freightModelPoMapper.selectByExample(freightModelPoExample);
            if (freightModelPoList.size() > 0) {
                logger.info(freightModelPoList.get(0).toString());
                return new ReturnObject<>(ResponseCode.FREIGHTNAME_SAME);
            }
        }

        freightModelPo.setUnit(freightModelChangeBo.getUnit());
        freightModelPo.setName(freightModelChangeBo.getName());

        int ret = freightModelMapper.updateFreightModel(freightModelPo);
        if (ret == 0) {
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
        } else {
            return new ReturnObject<>();
        }
    }

    /**
     * 增加一个店铺定义的运费模板
     *
     * @author 24320182203227 李子晗
     * @param freightModel 运费模板po
     * @return ReturnObject<FreightModelPo> 新增结果
     */
    public ReturnObject<FreightModelReturnVo> insertFreightModel(FreightModel freightModel) {
        ReturnObject<FreightModelReturnVo> retObj;
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
                FreightModelReturnVo freightModel1=new FreightModelReturnVo(freightModelPoMapper.selectByPrimaryKey(freightModelPo.getId()));
                retObj = new ReturnObject<>(freightModel1);
            }
        }
        catch (DataAccessException e) {
            if (Objects.requireNonNull(e.getMessage()).contains("name")) {
                //若有重复的角色名则新增失败
                //logger.debug("updateFreightModel: have same freightModel name = " + freightModelPo.getName());
                retObj = new ReturnObject<>(ResponseCode.FREIGHTNAME_SAME);
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
     * 修改重量运费模板
     * @author Cai Xinlu
     * @date 2020-12-10 9:40
     */
    public ReturnObject<ResponseCode> changeWeightFreightModel(WeightFreightModelChangeBo weightFreightModelChangeBo,
                                                               Long shopId) {
        ReturnObject<ResponseCode> retObj = null;

        WeightFreightModelPo weightFreightModelPo = weightFreightModelChangeBo.gotWeightFreightModelPo();
        WeightFreightModelPo retWeightFreightModel = weightFreightModelPoMapper.selectByPrimaryKey(weightFreightModelChangeBo.getId());
        if (retWeightFreightModel == null)
        {
            retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
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
            retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
            return retObj;
        }


        int ret = freightModelMapper.updateWeightFreightModel(weightFreightModelPo);
        if (ret == 0) {
            retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        } else {
            retObj = new ReturnObject<>();
        }
        return retObj;
    }

    /**
     * 修改件数运费模板
     * @author Cai Xinlu
     * @date 2020-12-10 9:40
     */
    public ReturnObject<ResponseCode> changePieceFreightModel(PieceFreightModelChangeBo pieceFreightModelChangeBo,
                                                              Long shopId) {
        ReturnObject<ResponseCode> retObj = null;

        PieceFreightModelPo pieceFreightModelPo = pieceFreightModelChangeBo.gotPieceFreightModelPo();
        PieceFreightModelPo retPieceFreightModel = pieceFreightModelPoMapper.selectByPrimaryKey(pieceFreightModelChangeBo.getId());
        // 判断是否存在此运费模板明细，即数据库是否有此id
        if (retPieceFreightModel == null)
        {
            retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
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
            retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
            return retObj;
        }

        int ret = freightModelMapper.updatePieceFreightModel(pieceFreightModelPo);
        if (ret == 0) {
            retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
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

        //logger.error("2222222");

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
            //通过蓝的第一个测试
            if(freightModelPoMapper.selectByPrimaryKey(id)==null)
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);

            Byte str;
            int ret ;
            //通过shopid查询

            FreightModelPoExample example = new FreightModelPoExample();
            FreightModelPoExample.Criteria criteria = example.createCriteria();
            criteria.andShopIdEqualTo(shopid);
            List<FreightModelPo> userProxyPos = freightModelPoMapper.selectByExample(example);

            //logger.error("1");
            if(userProxyPos.isEmpty()==true){
                //httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
                retObj = new ReturnObject<>(ResponseCode.SHOP_ID_NOTEXIST, String.format("不存在对应的shopid" ));
                return retObj;
            }
            //logger.error("2");

            for(FreightModelPo userProxyPo : userProxyPos){

                //logger.error("1");

                str = userProxyPo.getDefaultModel();
                if(str==1&&userProxyPo.getId().equals(id)){
                    //已存在对应的默认模板
                    logger.debug("updateFreightModel: update freightModel fail " + userProxyPo.toString() );
                    return new ReturnObject<>(ResponseCode.DEFAULTMODEL_EXISTED, String.format("已经存在对应的默认模板，新增失败" ));
                }
                else{
                    if(userProxyPo.getId().equals(id))
                    {//将对应id的模板设置为默认模板
                        userProxyPo.setDefaultModel((byte) 1);
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
                                if(userAnotherProxyPo.getDefaultModel()==1&&!(userAnotherProxyPo.getId().equals(id))){//将原商店的默认模板恢复为普通模板
                                    userAnotherProxyPo.setDefaultModel((byte) 0);
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
                retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE, String.format("shopid不存在对应的模板id，新增失败" ));
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
     * @date 2020/12/12
     */
    public ReturnObject<List> getWeightItemByFreightModelId(Long shopId, Long id)
    {
        FreightModelPo freightModelPo = freightModelPoMapper.selectByPrimaryKey(id);
        if(shopId != freightModelPo.getShopId())
        {
            return new ReturnObject(ResponseCode.FIELD_NOTVALID, String.format("店铺id不匹配：" + shopId));
        }
        WeightFreightModelPoExample example = new WeightFreightModelPoExample();
        WeightFreightModelPoExample.Criteria criteria = example.createCriteria();
        criteria.andFreightModelIdEqualTo(id);
        List<WeightFreightModelPo> weightFreightModelPos = weightFreightModelPoMapper.selectByExample(example);

        if (null == weightFreightModelPos || weightFreightModelPos.isEmpty())
        {
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        else
        {
            List<WeightFreightModel> weightFreightModels = new ArrayList<>();
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
     * @date 2020/12/12
     */
    public ReturnObject<List> getPieceItemByFreightModelId(Long shopId, Long id) {
        FreightModelPo freightModelPo = freightModelPoMapper.selectByPrimaryKey(id);
        if(shopId != freightModelPo.getShopId())
        {
            return new ReturnObject(ResponseCode.FIELD_NOTVALID, String.format("店铺id不匹配：" + shopId));
        }
        PieceFreightModelPoExample example = new PieceFreightModelPoExample();
        PieceFreightModelPoExample.Criteria criteria = example.createCriteria();
        criteria.andFreightModelIdEqualTo(id);
        List<PieceFreightModelPo> pieceFreightModelPos = pieceFreightModelPoMapper.selectByExample(example);

        if (null == pieceFreightModelPos || pieceFreightModelPos.isEmpty()) {
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
        } else {
            List<PieceFreightModel> pieceFreightModels = new ArrayList<>();
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
     * @date 2020/12/12
     */
    public ReturnObject<VoObject> delWeightItemById(Long shopId, Long id)
    {
        ReturnObject<VoObject> returnObject;
        WeightFreightModelPo weightFreightModelPo = weightFreightModelPoMapper.selectByPrimaryKey(id);
        if(weightFreightModelPo == null)
        {
            logger.info("模板明细不存在或已被删除：id = " + id);
            returnObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        else
        {
            FreightModelPo freightModelPo = freightModelPoMapper.selectByPrimaryKey(weightFreightModelPo.getFreightModelId());
            if(freightModelPo.getShopId() != shopId)
            {
                return new ReturnObject(ResponseCode.RESOURCE_ID_OUTSCOPE, String.format("店铺id不匹配：" + shopId));
            }
            int state = weightFreightModelPoMapper.deleteByPrimaryKey(id);
            if (state == 0) {
                logger.info("模板明细不存在或已被删除：id = " + id);
                returnObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            } else {
                logger.info("模板明细 id = " + id + " 已被永久删除");
                returnObject = new ReturnObject<>();
            }
        }
        return returnObject;
    }

    /**
     * 删除某个件数运费模板明细
     * @author li mingming
     * @param id 件数运费模板明细id
     * @return ReturnObject
     * @date 2020/12/12
     */
    public ReturnObject delPieceItemById(Long shopId, Long id)
    {
        ReturnObject<VoObject> returnObject;
        PieceFreightModelPo pieceFreightModelPo = pieceFreightModelPoMapper.selectByPrimaryKey(id);
        if(pieceFreightModelPo == null)
        {
            logger.info("模板明细不存在或已被删除：id = " + id);
            returnObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        else
        {
            FreightModelPo freightModelPo = freightModelPoMapper.selectByPrimaryKey(pieceFreightModelPo.getFreightModelId());
            if(freightModelPo.getShopId() != shopId)
            {
                return new ReturnObject(ResponseCode.RESOURCE_ID_OUTSCOPE, String.format("店铺id不匹配：" + shopId));
            }
            int state = pieceFreightModelPoMapper.deleteByPrimaryKey(id);
            if (state == 0) {
                logger.info("模板明细不存在或已被删除：id = " + id);
                returnObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            } else {
                logger.info("模板明细 id = " + id + " 已被永久删除");
                returnObject = new ReturnObject<>();
            }
        }
        return returnObject;
    }
    /**
     * 查询某个件数运费模板明细
     * @author 李子晗
     * @param shopId,RegionId 店铺Id,地区Id
     * @param id 件数运费模板明细id
     * @return ReturnObject
     */
    public ReturnObject<PieceFreightModel> getPieceItemByFreightModelIdRegionId(Long shopId, Long id,Long RegionId) {
        PieceFreightModelPoExample example = new PieceFreightModelPoExample();
        PieceFreightModelPoExample.Criteria criteria = example.createCriteria();
        criteria.andFreightModelIdEqualTo(id);
        criteria.andRegionIdEqualTo(RegionId);
        List<PieceFreightModelPo> pieceFreightModelPos = pieceFreightModelPoMapper.selectByExample(example);
        if(pieceFreightModelPos.size()==0)
            return new ReturnObject(ResponseCode.FREIGHTMODEL_SHOP_NOTFIT);
        PieceFreightModel pieceFreightModel=new PieceFreightModel(pieceFreightModelPos.get(0));
        return new ReturnObject<>(pieceFreightModel);
    }
    /**
     * 查询某个件数运费模板明细
     * @author 李子晗
     * @param shopId,RegionId 店铺Id,地区Id
     * @param id 件数运费模板明细id
     * @return ReturnObject
     */
    public ReturnObject<WeightFreightModel> getWeightItemByFreightModelIdRegionId(Long shopId, Long id,Long RegionId) {
        WeightFreightModelPoExample example = new WeightFreightModelPoExample();
        WeightFreightModelPoExample.Criteria criteria = example.createCriteria();
        criteria.andFreightModelIdEqualTo(id);
        criteria.andRegionIdEqualTo(RegionId);
        List<WeightFreightModelPo> weightFreightModelPos = weightFreightModelPoMapper.selectByExample(example);
        if(weightFreightModelPos.size()==0)
            return new ReturnObject(ResponseCode.FREIGHTMODEL_SHOP_NOTFIT);
        WeightFreightModel weightFreightModel=new WeightFreightModel(weightFreightModelPos.get(0));
        return new ReturnObject<>(weightFreightModel);
    }

    /**
     * 通过shopId获得该店铺默认运费模板
     *
     *
     * @author 24320182203227 李子晗
     * @param shopId 运费模板id
     * @return ReturnObject运费模板查询结果
     * createdBy 李子晗 2020/12/9 20:12
     * modifiedBy 李子晗 2020/12/9 20:12
     */
    public FreightModelPo getDefaultFreightModelByshopId(Long shopId) {
        FreightModelPoExample freightModelPoExample = new FreightModelPoExample();
        FreightModelPoExample.Criteria criteria = freightModelPoExample.createCriteria();
        criteria.andShopIdEqualTo(shopId);
        criteria.andDefaultModelEqualTo((byte) 1);
        List<FreightModelPo> freightModelPo= freightModelPoMapper.selectByExample(freightModelPoExample);
        //po对象为空，没查到
        if (freightModelPo.size() == 0) {
            logger.error("getFreightModelById: 数据库不存在该默认运费模板 freightmodel_id=");
            return null;
        }
        return freightModelPo.get(0);
    }


}
