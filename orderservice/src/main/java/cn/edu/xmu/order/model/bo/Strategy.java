package cn.edu.xmu.order.model.bo;


import lombok.Data;

import java.util.List;

/**
 * 团购规则
 * @author 李明明
 * @date 2020-12-14
 */
@Data
public class Strategy
{
    private List<level> data;


    public static class level{
        private Integer num;
        private Long price;

        public level(){}

        public level(Integer num, Long price)
        {
            this.num = num;
            this.price = price;
        }

        public Integer getNum() {
            return num;
        }

        public Long getPrice() {
            return price;
        }

        public void setNum(Integer num) {
            this.num = num;
        }

        public void setPrice(Long price) {
            this.price = price;
        }
    }
}

