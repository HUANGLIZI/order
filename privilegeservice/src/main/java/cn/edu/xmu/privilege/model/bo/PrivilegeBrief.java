package cn.edu.xmu.privilege.model.bo;

import cn.edu.xmu.privilege.model.po.PrivilegePo;
import lombok.Data;

/**
 * 权限对象
 * @author Ming Qiu
 **/
@Data
public class PrivilegeBrief {

    private Long id;

    private  Integer bitIndex;
    /**
     * 用PO对象构造BO对象
     * @param po
     */
    public PrivilegeBrief(PrivilegePo po) {
        this.id = po.getId();
        this.bitIndex = po.getBitIndex().intValue();
    }
}
