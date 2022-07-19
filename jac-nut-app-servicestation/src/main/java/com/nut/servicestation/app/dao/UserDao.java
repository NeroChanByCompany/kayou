package com.nut.servicestation.app.dao;

import com.nut.servicestation.app.domain.User;
import com.nut.servicestation.app.dto.StaffListDto;
import com.nut.servicestation.app.dto.UserInfoDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserDao {
    /**
     * 根据用户名查询用户信息
     *
     * @param DbName    数据库名
     * @param accountName 用户名
     * @return 用户信息
     */
    List<UserInfoDto> queryLoginInfoSql(@Param("DbName") String DbName, @Param("accountName") String accountName);

    /**
     * 根据用户名更新密码
     *
     * @param DbName    寰游数据库名
     * @param accountPwd  密码MD5
     * @param accountName 用户名
     */
    int updatePwdByAccountName(@Param("DbName") String DbName,
                               @Param("accountPwd") String accountPwd,
                               @Param("accountName") String accountName);

    /**
     * 根据用户名更新用户昵称和联系方式
     *
     * @param DbName        寰游数据库名
     * @param accountNickname 昵称
     * @param accountLinktel  联系方式
     * @param accountName     用户名
     */
    int updateUserInfoByAccountName(@Param("DbName") String DbName,
                                    @Param("accountNickname") String accountNickname,
                                    @Param("accountLinktel") String accountLinktel,
                                    @Param("accountName") String accountName,
                                    @Param("sendMessageKey") String sendMessageKey);

    /**
     * 查询指派人员列表
     * @param DbName 寰游数据库名
     * @param stationId 服务站ID
     * @return 人员列表
     */
    List<StaffListDto> queryStaffList(@Param("DbName") String DbName, @Param("stationId") String stationId);

    String queryStationPhone(@Param("DbName") String DbName, @Param("stationId") String stationId);

    String queryUserIdByPhone(@Param("list") List<String> phone);

    String queryAccountIds(@Param("DbName") String DbName, @Param("stationId") String stationId, @Param("jobType") int jobType);

    Long queryIdByPhone(@Param("phone") String phone);

     /**
     * 查询建单范围
     *
     * @param DbName    寰游数据库名
     * @param serviceCode 用户名
     * @return 建单范围
     */
     Integer querySecondaryCreateWoRange(@Param("DbName") String DbName, @Param("serviceCode") String serviceCode);

     String queryUcIdByWorkCode(String workCode);

     User selectByPhone(String phone);

     Integer queryStationId(@Param("DbName") String DbName, @Param("userId") Long userId);

    /**
     * 查询消息key
     * @param databaseName
     * @param userId
     * @return
     */
    UserInfoDto queryUserInfo(@Param("databaseName") String databaseName, @Param("userId") String userId);

    /**
     * 更新sendmessagekey
     * @param accountId
     */
    void updateById(@Param("databaseName") String databaseName,@Param("accountId") String accountId);

    /**
     * 更新状态为为激活
     * @param databaseName
     * @param userId
     */
    void updateLockByAccountName(@Param("databaseName")String databaseName,@Param("accountId") String userId,@Param("lockStatus") Integer lockStatus);

    /**
     * 根据accountId查询账号名
     * @param databaseName tsp数据库名
     * @param accountId
     * @return
     */
    String queryUserNameById(@Param("databaseName")String databaseName,@Param("accountId") Long accountId);
}
