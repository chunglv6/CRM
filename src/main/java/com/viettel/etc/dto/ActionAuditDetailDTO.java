package com.viettel.etc.dto;

import com.viettel.etc.repositories.tables.entities.ActionAuditDetailEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.json.JSONException;
import org.json.JSONObject;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.sql.Date;
import java.util.Objects;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class ActionAuditDetailDTO {
    String oldValue;
    String newValue;
    private Object oldEntity;

    public ActionAuditDetailDTO(Object entity, int type) throws IllegalAccessException, JSONException {
        JSONObject newValueJson = new JSONObject();

        Field[] fields = entity.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            if (Objects.nonNull(field.get(entity))) {
                Column column = field.getAnnotation(Column.class);
                if (Objects.nonNull(column)) {
                    newValueJson.put(field.getAnnotation(Column.class).name(), field.get(entity));
                }
            }
        }

        if (type == 1){
            this.oldValue = "";
            this.newValue = newValueJson.toString();
        }
        else{
            this.oldValue = newValueJson.toString();
            this.newValue = "";
        }
    }

    public ActionAuditDetailDTO(Object entity) throws IllegalAccessException, JSONException {
        JSONObject newValueJson = new JSONObject();

        Field[] fields = entity.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            if (Objects.nonNull(field.get(entity))) {
                Column column = field.getAnnotation(Column.class);
                if (Objects.nonNull(column)) {
                    newValueJson.put(field.getAnnotation(Column.class).name(), field.get(entity));
                }
            }
        }

        this.oldValue = "";
        this.newValue = newValueJson.toString();
    }

    public ActionAuditDetailDTO(Object oldEntity, Object newEntity) throws IllegalAccessException, JSONException {
        if (oldEntity == null || newEntity == null){
            return;
        }
        JSONObject oldValueJson = new JSONObject();
        JSONObject newValueJson = new JSONObject();

        Field[] fields = oldEntity.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            boolean oldToNew = Objects.nonNull(field.get(oldEntity)) && !field.get(oldEntity).equals(field.get(newEntity));
            boolean newToOld = Objects.nonNull(field.get(newEntity)) && !field.get(newEntity).equals(field.get(oldEntity));
            if (oldToNew || newToOld) {
                Column column = field.getAnnotation(Column.class);
                if (Objects.nonNull(column)) {
                    if (field.get(oldEntity) != field.get(newEntity)){
                        oldValueJson.put(field.getAnnotation(Column.class).name(), field.get(oldEntity));
                        newValueJson.put(field.getAnnotation(Column.class).name(), field.get(newEntity));
                    }
                }
            }
        }

        this.oldValue = oldValueJson.toString();
        this.newValue = newValueJson.toString();
    }

    public ActionAuditDetailEntity toEntity(Long actionAuditId, String tableName, Long pkId){
        ActionAuditDetailEntity entity = new ActionAuditDetailEntity();
        entity.setActionAuditId(actionAuditId);
        entity.setCreateDate(new Date(System.currentTimeMillis()));
        entity.setTableName(tableName);
        entity.setPkId(pkId);
        entity.setOldValue(this.oldValue);
        entity.setNewValue(this.newValue);

        return entity;
    }
}
