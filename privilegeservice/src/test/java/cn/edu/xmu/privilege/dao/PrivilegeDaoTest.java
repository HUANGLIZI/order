package cn.edu.xmu.privilege.dao;

import cn.edu.xmu.privilege.PrivilegeServiceApplication;
import cn.edu.xmu.privilege.model.bo.Privilege;
import cn.edu.xmu.privilege.model.bo.PrivilegeBrief;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Ming Qiu
 **/
@SpringBootTest(classes = PrivilegeServiceApplication.class)   //标识本类是一个SpringBootTest
@Transactional
public class PrivilegeDaoTest {

    @Autowired
    private PrivilegeDao privilegeDao;

    @Test
    public void getPrivilegeBriefById(){
        PrivilegeBrief p1 = privilegeDao.getPrivilegeBriefById((long) 2);
        assertEquals(2, p1.getId());
        assertEquals(2, p1.getBitIndex());

        p1 = privilegeDao.getPrivilegeBriefById((long) 13);
        assertEquals(13, p1.getId());
        assertEquals(13, p1.getBitIndex());
    }

    @Test
    public void getPrivilegeBriefKey(){
        PrivilegeBrief p1 = privilegeDao.getPrivilegeBriefByKey("/adminusers/{id}", Privilege.RequestType.GET);
        assertEquals(2, p1.getId());
        assertEquals(2, p1.getBitIndex());

        p1 = privilegeDao.getPrivilegeBriefByKey("/adminusers/{id}", Privilege.RequestType.DELETE);
        assertEquals(4, p1.getId());
        assertEquals(4, p1.getBitIndex());
    }

}
