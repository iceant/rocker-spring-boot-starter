package rocker;

import com.fizzed.rocker.BindableRockerModel;
import com.pointcx.rocker.spring.boot.starter.SpringRocker;
import com.pointcx.rocker.spring.boot.starter.web.RockerViewResolver;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class $r implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        $r.applicationContext = applicationContext;
    }

    //////////////////////////////////////////////////////////////////////////
    //// spring utils
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static Object getBean(Class clazz){
        return applicationContext.getBean(clazz);
    }

    public static Object getBean(Class clss, Object[] args){
        return applicationContext.getBean(clss, args);
    }

    public static Object getBean(String name){
        return applicationContext.getBean(name);
    }

    public static Object getBean(String name, Object[] args){
        return applicationContext.getBean(name, args);
    }

    public static String[] getBeanNames(){
        return applicationContext.getBeanDefinitionNames();
    }

    public static String getMessage(String code){
        return applicationContext.getMessage(code, null, LocaleContextHolder.getLocale());
    }

    public static String getMessage(String code, Object[] args){
        return applicationContext.getMessage(code, args, LocaleContextHolder.getLocale());
    }

    public static String[] getActivedProfiles(){
        return applicationContext.getEnvironment().getActiveProfiles();
    }

    public static boolean isProfileActived(String name){
        for(String activedProfile : applicationContext.getEnvironment().getActiveProfiles()){
            if(activedProfile.equalsIgnoreCase(name)) return true;
        }
        return false;
    }

    public static String getProperty(String name, String defaultValue) {
        return applicationContext.getEnvironment().getProperty(name, defaultValue);
    }

    public static String getProperty(String name) {
        return applicationContext.getEnvironment().getProperty(name);
    }
    //////////////////////////////////////////////////////////////////////////
    ////

    public static HttpServletRequest request(){
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if(requestAttributes instanceof ServletRequestAttributes){
            return ((ServletRequestAttributes) requestAttributes).getRequest();
        }
        return null;
    }

    public static List<String> attrNames() {
        HttpServletRequest request = request();
        if(request==null){
            return Collections.EMPTY_LIST;
        }
        Enumeration<String> names = request.getAttributeNames();
        List<String> result = new ArrayList<>();
        for (; names.hasMoreElements(); ) {
            result.add(names.nextElement());
        }
        return result;
    }

    public static Object attr(String name, Object defaultValue){
        HttpServletRequest request = request();
        if(request==null){
            return defaultValue;
        }
        Object value = request.getAttribute(name);
        if(value==null) return defaultValue;
        return value;
    }

    public static Object attr(String name){
        return attr(name, null);
    }

    public static Integer attrInt(String name, Integer defaultValue){
        return (Integer) attr(name, defaultValue);
    }

    public static Integer attrInt(String name){
        return (Integer) attr(name);
    }

    public static Float attrFloat(String name, Float value){
        return (Float) attr(name, value);
    }

    public static Float attrFloat(String name){
        return (Float) attr(name);
    }

    public static Long attrLong(String name, Long defaultValue){
        return (Long) attr(name, defaultValue);
    }

    public static Long attrLong(String name){
        return (Long) attr(name);
    }

    public static Double attrDouble(String name, Double value){
        return (Double) attr(name, value);
    }

    public static Double attrDoulbe(String name){
        return (Double) attr(name);
    }

    public static Date attrDate(String name, Date defaultValue){
        return (Date) attr(name, defaultValue);
    }

    public static Date attrDate(String name){
        return (Date) attr(name);
    }

    public static String attrString(String name, String defaultValue){
        return (String) attr(name, defaultValue);
    }

    public static String attrString(String name){
        return (String) attr(name);
    }

    public static Boolean attrBoolean(String name, Boolean defaultValue){
        return (Boolean) attr(name, defaultValue);
    }

    public static Boolean attrBoolean(String name){
        return (Boolean) attr(name);
    }

    public static String path(String url) {
        if (url == null || url.length() < 1) return "";
        HttpServletRequest request = request();
        if(request==null) return url;
        String contextPath = request.getContextPath();
        contextPath = contextPath.endsWith("/") ? contextPath : contextPath + "/";
        url = url.startsWith("/") ? url.substring(1) : url;
        StringBuilder sb = new StringBuilder();
        sb.append(contextPath).append(url);
        return sb.toString();
    }
    // -----------------------------------------------------------------------
    public static String msg(String code){
        return getMessage(code);
    }

    public static String msg(String code, Object ... args){
        return getMessage(code, args);
    }
    // -----------------------------------------------------------------------
    public static String prop(String name){
        return getProperty(name);
    }

    public static String prop(String name, String defaultValue){
        return getProperty(name, defaultValue);
    }
    // -----------------------------------------------------------------------

    public static Object bean(String name){
        return applicationContext.getBean(name);
    }

    public static Object bean(String name, Object ... args){
        return applicationContext.getBean(name, args);
    }

    public static Object bean(Class cls){
        return applicationContext.getBean(cls);
    }

    public static Object bean(Class cls, Object ... args){
        return applicationContext.getBean(cls, args);
    }

    // -----------------------------------------------------------------------
    public static String ip() {
        HttpServletRequest request = request();
        if(request==null) return "127.0.0.1";

        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
        } else if (ip.length() > 15) {
            String[] ips = ip.split(",");
            for (int index = 0; index < ips.length; index++) {
                String strIp = (String) ips[index];
                if (!("unknown".equalsIgnoreCase(strIp))) {
                    ip = strIp;
                    break;
                }
            }
        }
        return ip;
    }

    // -----------------------------------------------------------------------
    public static String paramString(String name, String defaultValue) {
        String value = request().getParameter(name);
        if (value == null) return defaultValue;
        return value;
    }

    public static String paramString(String name) {
        return request().getParameter(name);
    }

    public static Boolean paramBoolean(String name, Boolean defaultValue){
        String value = request().getParameter(name);
        if(value==null) return defaultValue;
        return Boolean.parseBoolean(value);
    }

    public static Boolean paramBoolean(String name){
        return paramBoolean(name, null);
    }

    public static Integer paramInt(String name, Integer defaultValue) {
        String value = request().getParameter(name);
        if (value == null) return defaultValue;
        return Integer.parseInt(value);
    }

    public static Integer paramInt(String name) {
        String value = request().getParameter(name);
        if (value == null) return null;
        return Integer.parseInt(value);
    }

    public static Long paramLong(String name, Long defaultValue) {
        String value = request().getParameter(name);
        if (value == null) return defaultValue;
        return Long.parseLong(value);
    }

    public static Long paramLong(String name) {
        String value = request().getParameter(name);
        if (value == null) return null;
        return Long.parseLong(value);
    }

    public static Float paramFloat(String name, Float defaultValue) {
        String value = request().getParameter(name);
        if (value == null) return defaultValue;
        return Float.parseFloat(value);
    }

    public static Float paramFloat(String name) {
        String value = request().getParameter(name);
        if (value == null) return null;
        return Float.parseFloat(value);
    }

    public static Double paramDouble(String name, Double defaultValue) {
        String value = request().getParameter(name);
        if (value == null) return defaultValue;
        return Double.parseDouble(value);
    }

    public static Double paramDouble(String name) {
        String value = request().getParameter(name);
        if (value == null) return null;
        return Double.parseDouble(value);
    }

    public static Date paramDate(String name, String fmt, Date defaultValue) {
        String value = request().getParameter(name);
        if (value == null) return defaultValue;
        try {
            return new SimpleDateFormat(fmt).parse(value);
        } catch (ParseException e) {
            return defaultValue;
        }
    }

    public static Date paramDate(String name, String fmt) {
        return paramDate(name, fmt, null);
    }


    static final SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyy-MM-dd");
    static final SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static Date paramDate(String name, Date defaultValue) {
        String value = request().getParameter(name);
        if (value == null) return defaultValue;
        try {
            return yyyyMMdd.parse(value);
        } catch (ParseException e) {
            return defaultValue;
        }
    }

    public static Date paramDate(String name) {
        return paramDate(name, (Date) null);
    }

    public static Date paramDateTime(String name, Date defaultValue) {
        String value = request().getParameter(name);
        if (value == null) return defaultValue;
        try {
            return yyyyMMddHHmmss.parse(value);
        } catch (ParseException e) {
            return defaultValue;
        }
    }

    public static Date paramDateTime(String name) {
        return paramDateTime(name, (Date) null);
    }

    public static String formatDate(Date date) {
        return yyyyMMdd.format(date);
    }

    public static String formatDateTime(Date date) {
        return yyyyMMddHHmmss.format(date);
    }

    public static String formatDate(Date date, String fmt) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(fmt);
        return simpleDateFormat.format(date);
    }

    public static Date parseDate(String dateValue, String fmt){
        SimpleDateFormat dateFormat = new SimpleDateFormat(fmt);
        try {
            return dateFormat.parse(dateValue);
        } catch (ParseException e) {
        }
        return null;
    }

    public static Date parseDate(String dateValue, String fmt, Date defaultValue){
        SimpleDateFormat dateFormat = new SimpleDateFormat(fmt);
        try {
            return dateFormat.parse(dateValue);
        } catch (ParseException e) {
        }
        return defaultValue;
    }
    // -----------------------------------------------------------------------
    //////////////////////////////////////////////////////////////////////////
    //// CSRF Helper
    public static CsrfToken csrfToken() {
        CsrfToken token = new HttpSessionCsrfTokenRepository().loadToken(request());
        if (token == null) {
            token = (CsrfToken) attr("_csrf");
        }
        return token;
    }

    public static String csrf() {
        CsrfToken token = csrfToken();
        if (token == null) return "";
        return token.getToken();
    }

    public static String csrfTokenHeaderName() {
        CsrfToken token = csrfToken();
        if (token == null) return "";
        return token.getHeaderName();
    }

    public static String csrfTokenParameterName() {
        CsrfToken token = csrfToken();
        if (token == null) return "";
        return token.getParameterName();
    }
    // -----------------------------------------------------------------------
    public static Object field(String name, String field, Object defaultValue) {
        Object object = attr(name);
        if (object == null) return null;
        Class objectClass = object.getClass();
        try {
            Field objectField = objectClass.getField(field);
            objectField.setAccessible(true);
            return objectField.get(object);
        } catch (NoSuchFieldException e) {
        }catch (IllegalAccessException e) {
        }

        try {
            String methodName = "get" + field.substring(0, 1).toUpperCase() + field.substring(1);
            Method method = objectClass.getMethod(methodName);
            method.setAccessible(true);
            return method.invoke(object);
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        } catch (NoSuchMethodException e) {
        }

        return defaultValue;
    }

    public static Object field(String key, Object defaultValue){
        int pos = key.indexOf(".");
        if (pos != -1) {
            String objectName = key.substring(0, pos);
            String fieldName = key.substring(pos + 1);
            return field(objectName, fieldName, defaultValue);
        }
        return defaultValue;
    }

    /* 以 objectName.fieldName 格式获取 request.attribute 中的对象属性 */
    public static Object field(String key) {
        int pos = key.indexOf(".");
        if (pos != -1) {
            String objectName = key.substring(0, pos);
            String fieldName = key.substring(pos + 1);
            return field(objectName, fieldName);
        }
        return null;
    }

    public static String fieldString(String key, String defaultValue){
        return (String) field(key, defaultValue);
    }

    public static String fieldString(String key){
        return (String) field(key);
    }

    public static Integer fieldInt(String key, Integer defaultValue){
        return (Integer) field(key, defaultValue);
    }

    public static Integer fieldInt(String key){
        return (Integer) field(key);
    }

    public static Float fieldFloat(String key, Float defaultValue){
        return (Float) field(key, defaultValue);
    }

    public static Float fieldFloat(String key){
        return (Float) field(key);
    }

    public static Long fieldLong(String key, Long defaultValue){
        return (Long) field(key, defaultValue);
    }

    public static Long fieldLong(String key){
        return (Long) field(key);
    }

    public static Double fieldDouble(String key, Double defaultValue){
        return (Double) field(key, defaultValue);
    }

    public static Double fieldDouble(String key){
        return (Double) field(key);
    }

    public static Date fieldDate(String key, Date defaultValue){
        return (Date) field(key, defaultValue);
    }

    public static Date fieldDate(String key){
        return (Date) field(key);
    }

    public static Boolean fieldBoolean(String key, Boolean defaultValue){
        return (Boolean) field(key, defaultValue);
    }

    public static Boolean fieldBoolean(String key){
        return (Boolean) field(key);
    }

    // -----------------------------------------------------------------------
    public static <T> T choose(boolean condition, T trueValue, T falseValue){
        return condition?trueValue:falseValue;
    }

    //////////////////////////////////////////////////////////////////////////
    ////
    static SpringRocker springRocker = null;

    public static BindableRockerModel template(String templatePath){
        if(springRocker==null) springRocker = (SpringRocker) getBean(SpringRocker.class);
        if(springRocker!=null){
            return springRocker.template(templatePath);
        }
        return null;
    }

    public static BindableRockerModel template(String templatePath, Object ... arguments){
        if(springRocker==null) springRocker = (SpringRocker) getBean(SpringRocker.class);
        if(springRocker!=null){
            return springRocker.template(templatePath, arguments);
        }
        return null;
    }
}
