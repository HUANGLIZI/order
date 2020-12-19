package cn.edu.xmu.freight.model.vo;

import cn.edu.xmu.freight.model.po.FreightModelPo;
import cn.edu.xmu.ooad.model.VoObject;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 *@auther zxj
 */
@Data
public class FreightModelReturnVo implements VoObject {
    @ApiModelProperty(value = "模板id")
    private Long id;

    @ApiModelProperty(value = "名字")
    private String name;

    @ApiModelProperty(value = "类型")
    private Byte type;

    @ApiModelProperty(value = "")
    private Integer unit;

    @ApiModelProperty(value = "默认模板")
    private Boolean defaultModel=false;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime gmtCreate;

    @ApiModelProperty(value = "修改时间")
    private LocalDateTime gmtModified;

    /**
     * 用FreightModelPo对象建立Vo对象
     *
     * @author 24320182203327 zxj
     */
    public FreightModelReturnVo(FreightModelPo freightModelPo) {
        this.id = freightModelPo.getId();
        this.name = freightModelPo.getName();
        this.type = freightModelPo.getType();
        this.unit= freightModelPo.getUnit();
        this.gmtCreate = freightModelPo.getGmtCreate();
        this.gmtModified = freightModelPo.getGmtModified();
        if(freightModelPo.getDefaultModel()==1){
            this.defaultModel=true;
        }
    }

    @Override
    public Object createVo() {
        return this;
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }

    public FreightModelPo createPo() {
        FreightModelPo freightModelPo=new FreightModelPo();
        freightModelPo.setId(this.id);
        freightModelPo.setName(this.name);
        freightModelPo.setType(this.type);
        freightModelPo.setUnit(this.unit);
        freightModelPo.setGmtCreate(this.gmtCreate);
        freightModelPo.setGmtModified(this.gmtModified);
        Byte a=1;
        if(this.defaultModel=true){
            freightModelPo.setDefaultModel(a);
        }
        return  freightModelPo;
    }
}
