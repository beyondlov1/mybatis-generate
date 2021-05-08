<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="${mapperClassFullName}">

    <resultMap id="BaseResultMap" type="${entityClassFullName}">
        <#if idColumnName?? && idColumnName != ''>
        <id column="${idColumnName}" jdbcType="${idJdbcType}" property="${idPropertyName}" />
        </#if>
        <#list normalColumns as column>
        <result column="${column.columnName}" jdbcType="${column.jdbcType}" property="${column.propertyName}" />
        </#list>
    </resultMap>

    <sql id="Base_Column_List">
        <#if idColumnName?? && idColumnName != ''>${idColumnName},</#if><#list normalColumns as column>${column.columnName}<#sep>, </#list>
    </sql>
</mapper>