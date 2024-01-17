package au.bystritskaia.service;

import javax.persistence.Id;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class QueryBuilder<T> {

    public String select(Class<? extends T> clazz, Object primary) {
        Field id = this.getIDField(clazz);


        if (id == null) {
            throw new RuntimeException("Не удалось найти поле первичного ключа");
        }

        String outID = (primary instanceof String) ? "'" + primary + "'" : primary.toString();
        return """
                SELECT *
                FROM %s
                WHERE %s = %s
                """.formatted(clazz.getSimpleName(), id.getName(), outID);
    }

    private Field getIDField(Class<? extends T> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        Field[] superFields = clazz.getSuperclass().getDeclaredFields();

        Field[] join = new Field[fields.length + superFields.length];
        System.arraycopy(fields, 0, join, 0, fields.length);
        System.arraycopy(superFields, 0, join, fields.length, superFields.length);

        Field result = null;

        for (Field field : join) {
            if (field.isAnnotationPresent(Id.class)) {
                result = field;
                break;
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public String save(T entity) {
        Field id = this.getIDField((Class<T>) entity.getClass());
        if (id == null)
            throw new RuntimeException("Не удалось найти id поле");
        String idValue;
        try {
            idValue = id.get(entity).toString();
        } catch (IllegalAccessException e) {
            idValue = null;
        }

        return (idValue == null) ? this.generateInsertValue(entity, id) : this.generateUpdateValue(entity, id, idValue);
    }

    private String generateUpdateValue(T entity, Field id, String idValue) {
        try {

            StringBuilder query = new StringBuilder("Update ");
            query.append(entity.getClass().getSimpleName());
            query.append(" SET ");
            Map<String, Object> values = convertFieldsToMap(entity);

            String vals = values.entrySet().stream().map(entry -> {
                Class<?> entryClass = entry.getValue().getClass();
                if (entryClass.equals(String.class))
                    return entry.getKey() + " = '" + entry.getValue() + "'";
                if (entryClass.equals(Date.class))
                    return entry.getKey() + "'" + new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(entry.getValue()) + "'";

                return entry.getKey() + "=" + entry.getValue();

            }).collect(Collectors.joining(",", "(", ")"));
            query.append(vals);
            query.append("WHERE ").append(id).append(" = ").append(idValue);
            return query.toString();
        } catch (Exception ex) {
            throw new RuntimeException("Не удалось сформировать запрос на обновление");
        }
    }

    private String generateInsertValue(T entity, Field id) {
        try {
            id.setAccessible(true);
            id.set(entity, UUID.randomUUID().toString());
            StringBuilder query = new StringBuilder("INSERT INTO ");
            query.append(entity.getClass().getSimpleName());
            query.append(" VALUES");
            Map<String, Object> values = convertFieldsToMap(entity);

            String vals = values.entrySet().stream().map(entry -> {
                Class<?> entryClass = entry.getValue().getClass();
                if (entryClass.equals(String.class))
                    return "'" + entry.getValue() + "'";
                if (entryClass.equals(Date.class))
                    return "'" + new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(entry.getValue()) + "'";

                return entry.getValue().toString();

            }).collect(Collectors.joining(",", "(", ")"));
            query.append(vals);
            return query.toString();
        } catch (Exception ex) {
            throw new RuntimeException("Не удалось свормировать строку для вставки значения", ex);
        }

    }

    private Map<String, Object> convertFieldsToMap(T entity) throws IllegalAccessException {
        Map<String, Object> values = new HashMap<>();
        if(entity.getClass().getSuperclass() != null) {
            for (Field field : entity.getClass().getSuperclass().getDeclaredFields()) {
                field.setAccessible(true);
                values.put(field.getName(), field.get(entity));
                field.setAccessible(false);
            }
        }
        for (Field field : entity.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            values.put(field.getName(), field.get(entity));
            field.setAccessible(false);
        }
        return values;
    }

    @SuppressWarnings("unchecked")
    public String delete(T entity) {
        try {
            Field field = this.getIDField((Class<T>) entity.getClass());
            field.setAccessible(true);
            Object id = field.get(entity);
            field.setAccessible(false);
            if(id == null) {
                throw new Exception("Не найдено поле первичного ключа в сущности");
            }

            String outID = (id instanceof String) ? "'" + id + "'" : id.toString();

            return  """
                    DELETE FROM %s
                    WHERE %s=%s
                    """.formatted(entity.getClass().getSimpleName(), field.getName(), outID);

        } catch (Exception ex) {
            throw new RuntimeException("Не удалось сформировать запрос на удаление", ex);
        }
    }


}
