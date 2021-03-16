package com.viettel.etc.dto.boo;

import com.viettel.etc.repositories.tables.entities.ExceptionListEntity;
import com.viettel.etc.utils.Constants;
import com.viettel.etc.utils.FnCommon;
import lombok.Data;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

@Data
public class ReqExceptionListDTO {
    String plate;

    String etag;

    Long request_id;

    Long request_datetime;

    Long station_in_id;

    Long station_out_id;

    String type;

    String register_vehicle_type;

    String exception_vehicle_type;

    Long price_turn;

    Long price_monthly;

    Long price_quarterly;

    String start_date;

    String end_date;

    Long status;

    Long action_type;

    public String getStringReq(ExceptionListEntity entity, String actionType, Long stationIn, Long stationOut, String plateType) throws JSONException {
        JSONObject ret = new JSONObject();
        Long time = System.currentTimeMillis();
        if (entity.getPlateNumber() != null) {
            ret.put("plate", FnCommon.getPlateNumberBoo1(entity.getPlateNumber(), plateType));
        }
        ret.put("etag", entity.getEpc());
        ret.put("request_id", time);
        ret.put("request_datetime", time);
        if (entity.getStationId() != null) {
            ret.put("station_in_id", entity.getStationId());
            ret.put("station_out_id", entity.getStationId());
        } else {
            ret.put("station_in_id", stationIn);
            ret.put("station_out_id", stationOut);
        }

        switch (entity.getExceptionType()) {
            case Constants.EXCEPTION_VEHICLE:
                ret.put("type", "LX");
                ret.put("vehicle_type", entity.getVehicleTypeId());
                ret.put("exception_vehicle_type", entity.getExceptionVehicleType());
                break;
            case Constants.EXCEPTION_TICKET:
                ret.put("type", "GV");
                ret.put("price_turn", entity.getPriceTurn());
                ret.put("price_monthly", entity.getPriceMonthly());
                ret.put("price_quarterly", entity.getPriceQuarterly());
                ret.put("vehicle_type", entity.getVehicleTypeId());
                break;
            case Constants.EXCEPTION_PRIORITY:
                ret.put("type", "WL");
                break;
            case Constants.EXCEPTION_BAN:
                ret.put("type", "BL");
                break;
        }
        if (entity.getEffDate() != null) {
            ret.put("start_date", FnCommon.convertDateToStringOther(entity.getEffDate(), Constants.COMMON_DATE_FORMAT_BOO_24H));
        }
        if (entity.getExpDate() != null) {
            ret.put("end_date", FnCommon.convertDateToStringOther(entity.getExpDate(), Constants.COMMON_DATE_FORMAT_BOO_24H));
        }
        ret.put("status", 1L);
        ret.put("action_type", actionType);
        return ret.toString();
    }
}
