package com.beyond.gen.freemarker;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chenshipeng
 * @date 2021/05/08
 */
@Data
public class MapperXmlEntity {
    private String entityClassFullName;
    private String mapperClassFullName;
    private String idColumnName;
    private String idPropertyName;
    private String idJdbcType;
    private List<ColumnEntity> normalColumns = new ArrayList<ColumnEntity>();

    @Data
    public static class ColumnEntity {

        private String columnName;
        private String propertyName;
        private String jdbcType;

    }
}
