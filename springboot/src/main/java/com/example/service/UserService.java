package com.example.service;

import cn.hutool.core.util.ObjectUtil;
import com.example.common.Constants;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Admin;
import com.example.entity.Certification;
import com.example.entity.User;
import com.example.entity.Account;
import com.example.exception.CustomException;
import com.example.mapper.UserMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sun.org.apache.bcel.internal.generic.RETURN;
import org.springframework.stereotype.Service;
import org.springframework.beans.BeanUtils;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import com.example.utils.TokenUtils;
/**
 * 用户业务处理
 **/
@Service
public class UserService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private CertificationService certificationService;

    public void add(User user) {
        User dbUser = this.selectByUsername(user.getUsername());
        if(ObjectUtil.isNotEmpty(dbUser)){
            throw new CustomException(ResultCodeEnum.USER_EXIST_ERROR);
        }
        if(ObjectUtil.isEmpty(user.getPassword()))
        {
            user.setPassword(Constants.USER_DEFAULT_PASSWORD);
        }
        if (ObjectUtil.isEmpty(user.getName()))
        {
            user.setName(user.getUsername());

        }
        user.setRole(RoleEnum.USER.name());
        userMapper.insert(user);
    }

    public User selectByUsername(String username) {
        User p =new User();
        p.setUsername(username);
        List<User> userList =this.selectAll(p);
        return userList.size() ==0? null : userList.get(0);
    }

  public void deleteById(Integer id) {
      userMapper.deleteById(id);

    }

    public void deleteBatch(List<Integer> ids) {
        for(Integer id : ids){
            this.deleteById(id);

        }    }

    public void updateById(User user) {
    userMapper.updateById(user);
    }


    public User selectById(Integer id) {
        return userMapper.selectById(id);
    }

   public List<User> selectAll(User user) {
        return userMapper.selectAll(user);
    }

    public PageInfo<User> selectPage(User user, Integer pageNum, Integer pageSize) {

      PageHelper.startPage(pageNum,pageSize);
      List<User> userList=userMapper.selectAll(user);
      return PageInfo.of(userList);

    }
    public Account login(Account account) {
        Account dbUser = userMapper.selectByUsername(account.getUsername());
        if (ObjectUtil.isNull(dbUser)) {
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
        }
        if (!account.getPassword().equals(dbUser.getPassword())) {
            throw new CustomException(ResultCodeEnum.USER_ACCOUNT_ERROR);
        }
        // 生成token
        String tokenData = dbUser.getId() + "-" + RoleEnum.USER.name();
        String token = TokenUtils.createToken(tokenData, dbUser.getPassword());
        dbUser.setToken(token);
        Certification certification = certificationService.selectByUserId(dbUser.getId());
        dbUser.setRider(ObjectUtil.isNotNull(certification)&&"通过".equals(certification.getStatus())) ;
        return dbUser;
    }
    /**
     * 注册
     */
    public void register(Account account) {
        User user = new User();
        BeanUtils.copyProperties(account, user);
        this.add(user);
    }

    public void charge(Double money) {
        Account currentUser = TokenUtils.getCurrentUser();
        currentUser.setAccount(currentUser.getAccount().add(BigDecimal.valueOf(money)));
        this.updateById((User) currentUser);

        // 记录收支明细

    }

}