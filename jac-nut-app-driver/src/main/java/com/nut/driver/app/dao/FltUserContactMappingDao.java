package com.nut.driver.app.dao;

import com.nut.driver.app.domain.FltUserContactMapping;
import com.nut.driver.app.domain.UserContactMapping;
import com.nut.driver.app.pojo.UserInfoPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FltUserContactMappingDao {
    int deleteByPrimaryKey(Long id);

    int insert(FltUserContactMapping record);

    int insertSelective(FltUserContactMapping record);

    FltUserContactMapping selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(FltUserContactMapping record);

    int updateByPrimaryKey(FltUserContactMapping record);

    /* ----------------自定义sql由此向下---------------- */

    /**
     * 查询联系人列表
     *
     * @param createUserId APP创建人ID
     * @param keyword      查询关键字
     * @return 联系人列表
     */
    List<UserInfoPojo> selectList(@Param("createUserId") Long createUserId, @Param("keyword") String keyword);

    /**
     * 根据用户ID和创建人用户ID查询
     *
     * @param userId       用户ID
     * @param createUserId 创建人用户ID
     * @return 唯一记录
     */
    FltUserContactMapping selectByUserIdAndCreateUserId(@Param("userId") Long userId, @Param("createUserId") Long createUserId);

    /**
     * 批量插入
     *
     * @param list 批量插入记录
     * @return 成功条数
     */
    int batchInsert(List<FltUserContactMapping> list);

    /**
     * 根据用户ID和手机号查询
     *
     * @param phone   手机号
     * @param userId 人用户ID
     * @return 唯一记录
     */
    List<UserContactMapping> selectByUserIdAndPhone(@Param("phone") String phone, @Param("userId") Long userId);

    int insertUserContant(UserContactMapping userContactMapping);

    List<UserContactMapping> selectContactList(@Param("keyword") String keyWord, @Param("userId") Long userId);

    int deleteContentById(Long id);
}