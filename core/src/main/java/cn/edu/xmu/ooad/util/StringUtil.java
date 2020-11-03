package cn.edu.xmu.ooad.util;

/**
 * 用于模型对象的工具类
 * @author Ming Qiu
 * @date Created in 2020/11/3 13:30
 **/
public class StringUtil {

    /**
     * 动态拼接字符串
     * @param sep 分隔符
     * @param fields 拼接的字符串
     * @return StringBuilder
     * createdBy: Ming Qiu 2020-11-02 11:44
     */
    public static StringBuilder concatString(String sep, String... fields){
        StringBuilder ret = new StringBuilder();

        for (int i = 0; i< fields.length; i++){
            if (i > 0){
                ret.append(sep);
            }
            ret.append(fields[i]);
        }
        return ret;
    }
}
