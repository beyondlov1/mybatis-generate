    <sql id="Base_Column_List">
        <#if idColumnName?? && idColumnName != ''>${idColumnName},</#if><#list normalColumns as column>${column.columnName}<#sep>, </#list>
    </sql>