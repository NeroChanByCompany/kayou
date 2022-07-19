package com.nut.servicestation.common.utils;


import com.alibaba.fastjson.JSON;
import com.nut.common.utils.HttpUtil;
import com.nut.servicestation.app.form.SendEmailForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author duxj
 *
 */
@Service
@Slf4j
public class MailSenderUtil {


    private static final Map<String, String> FILE_CONTENT_CACHE = new ConcurrentHashMap<String, String>();

    public static boolean sendCommonEmail(String sendEmailUrl, String title, String content, String toUser){

        SendEmailForm command = new SendEmailForm();
        List<String> toEmails = new ArrayList<>();
        toEmails.add(toUser);
        command.setToEmails(toEmails);
        command.setSubject(title);
        command.setContent(content);
        command.setWm("1");
        String jsonStr = JSON.toJSONString(command);
        int returnCore = 0;
        try {
            String resultJson = HttpUtil.postJson(sendEmailUrl,jsonStr,"");
            returnCore = JSON.parseObject(resultJson).getIntValue("resultCode");

        } catch (IOException e) {
            e.printStackTrace();
        }
        if(returnCore==200){
            return true;
        }else{
            return false;
        }
    }

    public static boolean sendCommonEmailList(String sendEmailUrl, String title, String content, String toUsers){

        SendEmailForm command = new SendEmailForm();
        List<String> toEmails = new ArrayList<>();
        for (String s : toUsers.split(",")) {
            toEmails.add(s);
        }
        command.setToEmails(toEmails);
        command.setSubject(title);
        command.setContent(content);
        command.setWm("1");
        String jsonStr = JSON.toJSONString(command);
        int returnCore = 0;
        try {
            String resultJson = HttpUtil.postJson(sendEmailUrl,jsonStr,"");
            returnCore = JSON.parseObject(resultJson).getIntValue("resultCode");

        } catch (IOException e) {
            e.printStackTrace();
        }
        if(returnCore==200){
            return true;
        }else{
            return false;
        }
    }
}
