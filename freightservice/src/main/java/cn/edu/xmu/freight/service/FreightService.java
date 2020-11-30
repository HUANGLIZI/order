package cn.edu.xmu.freight.service;

import cn.edu.xmu.freight.dao.FreightDao;
import cn.edu.xmu.freight.model.bo.FreightModel;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户服务
 * @author
 * Modified at 2020/11/5 10:39
 **/
@Service
public class FreightService {

    @Autowired
    FreightDao freightDao;

    private Logger logger = LoggerFactory.getLogger(FreightService.class);


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
     * @param shopId 店铺rid
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
}
