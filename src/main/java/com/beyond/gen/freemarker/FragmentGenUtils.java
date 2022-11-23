package com.beyond.gen.freemarker;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author chenshipeng
 * @date 2022/11/08
 */
public class FragmentGenUtils {

    /**
     * SEP_RE_LIST = ["(.*[a-z])By([A-Z].*)", "(.*[a-z])And([A-Z].*)", "(.*[a-z])ForUpdate(.*)"]
     * SEP_RE = "(.*[a-z])By([A-Z].*)"
     *
     * def msplit(regxs:list, s, list):
     *     if not s:
     *         return
     *     splitted = False
     *     for regx in regxs:
     *         group = re.match(regx, s)
     *         if group:
     *             msplit(regxs, group[1], list)
     *             msplit(regxs, group[2], list)
     *             splitted = True
     *             break
     *     if not splitted:
     *         list.append(s)
     */
    private static String[] SEP_RE_LIST = {"(.*[a-z])By([A-Z].*)", "(.*[a-z])And([A-Z].*)", "(.*[a-z])ForUpdate(.*)"};


    public static String createParamFragment(String methodName){
        if (StringUtils.isBlank(methodName) || !methodName.startsWith("get")){
            return null;
        }
        List<String> fields = new ArrayList<>();
        msplit(SEP_RE_LIST, methodName, fields);
        fields.remove("get");
        fields.remove("all");
        fields.remove("getAll");
        return String.format("(%s)",  generateParams(fields));
    }


    private static String generateParams(List<String> fields){
        List<String> conditions = new ArrayList<String>();
        for (String field : fields) {
            String col;
            if(field.endsWith("s") || field.endsWith("List")){
                conditions.add(String.format("@Param(\"%s\") List<Integer> %s", field, field));
            }else {
                conditions.add(String.format("@Param(\"%s\") Integer  %s", field, field));
            }
        }
        return String.join(", ",conditions);
    }

    public static String createXmlFragment(String methodName, String fullTableName){
        if (StringUtils.isBlank(methodName) || !methodName.startsWith("get")){
            return null;
        }
        List<String> fields = new ArrayList<>();
        msplit(SEP_RE_LIST, methodName, fields);
        fields.remove("get");
        fields.remove("all");
        fields.remove("getAll");
        return String.format("    <select id=\"%s\" resultMap=\"BaseResultMap\">\n        %s \n        where %s \n    </select>", methodName, generateSelect(fullTableName), generateWhere(fields));
    }

    private static String generateWhere(List<String> fields){
        List<String> conditions = new ArrayList<String>();
        for (String field : fields) {
            String col;
            if(field.endsWith("s") || field.endsWith("List")){
                if (field.endsWith("s")) {
                    col = StringUtils.substringBeforeLast(com.beyond.gen.freemarker.StringUtils.humpToLine(field), "s");
                }else{
                    col = StringUtils.substringBeforeLast(com.beyond.gen.freemarker.StringUtils.humpToLine(field), "_list");
                }
                conditions.add(String.format("%s in\n" +
                        "              <foreach collection=\"%s\" item=\"item\" separator=\",\" open=\"(\" close=\")\">\n" +
                        "                  #{item}\n" +
                        "              </foreach>", col, field));
            }else {
                col = com.beyond.gen.freemarker.StringUtils.humpToLine(field);
                conditions.add(String.format("%s = #{%s}", col, field));
            }

        }
        return String.join(" and ",conditions);
    }

    private static String generateSelect(String fullTableName){
        return String.format("select <include refid=\"Base_Column_List\"/> from %s", fullTableName);
    }

    private static void msplit(String[] regxs, String s, List<String> list){
        if (StringUtils.isBlank(s)) return;
        boolean splitted = false;
        for (String regx : regxs) {
            Pattern pattern = Pattern.compile(regx);
            Matcher matcher = pattern.matcher(s);
            if (matcher.matches()){
                msplit(regxs,matcher.group(1), list);
                msplit(regxs,matcher.group(2), list);
                splitted = true;
                break;
            }
        }
        if (!splitted){
            list.add(com.beyond.gen.freemarker.StringUtils.deCapitalize(s));
        }
    }


    public static String createXmlColumnList(MapperXmlEntity mapperXmlEntity){
        String templateName = "columnlist.ftl";
        return fromTemplate(templateName, mapperXmlEntity);
    }

    public static String createXmlResultMap(MapperXmlEntity mapperXmlEntity){
        String templateName = "resultmap.ftl";
        return fromTemplate(templateName, mapperXmlEntity);
    }

    private static String fromTemplate(String templateName, Object data){
        String templatePath = "";
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_28);
        configuration.setDefaultEncoding("UTF-8");
        try (StringWriter out = new StringWriter()){
            //ftl模板文件统一放至 com.lun.template 包下面
            configuration.setClassForTemplateLoading(FreeMarkerWriter.class, templatePath);
            Template template = configuration.getTemplate(templateName);
            //生成文件
            template.process(data, out);
            return out.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
