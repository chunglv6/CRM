package com.viettel.etc.repositories.tables.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;

/**
 * Autogen class Entity: Create Entity For Table Name Ws_audit
 *
 * @author ToolGen
 * @date Wed Jun 24 15:24:43 ICT 2020
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "WS_AUDIT")
public class WsAuditEntity implements Serializable {

    @Id
    @GeneratedValue(generator = "WS_AUDIT_SEQ")
    @SequenceGenerator(name = "WS_AUDIT_SEQ", sequenceName = "WS_AUDIT_SEQ", allocationSize = 1)
    @Column(name = "WS_AUDIT_ID")
    Long wsAuditId;

    @Column(name = "WS_CALL_TYPE")
    String wsCallType;

    @Column(name = "ACT_TYPE_ID")
    Long actTypeId;

    @Column(name = "REQUEST_TIME")
    Date requestTime;

    @Column(name = "ACTION_USER_NAME")
    String actionUserName;

    @Column(name = "WS_URI")
    String wsUri;

    @Column(name = "SOURCE_APP_ID")
    String sourceAppId;

    @Column(name = "IP_PC")
    String ipPc;

    @Column(name = "DESTINATION_APP_ID")
    String destinationAppId;

    @Column(name = "STATUS")
    String status;

    @Column(name = "FINISH_TIME")
    Long finishTime;

    @Column(name = "MSG_REQUEST")
    byte[] msgRequest;

    @Column(name = "MSG_REPONSE")
    byte[] msgReponse;

    @Column(name = "REQUEST_IN_ID")
    String requestInId;

    @Column(name = "REQUEST_OUT_ID")
    String requestOutId;

    @Column(name = "REQUEST_TIME_MILISECOND")
    Long requestTimeMiliSecond;

    public enum Status {
        NOT_SUCCESS("0"),
        SUCCESS("1"),
        ERROR("2");
        public final String value;

        Status(String value) {
            this.value = value;
        }
    }
}
