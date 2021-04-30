package com.beyond.gen.freemarker;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chenshipeng
 * @date 2021/04/30
 */
@Data
public class JavaEntity {
    private String className;
    private List<String> imports = new ArrayList<String>();
    private List<FieldEntity> fields = new ArrayList<FieldEntity>();
    private String packageName;


    @Data
    public static class FieldEntity {
        private String name;
        private String type;
        private String comment;
    }


}
