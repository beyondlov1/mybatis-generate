package com.beyond.gen.freemarker;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chenshipeng
 * @date 2021/05/08
 */
@Data
public class MapperEntity {
    private String packageName;
    private List<String> imports = new ArrayList<String>();
    private String mapperName;
    private String superMapperName;
    private String entityName;
    private String tableFullName;
    private List<MethodEntity> methods = new ArrayList<MethodEntity>();

    @Data
    public static class MethodEntity {
        private String name;
        private String returnClassName;
        private List<ParameterEntity> parameters = new ArrayList<ParameterEntity>();

        @Data
        public static class ParameterEntity {
            private String className;
            private String parameterName;
        }
    }
}
