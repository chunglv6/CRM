package com.viettel.etc.services.impl;

import com.viettel.etc.repositories.tables.entities.ContractEntity;
import com.viettel.etc.repositories.tables.entities.CustomerEntity;
import com.viettel.etc.repositories.tables.entities.LuckyCodeEntity;
import com.viettel.etc.services.LuckyService;
import com.viettel.etc.services.tables.CustomerServiceJPA;
import com.viettel.etc.services.tables.LuckyCodeServiceJPA;
import com.viettel.etc.utils.FnCommon;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LuckyServiceImpl implements LuckyService {

    @Autowired
    LuckyCodeServiceJPA luckyCodeServiceJPA;

    @Autowired
    CustomerServiceJPA customerServiceJPA;

    @Autowired
    SMSServiceImpl smsService;

    @Autowired
    EmailServiceImpl emailService;

    @Value("${sms.user.lucky-code}")
    String smsContent;

    @Override
    public String genLuckyCode(ContractEntity contractEntity, int type, Authentication authentication) {
        Calendar effDate = Calendar.getInstance();
        Calendar expDate = Calendar.getInstance();
        long currently = Calendar.getInstance().getTimeInMillis();
        effDate.set(2021, Calendar.JANUARY, 27);
        expDate.set(2021, Calendar.FEBRUARY, 19);
        CustomerEntity customerEntity = null;
        List<LuckyCodeEntity> entityList = luckyCodeServiceJPA.getByCustomerIdAndLuckyType(contractEntity.getCustId(), type);
        if (currently < effDate.getTimeInMillis() || currently > expDate.getTimeInMillis() || !FnCommon.isNullOrEmpty(entityList)) {
            return null;
        } else {
            customerEntity = customerServiceJPA.getOne(contractEntity.getCustId());
        }

        String luckyCode = getPrefixLuckyCode(type) + RandomStringUtils.randomAlphabetic(5).toUpperCase() + RandomStringUtils.randomNumeric(6);
        while (luckyCodeServiceJPA.existsByLuckyCode(luckyCode)) {
            luckyCode = getPrefixLuckyCode(type) + RandomStringUtils.randomAlphabetic(5).toUpperCase() + RandomStringUtils.randomNumeric(6);
        }
        LuckyCodeEntity luckyCodeEntity = new LuckyCodeEntity();
        luckyCodeEntity.setLuckyCode(luckyCode);
        luckyCodeEntity.setCustomerId(customerEntity.getCustId());
        luckyCodeEntity.setPhoneNumber(customerEntity.getPhoneNumber());
        luckyCodeEntity.setLuckyType(type);
        luckyCodeEntity.setCreateUser(FnCommon.getUserLogin(authentication));
        luckyCodeServiceJPA.save(luckyCodeEntity);

        // Send SMS lucky code
        String content = String.format(smsContent, contractEntity.getContractNo(), luckyCodeEntity.getLuckyCode());
        smsService.sendSMS(customerEntity.getPhoneNumber(), content, authentication);

        // Send Email lucky code
        String subject = new String("[ePass] Thông báo Chương Trình Quay Số Trúng Thưởng của ePass Tết Tân Sửu 2021 của ePass".getBytes(), Charset.forName(StandardCharsets.UTF_8.name()));
        String filePath = "template" + File.separator + "template_email_lucky_code.txt";
        Map<String, String> parameter = new HashMap<>();
        parameter.put("PARAM1", customerEntity.getCustName());
        parameter.put("PARAM2", luckyCodeEntity.getLuckyCode());
        emailService.sendMail(subject, contractEntity.getNoticeEmail(), filePath, parameter, authentication);
        return luckyCodeEntity.getLuckyCode();
    }

    private String getPrefixLuckyCode(int type) {
        switch (type) {
            case 1:
                return "N";
            case 2:
                return "T";
            case 3:
                return "V";
            default:
                return "";
        }
    }
}
