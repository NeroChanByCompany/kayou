package com.nut.driver.app.service.impl;

import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.driver.app.dao.FltFleetUserMappingDao;
import com.nut.driver.app.dao.FltUserContactMappingDao;
import com.nut.driver.app.dao.UserDao;
import com.nut.driver.app.domain.FltUserContactMapping;
import com.nut.driver.app.domain.User;
import com.nut.driver.app.domain.UserContactMapping;
import com.nut.driver.app.dto.UserContactsDto;
import com.nut.driver.app.entity.UserEntity;
import com.nut.driver.app.form.UserContactAddForm;
import com.nut.driver.app.form.UserContactDeleteForm;
import com.nut.driver.app.form.UserContactsForm;
import com.nut.driver.app.pojo.UserInfoPojo;
import com.nut.driver.app.service.ContactService;
import com.nut.driver.common.utils.ChineseUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service("ContactService")
public class ContactServiceImpl implements ContactService {

    private static final int Y = 1;
    private static final int N = 0;

    @Autowired
    private UserDao userMapper;

    @Autowired
    private FltUserContactMappingDao fltUserContactMappingMapper;

    @Autowired
    private FltFleetUserMappingDao fltFleetUserMappingMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HttpCommandResultWithData add(UserContactAddForm command) {
        log.info("[add]start");
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        //List<User> userList = generateUserId(command.getNickname(), command.getPhone(), command.getAutoIncreaseId());
        List<UserEntity> userList = userMapper.findListByPhoneAndApptype(command.getPhone());
        if(CollectionUtils.isEmpty(userList)){
            result.setResultCode(ECode.USER_DNE.code());
            result.setMessage("????????????????????????App???");
        }
        for(UserEntity user : userList){
            // ???????????????????????????????????????
            FltUserContactMapping existEntity = fltUserContactMappingMapper.selectByUserIdAndCreateUserId(user.getId(), command.getAutoIncreaseId());
            if (existEntity != null) {
                result.setResultCode(ECode.CLIENT_ERROR.code());
                result.setMessage("????????????????????????????????????");
                return result;
            }

            // ???????????????
            insertRecord(user.getId(), command);
        }

        log.info("[add]end");
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HttpCommandResultWithData delete(UserContactDeleteForm command) {
        log.info("[delete]start");
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        FltUserContactMapping existEntity = fltUserContactMappingMapper.selectByUserIdAndCreateUserId(command.getDelUserId(), command.getAutoIncreaseId());
        if (existEntity != null) {
            fltUserContactMappingMapper.deleteByPrimaryKey(existEntity.getId());
        }
        log.info("[delete]end");
        return result;
    }

    @Override
    public HttpCommandResultWithData query(UserContactsForm command) {
        log.info("[query]start");
        HttpCommandResultWithData<List> result = new HttpCommandResultWithData<>();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        if (command.getKeyword() != null && "".equals(command.getKeyword().trim())) {
            command.setKeyword(null);
        }
        List<UserInfoPojo> queryList = fltUserContactMappingMapper.selectList(command.getAutoIncreaseId(), command.getKeyword());
        log.info("[query]queryList.size:{}", queryList.size());

        // ???????????????????????????
        List<Long> exclusiveTeamUsers = null;
        if (queryList.size() > 0 && command.getExclusiveTeamId() != null && command.getExclusiveRole() != null) {
            exclusiveTeamUsers = getExclusiveUserIds(command.getExclusiveTeamId(), command.getExclusiveRole());
        }

        // ???????????????
        Map<String, List<UserInfoPojo>> mapByPinyin = queryList.stream()
                .collect(Collectors.groupingBy(e -> ChineseUtil.getNameFirstSpell(e.getName())));
        // ????????????
        List<String> keys = mapByPinyin.keySet().stream().sorted().collect(Collectors.toList());
        List<Map<String, List<UserContactsDto>>> data = new ArrayList<>();
        for (String pinyin : keys) {
            Map<String, List<UserContactsDto>> map = new HashMap<>(3);
            map.put(pinyin, pojoToDto(mapByPinyin.get(pinyin), exclusiveTeamUsers));
            data.add(map);
        }
        result.setData(data);
        log.info("[query]end");
        return result;
    }

    /**
     * ???????????????????????????????????????
     * @return HttpCommandResultWithData
     */
    @Transactional(rollbackFor = Exception.class)
    public HttpCommandResultWithData addContact(UserContactAddForm form) {
        log.info("[addContact]start");
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());

        // ???????????????????????????????????????
        List<UserContactMapping> userContactMappingList = fltUserContactMappingMapper.selectByUserIdAndPhone(form.getPhone(), form.getAutoIncreaseId());
        if (CollectionUtils.isNotEmpty(userContactMappingList)) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("????????????????????????????????????");
            return result;
        }
        // ???????????????
        insertUserContant(form);

        log.info("[addContact]end");
        return result;
    }

    /**
     * ???????????????
     */
    private void insertRecord(long userId, UserContactAddForm command) {
        log.info("[insertRecord]start");
        FltUserContactMapping userContactMapping = new FltUserContactMapping();
        userContactMapping.setUserId(userId);
        userContactMapping.setNickname(command.getNickname());
        userContactMapping.setPhone(command.getPhone());
        userContactMapping.setCreateUserId(command.getAutoIncreaseId());
        Date now = new Date();
        userContactMapping.setCreateTime(now);
        userContactMapping.setUpdateTime(now);
        fltUserContactMappingMapper.insertSelective(userContactMapping);
        log.info("[insertRecord]end");
    }
    /**
     * ????????????????????????????????????
     */
    private List<Long> getExclusiveUserIds(Long teamId, Integer role) {
        List<UserInfoPojo> queryList = fltFleetUserMappingMapper.selectByTeamIdAndRole(teamId, role, null);
        log.info("[getExclusiveUserIds]teamId:{}||role:{}||size:{}", teamId, role, queryList.size());
        return queryList.stream().map(UserInfoPojo::getId).collect(Collectors.toList());
    }
    /**
     * ????????????
     */
    private List<UserContactsDto> pojoToDto(List<UserInfoPojo> pojos, List<Long> exclusiveTeamUsers) {
        List<UserContactsDto> result = new ArrayList<>();
        for (UserInfoPojo pojo : pojos) {
            UserContactsDto dto = new UserContactsDto();
            dto.setNickname(pojo.getName());
            dto.setPhone(pojo.getPhone());
            dto.setUserId(pojo.getId());
            if (exclusiveTeamUsers != null) {
                if (exclusiveTeamUsers.contains(pojo.getId())) {
                    dto.setBound(Y);
                } else {
                    dto.setBound(N);
                }
            }
            result.add(dto);
        }
        return result;
    }

    /**
     * ???????????????
     */
    private void insertUserContant(UserContactAddForm command) {
        log.info("[insertUserContant]start");
        UserContactMapping userContactMapping = new UserContactMapping();
        userContactMapping.setUserId(command.getAutoIncreaseId());
        userContactMapping.setNickname(command.getNickname());
        userContactMapping.setPhone(command.getPhone());
        Date now = new Date();
        userContactMapping.setCreateTime(now);
        userContactMapping.setUpdateTime(now);
        fltUserContactMappingMapper.insertUserContant(userContactMapping);
        log.info("[insertUserContant]end");
    }

}
