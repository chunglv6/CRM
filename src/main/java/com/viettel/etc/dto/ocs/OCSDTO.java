package com.viettel.etc.dto.ocs;

import com.viettel.etc.dto.ContractDTO;
import com.viettel.etc.repositories.tables.entities.ContractEntity;
import com.viettel.etc.repositories.tables.entities.ContractPaymentEntity;
import com.viettel.etc.repositories.tables.entities.CustomerEntity;
import com.viettel.etc.utils.FnCommon;

import java.util.Objects;

/**
 * Class chua dto cua OCS
 */
public class OCSDTO {
    /**
     * set value to contractForm
     */
    public OCSCreateContractForm createContractFormFromContractEntity(ContractEntity entity, CustomerEntity customer, ContractDTO contractDTO) {
        OCSCreateContractForm form = new OCSCreateContractForm();
        form.setContractId(String.valueOf(entity.getContractId()));
        form.setContractCode(entity.getContractNo());
        form.setCustomerId(String.valueOf(entity.getCustId()));
        form.setCustomerName(customer.getCustName());
        form.setMsisdn(contractDTO.getNoticePhoneNumber());
        form.setEmailAddress(contractDTO.getNoticeEmail());
        form.setBirthDay(FnCommon.formatOCSDate(customer.getBirthDate()));
        form.setEffDate(FnCommon.formatOCSDate(entity.getEffDate()));
        if (Objects.nonNull(entity.getExpDate())) {
            form.setExpDate(FnCommon.formatOCSDate(entity.getExpDate()));
        }
        form.setCustomerType(String.valueOf(customer.getCustTypeId()));
        form.setContractType(String.valueOf(customer.getCustTypeId()));
        form.setCustomerStatus(customer.getStatus());
        form.setContractStatus(entity.getStatus());
        form.setChargeType(entity.getPayCharge());
        return form;
    }

    /**
     * Set value cho OCSUpdateContractForm
     *
     * @param contract
     * @param customer
     * @param contractDTO
     * @return
     */
    public OCSUpdateContractForm updateContractOCSFormFromEntity(ContractEntity contract, CustomerEntity customer, ContractDTO contractDTO) {
        OCSUpdateContractForm form = new OCSUpdateContractForm();
        form.setContractId(String.valueOf(contract.getContractId()));
        form.setContractCode(contract.getContractNo());
        form.setCustomerId(String.valueOf(customer.getCustId()));
        form.setCustomerName(customer.getCustName());
        form.setMsisdn(customer.getCustName());
        form.setMsisdn(customer.getPhoneNumber());
        form.setBirthDay(FnCommon.formatOCSDate(customer.getBirthDate()));
        form.setIdentify(customer.getRepIdentityNumber());
        form.setEffDate(FnCommon.formatOCSDate(contract.getEffDate()));
        if (Objects.nonNull(contract.getExpDate())) {
            form.setExpDate(FnCommon.formatOCSDate(contract.getExpDate()));
        }
        form.setCustomerType(String.valueOf(customer.getCustTypeId()));
        form.setCustomerStatus(customer.getStatus());
        // status 1 is ACTIVE in OCS
        form.setContractStatus(ContractPaymentEntity.Status.ACTIVATED.value);
        form.setLangid(String.valueOf(Language.VN.ordinal()));
        if (Objects.nonNull(contractDTO) && Objects.nonNull(contractDTO.getNoticePhoneNumber())) {
            form.setMsisdn(contractDTO.getNoticePhoneNumber());
        }
        if (Objects.nonNull(contractDTO) && !FnCommon.isNullOrEmpty(contractDTO.getNoticeEmail())) {
            form.setEmailAddress(contractDTO.getNoticeEmail());
        }
        // Khong day thong tin trong contract_payment khi cap nhat thong tin hop
//        if (payment != null) {
//            // Neu la tai khoan lien ket la viettelpay thi methodCharge fix cung = 2
//            if (payment.getMethodRechargeId() == 3641L) {
//                form.setChargeMethod(OCSUpdateContractForm.ChargeMethod.VIETTELPAY.value);
//            } else {
//                form.setChargeMethod(String.valueOf(payment.getMethodRechargeId()));
//            }
//
//            form.setAccountNumber(payment.getAccountNumber());
//            form.setAccountOwner(payment.getAccountOwner());
//            form.setToken(payment.getToken());
//        }
        return form;
    }


    public enum Language {
        VN(57),
        EN(89);
        int code;

        Language(int code) {
            this.code = code;
        }
    }
}
