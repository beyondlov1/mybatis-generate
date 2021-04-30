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
    /**
    * ${field.comment}
    */
    private ${field.type} ${field.name};

</#list>
}