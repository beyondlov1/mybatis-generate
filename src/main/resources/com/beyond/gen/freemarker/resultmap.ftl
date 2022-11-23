    <resultMap id="BaseResultMap" type="${entityClassFullName}">
        <#if idColumnName?? && idColumnName != ''>
            <id column="${idColumnName}" jdbcType="${idJdbcType}" property="${idPropertyName}" />
        </#if>
        <#list normalColumns as column>
            <result column="${column.columnName}" jdbcType="${column.jdbcType}" property="${column.propertyName}" />
        </#list>
    </resultMap>
