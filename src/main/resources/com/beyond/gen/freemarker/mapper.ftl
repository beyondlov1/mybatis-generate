package ${packageName};

<#list imports as import>
import ${import};
</#list>

/**
* generated
*/
@Mapper
public interface ${mapperName} <#if superMapperName?? && superMapperName != ''>extends ${superMapperName} </#if>{

<#list methods as method>

    ${method.returnClassName} ${method.name}(<#list method.parameters as parameter>${parameter.className} ${parameter.parameterName}</#list>);

</#list>
}