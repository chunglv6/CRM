package com.viettel.etc.dto.ocs;

import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModelProperty;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2020-06-03T09:15:17.839Z")
public class OCSResponse {
    @SerializedName("resultCode")
    private String resultCode = null;

    @SerializedName("description")
    private String description = null;

    @SerializedName("subscriptionTicketId")
    private String subscriptionTicketId = null;

    public OCSResponse resultCode(String resultCode) {
        this.resultCode = resultCode;
        return this;
    }

    /**
     * Mã lỗi
     *
     * @return resultCode
     **/
    @ApiModelProperty(value = "Mã lỗi")
    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public OCSResponse description(String description) {
        this.description = description;
        return this;
    }

    /**
     * Mô tả lỗi
     *
     * @return description
     **/
    @ApiModelProperty(value = "Mô tả lỗi")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * id cua ve OCS
     *
     * @return description
     **/
    @ApiModelProperty(value = "id cua ve OCS")
    public String getSubscriptionTicketId() {
        return subscriptionTicketId;
    }

    public void setSubscriptionTicketId(String subscriptionTicketId) {

        this.subscriptionTicketId = subscriptionTicketId;
    }
}
