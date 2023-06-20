package com.beyond.gen.freemarker;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.lang3.StringUtils;
import rita.RiTa;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private static Pattern[] SEP_RE_PATTERN_LIST = {Pattern.compile("(.*[a-z])By([A-Z].*)"), Pattern.compile("(.*[a-z])And([A-Z].*)"), Pattern.compile("(.*[a-z])ForUpdate(.*)")};


    public static String createParamFragment(String methodName){
        if (StringUtils.isBlank(methodName) || !methodName.startsWith("get")){
            return null;
        }
        List<String> fields = new ArrayList<>();
        msplit(SEP_RE_PATTERN_LIST, methodName, fields);
        fields.remove("get");
        fields.remove("all");
        fields.remove("getAll");
        return String.format("(%s)",  generateParams(fields));
    }


    public static String generateParams(List<String> fields){
        List<String> conditions = new ArrayList<String>();
        for (String field : fields) {
            String col;
            if(field.endsWith("List") || isPlurality(field)){
                conditions.add(String.format("@Param(\"%s\") List<Integer> %s", field, field));
            }else {
                conditions.add(String.format("@Param(\"%s\") Integer %s", field, field));
            }
        }
        return String.join(", ",conditions);
    }

    public static String createXmlFragment(String methodName, String fullTableName){
        if (StringUtils.isBlank(methodName) || !methodName.startsWith("get")){
            return null;
        }
        List<String> fields = new ArrayList<>();
        msplit(SEP_RE_PATTERN_LIST, methodName, fields);
        fields.remove("get");
        fields.remove("all");
        fields.remove("getAll");
        return String.format("    <select id=\"%s\" resultMap=\"BaseResultMap\">\n        %s \n        where %s \n    </select>", methodName, generateSelect(fullTableName), generateWhere(fields));
    }


    public static String createXmlFragmentFromSql(String mybatisSql,String methodName, String resultType){
        return String.format("    <select id=\"%s\" resultType=\"%s\">\n        %s \n    </select>", methodName, resultType, mybatisSql);
    }

    public static String parseAndGenerateWhere(String methodName){
        List<String> fields = new ArrayList<>();
        methodName = StringUtils.substringAfter(methodName, "By");
        msplit(SEP_RE_PATTERN_LIST, methodName, fields);
        return generateWhere(fields);
    }

    public static Map<String,Object> parseAndGenerateWhereMap(String methodName){
        List<String> fields = new ArrayList<>();
        methodName = StringUtils.substringAfter(methodName, "By");
        msplit(SEP_RE_PATTERN_LIST, methodName, fields);
        return generateWhereMap(fields);
    }

    public static Map<String,Object> generateWhereMap(List<String> fields){
        Map<String, Object> map = new HashMap<>();
        List<Map<String, Object>> conditionMaps = new ArrayList<Map<String, Object>>();
        List<String> conditions = new ArrayList<String>();
        for (String field : fields) {
            Map<String, Object> conditionMap = new HashMap<>();
            String col;
            if(field.endsWith("List") || isPlurality(field)){
                if (field.endsWith("List")) {
                    col = StringUtils.substringBeforeLast(StringUtil.humpToLine(field), "_list");
                }else{
                    col = StringUtil.humpToLine(singularize(field));
                }
                conditions.add(String.format("%s in\n" +
                        "              <foreach collection=\"%s\" item=\"item\" separator=\",\" open=\"(\" close=\")\">\n" +
                        "                  #{item}\n" +
                        "              </foreach>", col, field));
                conditionMap.put("isList", true);
                conditionMap.put("col", col);
                conditionMap.put("field", field);
            }else {
                col = StringUtil.humpToLine(field);
                conditions.add(String.format("%s = #{%s}", col, field));
                conditionMap.put("isList", false);
                conditionMap.put("col", col);
                conditionMap.put("field", field);
            }
            conditionMaps.add(conditionMap);
        }
        map.put("where", String.join(" and ",conditions)) ;
        map.put("conditions", conditionMaps) ;
        return map;
    }


    private static String generateWhere(List<String> fields){
        List<String> conditions = new ArrayList<String>();
        for (String field : fields) {
            String col;
            if(field.endsWith("List") || isPlurality(field)){
                if (field.endsWith("List")) {
                    col = StringUtils.substringBeforeLast(StringUtil.humpToLine(field), "_list");
                }else{
                    col = StringUtil.humpToLine(singularize(field));
                }
                conditions.add(String.format("%s in\n" +
                        "              <foreach collection=\"%s\" item=\"item\" separator=\",\" open=\"(\" close=\")\">\n" +
                        "                  #{item}\n" +
                        "              </foreach>", col, field));
            }else {
                col = StringUtil.humpToLine(field);
                conditions.add(String.format("%s = #{%s}", col, field));
            }

        }
        return String.join(" and ",conditions);
    }

    private static String singularize(String word){
        if (StringUtils.isBlank(word)) return word;
        word = word.trim();
        int subindex = getLastCapIndex(word);
        String lastCapWord = word.substring(subindex).toLowerCase();
        if (subindex == 0){
            return RiTa.singularize(lastCapWord);
        }
        return word.substring(0, subindex) + StringUtils.capitalize(RiTa.singularize(lastCapWord));
    }

    /**
     * 'people' 这种会标记为 nn, 不是负数, 但是 RiTa.singularize 可以变成单数 'person', 可能是个bug, 但是感觉不重要, 暂时不改
     * PS: https://github.com/generativa/RiTa  这个库貌似可以识别
     */
    private static boolean isPlurality(String word){
        if (StringUtils.isBlank(word)) return false;
        word = word.trim();
        int subindex = getLastCapIndex(word);
        word = word.substring(subindex).toLowerCase();
        Map<String, String> result = RiTa.analyze(word);
        if (Arrays.asList("nns","nnps").contains(result.get("pos"))){
            return true;
        }
        return false;
    }

    /**
     * 找最后一个大写单词
     * @return
     */
    private static int getLastCapIndex(String word){
        char[] chars = word.toCharArray();
        int i = chars.length - 1;
        for (; i >= 0; i--) {
            if (i == 0) break;
            if (Character.isUpperCase(chars[i])){
                break;
            }
        }
        return i;
    }

    private static String generateSelect(String fullTableName){
        return String.format("select <include refid=\"Base_Column_List\"/> from %s", fullTableName);
    }

    private static void msplit(Pattern[] regxs, String s, List<String> list){
        if (StringUtils.isBlank(s)) return;
        boolean splitted = false;
        for (Pattern regx : regxs) {
            Matcher matcher = regx.matcher(s);
            if (matcher.matches()){
                msplit(regxs,matcher.group(1), list);
                msplit(regxs,matcher.group(2), list);
                splitted = true;
                break;
            }
        }
        if (!splitted){
            list.add(StringUtil.deCapitalize(s));
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

    public static String fromTemplate(String templateName, Object data){
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
