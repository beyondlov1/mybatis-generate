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
    private String packageName;
    private List<String> imports = new ArrayList<String>();
    private String className;
    private List<FieldEntity> fields = new ArrayList<FieldEntity>();

    private String tableFullName;
    private FieldEntity id = new FieldEntity();


    @Data
    public static class FieldEntity {
        private String name;
        private String type;
        private String comment;
    }

    public JavaEntity4Gen toGen(boolean needConst){
        JavaEntity4Gen javaEntity4Gen = new JavaEntity4Gen();
        javaEntity4Gen.setPackageName(packageName);
        javaEntity4Gen.setImports(imports);
        javaEntity4Gen.setClassName(className);

        List<JavaEntity4Gen.FieldEntity4Gen> fieldEntity4Gens = new ArrayList<JavaEntity4Gen.FieldEntity4Gen>();
        for (FieldEntity field : fields) {
            JavaEntity4Gen.FieldEntity4Gen fieldEntity4Gen = new JavaEntity4Gen.FieldEntity4Gen();
            fieldEntity4Gen.setName(field.getName());
            fieldEntity4Gen.setType(field.getType());
            fieldEntity4Gen.setComment(field.getComment());
            if (needConst){
                fieldEntity4Gen.setColumnConstName(("COL_"+StringUtils.humpToLine(field.getName())).toUpperCase());
            }
            fieldEntity4Gen.setColumnName(StringUtils.humpToLine(field.getName()));
            fieldEntity4Gens.add(fieldEntity4Gen);
        }
        javaEntity4Gen.setFields(fieldEntity4Gens);

        if (id != null){
            JavaEntity4Gen.FieldEntity4Gen id4Gen = new JavaEntity4Gen.FieldEntity4Gen();
            id4Gen.setName(id.getName());
            id4Gen.setType(id.getType());
            id4Gen.setComment(id.getComment());
            if (needConst){
                id4Gen.setColumnConstName(("COL_"+StringUtils.humpToLine(id.getName())).toUpperCase());
            }
            id4Gen.setColumnName(StringUtils.humpToLine(id.getName()));
            javaEntity4Gen.setId(id4Gen);
        }
        javaEntity4Gen.setTableFullName(tableFullName);
        return javaEntity4Gen;
    }

}
