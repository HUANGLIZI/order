package cn.edu.xmu.freight.mapper;

import cn.edu.xmu.freight.model.po.FreightModelPo;
import cn.edu.xmu.freight.model.po.PieceFreightModelPo;
import cn.edu.xmu.freight.model.po.WeightFreightModelPo;

import java.util.List;

/**
 * @author Caixin
 * @date 2020-11-26 19:10
 */

public interface FreightModelMapper {

    int updateFreightModel(FreightModelPo freightModelPo);

    int updateWeightFreightModel(WeightFreightModelPo weightFreightModelPo);

    int updatePieceFreightModel(PieceFreightModelPo pieceFreightModelPo);
}
