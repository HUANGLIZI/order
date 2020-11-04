package cn.edu.xmu.privilege.model.bo;

import cn.edu.xmu.ooad.util.AES;
import cn.edu.xmu.ooad.util.SHA256;
import cn.edu.xmu.ooad.util.StringUtil;
import cn.edu.xmu.privilege.model.po.UserPo;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 后台用户
 *
 * @author Ming Qiu
 * @date Created in 2020/11/3 20:10
 **/
@Data
public class User {

    public static String AESPASS = "OOAD2020-11-01";

    /**
     * 后台用户状态
     */
    public enum State {
        NEW(0, "新注册"),
        NORM(1, "正常"),
        FORBID(2, "封禁"),
        DELETE(3, "废弃");

        private static final Map<Integer, User.State> stateMap;

        static { //由类加载机制，静态块初始加载对应的枚举属性到map中，而不用每次取属性时，遍历一次所有枚举值
            stateMap = new HashMap();
            for (User.State enum1 : values()) {
                stateMap.put(enum1.code, enum1);
            }
        }

        private int code;
        private String description;

        State(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public static User.State getTypeByCode(Integer code) {
            return stateMap.get(code);
        }

        public Integer getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }
    }

    private Long id;

    private String userName;

    private String password;

    private String mobile;

    private Boolean mobileVerified = false;

    private String email;

    private Boolean emailVerified = false;

    private String name;

    private String avatar;

    private LocalDateTime lastLoginTime;

    private String lastLoginIp;

    private String openId;

    private State state = State.NEW;

    private Long departId;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    private Long creatorId;

    private String signature;

    private String cacuSignature;

    /**
     * 构造函数
     * @param po Po对象
     */
    public User(UserPo po){
        this.id = po.getId();
        this.userName = po.getUserName();
        this.password = po.getPassword();
        this.mobile = AES.decrypt(po.getMobile(),AESPASS);
        if (null != po.getMobileVerified()) {
            this.mobileVerified = po.getMobileVerified() == 1;
        }
        this.email = AES.decrypt(po.getEmail(),AESPASS);

        if (null != po.getEmailVerified()) {
            this.emailVerified = po.getEmailVerified() == 1;
        }
        this.name = AES.decrypt(po.getName(),AESPASS);
        this.avatar = po.getAvatar();
        this.lastLoginTime = po.getLastLoginTime();
        this.lastLoginIp = po.getLastLoginIp();
        this.openId = po.getOpenId();
        if (null != po.getState()) {
            this.state = State.getTypeByCode(po.getState().intValue());
        }
        this.departId = po.getDepartId();
        this.creatorId = po.getCreatorId();
        this.gmtCreate = po.getGmtCreate();
        this.gmtModified = po.getGmtModified();
        this.signature = po.getSignature();

        StringBuilder signature = StringUtil.concatString("-", po.getUserName(), po.getPassword(),
                po.getMobile(),po.getEmail(),po.getOpenId(),po.getState().toString(),po.getDepartId().toString(),
                po.getCreatorId().toString());
        this.cacuSignature = SHA256.getSHA256(signature.toString());
    }

    /**
     * 对象未篡改
     * @return
     */
    public Boolean authetic() {
        return this.cacuSignature.equals(this.signature);
    }
}
