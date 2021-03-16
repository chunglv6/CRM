package com.viettel.etc.services.impl;

import com.viettel.etc.dto.OtpDTO;
import com.viettel.etc.repositories.tables.entities.ContractEntity;
import com.viettel.etc.repositories.tables.entities.CustomerEntity;
import com.viettel.etc.repositories.tables.entities.OtpEntity;
import com.viettel.etc.repositories.tables.entities.OtpIdentify;
import com.viettel.etc.services.OtpService;
import com.viettel.etc.services.tables.ContractServiceJPA;
import com.viettel.etc.services.tables.CustomerServiceJPA;
import com.viettel.etc.services.tables.OtpServiceJPA;
import com.viettel.etc.utils.exceptions.EtcException;
import com.viettel.etc.utils.FnCommon;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * Thuc hien chuc nang OTP
 */
@Service
public class OtpServiceImpl implements OtpService {
    @Autowired
    OtpServiceJPA otpServiceJPA;

    @Autowired
    ContractServiceJPA contractServiceJPA;

    @Autowired
    CustomerServiceJPA customerServiceJPA;

    @Autowired
    SMSServiceImpl smsService;

    @Value("${sms.user.request-otp}")
    String sms;

    @Value("${sms.otp.duration}")
    String duration;

    /***
     * Chuc nang yeu cau OTP
     * @param params
     * @param authentication
     * @return
     */
    @Override
    public Object requestOTP(OtpDTO params, Authentication authentication) {
        if (FnCommon.isNullOrEmpty(params.getPhone()) || FnCommon.isNullObject(params.getConfirmType())) {
            return null;
        }
        OtpIdentify id = new OtpIdentify(params.getPhone(), params.getConfirmType());
        if (otpServiceJPA.existsById(id)) {
            OtpEntity otpEntity = otpServiceJPA.getById(id);
            otpServiceJPA.delete(otpEntity);
        }
        String pass = RandomStringUtils.randomNumeric(6);
        OtpEntity data = new OtpEntity();
        data.setPhone(id.getPhone());
        data.setConfirmType(id.getConfirmType());
        data.setOtp(pass);
        data.setDuration(Integer.valueOf(duration));
        String contentSMS = String.format(sms, pass);
        smsService.sendSMS(params.getPhone(), contentSMS, authentication);
        otpServiceJPA.save(data);
        return "";
    }

    /***
     * Yeu cau OTP cho hop dong
     * @param params
     * @param authentication
     * @return
     */
    @Override
    public Object requestOtpContract(OtpDTO params, Authentication authentication) {
        ContractEntity contractEntity = contractServiceJPA.getByAccountUser(params.getUser());
        if (contractEntity == null) {
            throw new EtcException("crm.contract.not.exist");
        }
        CustomerEntity customerEntity = customerServiceJPA.getOne(contractEntity.getCustId());
        if (!params.getPhone().equals(contractEntity.getNoticePhoneNumber()) &&
                !params.getPhone().equals(customerEntity.getPhoneNumber()) &&
                !params.getPhone().equals(customerEntity.getAuthPhoneNumber()) &&
                !params.getPhone().equals(customerEntity.getRepPhoneNumber())) {
            throw new EtcException("validate.phone.not.belong");
        }
        return requestOTP(params, authentication);
    }

    @Override
    public Object otpContract(OtpDTO params, Authentication authentication) {
        ContractEntity contractEntity = contractServiceJPA.getOne(params.getContractId());
        if (contractEntity == null) {
            throw new EtcException("crm.contract.not.exist");
        }
        params.setPhone(contractEntity.getNoticePhoneNumber());
        return requestOTP(params, authentication);
    }
}
