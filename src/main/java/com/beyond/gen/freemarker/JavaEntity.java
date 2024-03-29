package com.beyond.gen.freemarker;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

;

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
    private FieldEntity id;


    @Data
    public static class FieldEntity {
        private String name;
        private String type;
        private String comment;
        private String valueStr;
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
                fieldEntity4Gen.setColumnConstName(("COL_"+ StringUtil.humpToLine(field.getName())).toUpperCase());
            }
            fieldEntity4Gen.setColumnName(StringUtil.humpToLine(field.getName()));
            fieldEntity4Gen.setColumnDefault(getColumnDefaultFromValueStr(field.valueStr, field.type));
            fieldEntity4Gens.add(fieldEntity4Gen);
        }
        javaEntity4Gen.setFields(fieldEntity4Gens);

        if (id != null){
            JavaEntity4Gen.FieldEntity4Gen id4Gen = new JavaEntity4Gen.FieldEntity4Gen();
            id4Gen.setName(id.getName());
            id4Gen.setType(id.getType());
            id4Gen.setComment(id.getComment());
            if (needConst){
                id4Gen.setColumnConstName(("COL_"+ StringUtil.humpToLine(id.getName())).toUpperCase());
            }
            id4Gen.setColumnName(StringUtil.humpToLine(id.getName()));
            javaEntity4Gen.setId(id4Gen);
        }
        javaEntity4Gen.setTableFullName(tableFullName);
        return javaEntity4Gen;
    }

    private String getColumnDefaultFromValueStr(String valueStr, String type){
        if (valueStr == null){
            return null;
        }
        if (StringUtils.equals(type, "BigDecimal")){
            if (new BigDecimal(valueStr).compareTo(BigDecimal.ZERO) == 0){
                return "BigDecimal.ZERO";
            }
            return String.format("BigDecimal.valueOf(%s)", valueStr);
        }
        if (StringUtils.equals(type, "String")){
            return String.format("\"%s\"", valueStr);
        }
        if (StringUtils.equals(type, "Date")){
            return null;
        }
        if (StringUtils.equals(type, "LocalDateTime")){
            return null;
        }
        return valueStr;
    }

}
