package com.viettel.etc.dto;

import com.viettel.etc.repositories.tables.entities.ActionAuditEntity;
import com.viettel.etc.utils.FnCommon;
import lombok.Data;
import org.springframework.security.core.Authentication;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Date;

@Data
public class ActionAuditDTO {
    private Long reasonId;

    private Long actTypeId;

    private String actionUserName;

    private String actionUserFullName;

    private Long custId;

    private Long contractId;

    private Long vehicleId;

    private Date createDate;

    private Long appId;

    private String ip;

    private Long status;

    public ActionAuditEntity toEntity(Authentication authentication, Long reasonId, Long actTypeId, Long custId, Long contractId, Long vehicleId, String ip){
        ActionAuditEntity actionAuditEntity = new ActionAuditEntity();
        actionAuditEntity.setActReasonId(reasonId);
        actionAuditEntity.setActTypeId(actTypeId);
        actionAuditEntity.setActionUserName(FnCommon.getUserLogin(authentication));
        actionAuditEntity.setActionUserFullName(FnCommon.getUserLogin(authentication));
        if(custId != null) {
            actionAuditEntity.setCustId(custId);
        }
        if(contractId != null){
            actionAuditEntity.setContractId(contractId);
        }
        if(vehicleId != null){
            actionAuditEntity.setVehicleId(vehicleId);
        }
        actionAuditEntity.setCreateDate(new Date(System.currentTimeMillis()));
        actionAuditEntity.setAppId(FnCommon.getClientId(authentication));
        actionAuditEntity.setIpPc(ip);
        actionAuditEntity.setStatus(ActionAuditEntity.Status.SUCCESS.value);
        return actionAuditEntity;
    }

    public ActionAuditEntity toEntity(Authentication authentication, Long reasonId, Long actTypeId, Long custId, Long contractId, Long vehicleId) throws UnknownHostException {
        String ip = InetAddress.getLocalHost().getHostAddress();
        return toEntity(authentication, reasonId, actTypeId, custId, contractId, vehicleId, ip);
    }

    public ActionAuditEntity toEntity(Authentication authentication, Long reasonId, Long actTypeId, Long custId, Long contractId) throws UnknownHostException {
        return toEntity(authentication, reasonId, actTypeId, custId, contractId, null);
    }
}
