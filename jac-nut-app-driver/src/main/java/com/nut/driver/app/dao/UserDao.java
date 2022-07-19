package com.nut.driver.app.dao;

import com.nut.driver.app.domain.InviterMessage;
import com.nut.driver.app.domain.UserForForum;
import com.nut.driver.app.entity.UserEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nut.driver.app.form.UpdatePasswordForm;
import com.nut.driver.app.pojo.UserInfoPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 用户表
 *
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-15 16:45:02
 */
@Mapper
public interface UserDao extends BaseMapper<UserEntity> {

    /**
     * 插入数据
     *
     * @param record 记录
     * @return 影响行数
     */
    int insertSelective(UserEntity record);

    /**
     * 根据条件更新数据
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(UserEntity record);

    UserEntity findNoRegisterPhone(String phone);

    UserEntity findByPhoneAndType(@Param("phone") String phone, @Param("type") String type);

    /**
     * 根据主键更新数据
     *
     * @param record 记录
     * @return 影响行数
     */
    int updateByPrimaryKey(UserEntity record);

    /**
     * 根据用户id查询数据
     *
     * @param id 用户id（主键）
     * @return 数据
     */
    UserInfoPojo findById(Long id);

    /**
     * 根据用户中心ID查询数据
     *
     * @param ucId 用户中心Id
     * @return 数据
     */
    UserEntity findByUcId(String ucId);

    /**
     * 重置密码
     *
     * @param form
     * @return
     */
    int resetPasswordInfo(UpdatePasswordForm form);

    /**
     * 根据Id查询数据
     *
     * @param id id
     * @return 查询数据
     */
    UserEntity selectByPrimaryKey(Long id);

    /**
     * 修改用户签名
     *
     * @param id
     * @param signature
     * @return
     */
    int updateSignature(@Param("id") String id, @Param("signature") String signature);

    String getCustomIdsByUserIdNew(@Param("driverId") Long driverId, @Param("list") List<String> list); //新版APP使用

    int deleteUserCustomMaintanceCommon(List<String> ids);

    List<UserEntity> findListByPhoneAndApptype(@Param("phone") String phone);

    /**
     * 批量查询ucId
     */
    List<String> queryUcIdById(List<Long> list);

    String findPhoneByUcid(String ucid);

    /**
     * @Author liuBing
     * @Description //TODO 论坛查询用户信息
     * @Date 15:30 2021/7/23
     * @Param [idList]
     * @return java.util.List<com.nut.driver.app.domain.UserForForum>
     **/
    List<UserForForum> findUserListByIdList(List<String> idList);

    /**
     * @Author liuBing
     * @Description //TODO 根据userId查询ucId
     * @Date 16:38 2021/5/27
     * @Param [receiverId]
     * @return java.lang.String
     **/
    String selectById(String userId);

    /**
     * 根据id查询ucId
     * @param id
     * @return
     */
    String findUcId(@Param("id") Long id);

    /**
     * 查询用户标记user_type
     */
    InviterMessage selectUserTypeByucId(@Param("ucId") String ucId);

    // 获取用户网红标识
    Integer userStar(@Param("ucId") String ucId);

    // 获取用户是否为网红
    Integer isStat(@Param("userId") String userId);

    // 获取用户是否停用
    Integer isDisable(@Param("userId") String userId);

    // 根据用户电话与客户端类型查询ucid
    String ucIdByPhoneType(@Param("phone") String phone,@Param("type") String type);
}
