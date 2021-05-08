package com.beyond.gen.freemarker;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class FreeMarkerWriterTest {

    @Test
    public void write() {
        FreeMarkerWriter freeMarkerWriter = new FreeMarkerWriter("", "entity.ftl", "/home/beyond/tmp", "test.java");
        JavaEntity javaEntity = new JavaEntity();

        javaEntity.setClassName("Book");
        javaEntity.setPackageName("com.beyond");

        List<String> imports = new ArrayList<String>();
        imports.add("lombok.Data");
        javaEntity.setImports(imports);

        List<JavaEntity.FieldEntity> fields = new ArrayList<JavaEntity.FieldEntity>();
        JavaEntity.FieldEntity field = new JavaEntity.FieldEntity();
        field.setName("id");
        field.setType("Integer");
        field.setComment("");
        fields.add(field);
        javaEntity.setFields(fields);

        freeMarkerWriter.write(javaEntity.toGen(true));
    }


    @Test
    public void write1() {
        FreeMarkerWriter freeMarkerWriter = new FreeMarkerWriter("", "mapper.ftl", "/home/beyond/tmp", "TestMapper.java");
        MapperEntity mapperEntity = new MapperEntity();

        mapperEntity.setMapperName("BookMapper");
        mapperEntity.setPackageName("com.beyond");

        List<String> imports = new ArrayList<String>();
        imports.add("org.apache.ibatis.annotations.Mapper");
        mapperEntity.setImports(imports);
//        mapperEntity.setSuperMapperName("CustomMapper");
        freeMarkerWriter.write(mapperEntity);
    }


    @Test
    public void write2() {
        FreeMarkerWriter freeMarkerWriter = new FreeMarkerWriter("", "mapperxml.ftl", "/home/beyond/tmp", "TestMapper.xml");
        MapperXmlEntity mapperXmlEntity = new MapperXmlEntity();
        mapperXmlEntity.setEntityClassFullName("com.beyond.Book");
        mapperXmlEntity.setMapperClassFullName("com.beyond.BookMapper");
        mapperXmlEntity.setIdColumnName("id");
        mapperXmlEntity.setIdPropertyName("id");
        mapperXmlEntity.setIdJdbcType("INTEGER");
        List<MapperXmlEntity.ColumnEntity> columns = new ArrayList<MapperXmlEntity.ColumnEntity>();
        MapperXmlEntity.ColumnEntity column = new MapperXmlEntity.ColumnEntity();
        column.setColumnName("user_name");
        column.setPropertyName("userName");
        column.setJdbcType("VARCHAR");

        columns.add(column);
        mapperXmlEntity.setNormalColumns(columns);

        freeMarkerWriter.write(mapperXmlEntity);
    }

}