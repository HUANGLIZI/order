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
import cn.edu.xmu.freight.mapper.FreightModelPoMapper;
import cn.edu.xmu.freight.mapper.PieceFreightModelPoMapper;
import cn.edu.xmu.freight.mapper.WeightFreightModelPoMapper;
import cn.edu.xmu.freight.model.bo.FreightModel;
import cn.edu.xmu.freight.model.bo.PieceFreightModel;
import cn.edu.xmu.freight.model.bo.WeightFreightModel;
import cn.edu.xmu.freight.model.po.FreightModelPo;
import cn.edu.xmu.freight.model.po.PieceFreightModelPo;
import cn.edu.xmu.freight.model.po.WeightFreightModelPo;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author Caixin
 * @date 2020-11-26 14:01
 */
import java.time.LocalDateTime;

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
    private static final Logger logger = LoggerFactory.getLogger(FreightDao.class);

    @Autowired(required = false)
    private  FreightModelPoMapper freightModelPoMapper;

    @Autowired(required = false)
    private  PieceFreightModelPoMapper pieceFreightModelPoMapper;

    @Autowired(required = false)
    private  WeightFreightModelPoMapper weightFreightModelPoMapper;


    public FreightDao() {

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






    public  ReturnObject<PieceFreightModel> insertPieceFreightModel(PieceFreightModel pieceFreightModel) {

        PieceFreightModelPo pieceFreightModelPoPo = pieceFreightModel.gotPieceFreightModelPo();
        ReturnObject<PieceFreightModel> retObj;
        try{
            int ret = pieceFreightModelPoMapper.insertSelective(pieceFreightModelPoPo);
            if (ret == 0) {
                //插入失败
                logger.debug("insertPieceFreightModel: insert pieceFreightModel fail " + pieceFreightModelPoPo.toString());
                retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("新增失败：" + pieceFreightModelPoPo.getId()));
            } else {
                //插入成功
                logger.debug("insertPieceFreightModel: insert pieceFreightModel = " + pieceFreightModelPoPo.toString());
                pieceFreightModel.setId(pieceFreightModelPoPo.getId());
                retObj = new ReturnObject<>(pieceFreightModel);
            }
        }
        catch (DataAccessException e) {
//            if (Objects.requireNonNull(e.getMessage()).contains("auth_role.auth_role_name_uindex")) {
//                //若有重复的角色名则新增失败
//                logger.debug("updateRole: have same role name = " + pieceFreightModelPoPo.getName());
//                retObj = new ReturnObject<>(ResponseCode.ROLE_REGISTERED, String.format("角色名重复：" + pieceFreightModelPoPo.getName()));
//            } else {
//                // 其他数据库错误
//                logger.debug("other sql exception : " + e.getMessage());
//                retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
//            }

            //                // 其他数据库错误
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
//            if (Objects.requireNonNull(e.getMessage()).contains("auth_role.auth_role_name_uindex")) {
//                //若有重复的角色名则新增失败
//                logger.debug("updateRole: have same role name = " + pieceFreightModelPoPo.getName());
//                retObj = new ReturnObject<>(ResponseCode.ROLE_REGISTERED, String.format("角色名重复：" + pieceFreightModelPoPo.getName()));
//            } else {
//                // 其他数据库错误
//                logger.debug("other sql exception : " + e.getMessage());
//                retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
//            }

            //                // 其他数据库错误
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


    public boolean putDefaultPieceFreight(Long id, Long shopid) {
        //getPrivIdsByRoleId已经进行role的签名校验

        FreightModelPo freightModelPo=freightModelPoMapper.selectByPrimaryKey(id);

        freightModelPo.setShopId(shopid);
        freightModelPo.setDefaultModel("默认运费模板");

        if(freightModelPoMapper.insert(freightModelPo)==1)
            return true;

        return false;
    }


    public ReturnObject<FreightModel> putDefaultPieceFreight1(Long id, Long shopid){
        FreightModel freightModel = new FreightModel();
        ReturnObject<FreightModel> retObj = null;

        FreightModelPo freightModelPo=freightModelPoMapper.selectByPrimaryKey(id);
        freightModelPo.setShopId(shopid);
        freightModelPo.setDefaultModel("默认运费模板");
        freightModelPo.setGmtCreated(LocalDateTime.now());
        freightModelPo.setGmtModified(LocalDateTime.now());

        try{
            int ret = freightModelPoMapper.insertSelective(freightModelPo);
            if (ret == 0) {
                //插入失败
                logger.debug("insertFreightModel: insert freightModel fail " + freightModelPo.toString() );
                retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("新增失败：" + freightModelPo.getName()));
            } else {
                //插入成功
                logger.debug("insertFreightModel: insert freightModel = " + freightModelPo.toString());
                freightModel.setShopId(shopid);//??????????????????
                freightModel.setId(freightModelPo.getId());
                retObj = new ReturnObject<>(freightModel);
            }
        }


        catch (DataAccessException e) {
//            if (Objects.requireNonNull(e.getMessage()).contains("auth_role.auth_role_name_uindex")) {
//                //若有模板的角色名则新增失败
//                logger.debug("updateRole: have same role name = " + freightModelPo.getName());
//                retObj = new ReturnObject<>(ResponseCode.FREIGHTNAME_SAME, String.format("模板名重复：" + freightModelPo.getName()));
//            } else {
//                // 其他数据库错误
//                logger.debug("other sql exception : " + e.getMessage());
//                retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
//            }
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


//    /**
//     * 增加一个角色
//     *
//     * @author 24320182203281 王纬策
//     * @param role 角色bo
//     * @return ReturnObject<Role> 新增结果
//     * createdBy 王纬策 2020/11/04 13:57
//     * modifiedBy 王纬策 2020/11/7 19:20
//     */
//    public ReturnObject<FreightModel> insertRole(FreightModel role) {
//        FreightModelPo rolePo = role.gotRolePo();
//        ReturnObject<Role> retObj = null;
//        try{
//            int ret = roleMapper.insertSelective(rolePo);
//            if (ret == 0) {
//                //插入失败
//                logger.debug("insertRole: insert role fail " + rolePo.toString());
//                retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("新增失败：" + rolePo.getName()));
//            } else {
//                //插入成功
//                logger.debug("insertRole: insert role = " + rolePo.toString());
//                role.setId(rolePo.getId());
//                retObj = new ReturnObject<>(role);
//            }
//        }
//        catch (DataAccessException e) {
//            if (Objects.requireNonNull(e.getMessage()).contains("auth_role.auth_role_name_uindex")) {
//                //若有重复的角色名则新增失败
//                logger.debug("updateRole: have same role name = " + rolePo.getName());
//                retObj = new ReturnObject<>(ResponseCode.ROLE_REGISTERED, String.format("角色名重复：" + rolePo.getName()));
//            } else {
//                // 其他数据库错误
//                logger.debug("other sql exception : " + e.getMessage());
//                retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
//            }
//        }
//        catch (Exception e) {
//            // 其他Exception错误
//            logger.error("other exception : " + e.getMessage());
//            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
//        }
//        return retObj;
//    }



}
