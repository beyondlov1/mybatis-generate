package com.beyond.gen.freemarker;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chenshipeng
 * @date 2021/04/30
 */
@Data
public class JavaEntity4Gen {
    private String packageName;
    private List<String> imports = new ArrayList<String>();
    private String className;
    private List<FieldEntity4Gen> fields = new ArrayList<FieldEntity4Gen>();

    private String tableFullName;
    private FieldEntity4Gen id;

    @Data
    public static class FieldEntity4Gen {
        private String name;
        private String type;
        private String comment;
        private String columnConstName;
        private String columnName;
    }


}
