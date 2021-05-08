package ${packageName};

<#list imports as import>
import ${import};
</#list>

/**
* generated
*/
@Data
public class ${className} {

<#list fields as field>

    <#if field.comment?? && field.comment != ''>
    /**
    * ${field.comment}
    */
    </#if>
    private ${field.type} ${field.name};

</#list>

<#list fields as field>
    <#if field.columnConstName?? && field.columnConstName != ''>
    public static final String ${field.columnConstName} = "${field.columnName}";
    </#if>
</#list>
}