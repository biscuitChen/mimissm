package cn.fafu.service.impl;

import cn.fafu.mapper.AdminMapper;
import cn.fafu.pojo.Admin;
import cn.fafu.pojo.AdminExample;
import cn.fafu.service.AdminService;
import cn.fafu.utils.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    //在业务逻辑层，一定会有数据访问层的对象
    @Autowired
    AdminMapper adminMapper;

    @Override
    public Admin login(String name, String pwd) {

        //根据传入的用户到DB中查询相对应的用户对象
        //有条件一定要创建AdminExample对象，用来封装条件
        AdminExample adminExample = new AdminExample();
        adminExample.createCriteria().andANameEqualTo(name);

        List<Admin> admins = adminMapper.selectByExample(adminExample);
        if(admins.size() > 0) {
            Admin admin = admins.get(0);
            //如果查询到用户对象，进行密码比对，注意密码是密文
            String pwdMD5 = MD5Util.getMD5(pwd);
            if (pwdMD5.equals(admin.getaPass())){
                return admin;
            }
        }

        return null;
    }
}
