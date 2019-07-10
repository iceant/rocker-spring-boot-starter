package com.pointcx.rocker.spring.boot.starter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.Ordered;

@ConfigurationProperties(prefix = "spring.rocker")
public class RockerProperties {

    //////////////////////////////////////////////////////////////////////////
    //// 编译属性
    private String templateDirectory;
    private String outputDirectory;
    private String classDirectory;
    private boolean failOnError = true;
    private boolean skip = false;
    private String touchFile;
    private boolean skipTouch = false;
    private boolean addAsSources = true;
    private boolean addAsTestSources = false;
    private String javaVersion = "1.8";
    private boolean optimize = false;
    private String extendsClass;
    private String extendsModelClass;
    private boolean discardLogicWhitespace = false;
    private String targetCharset = "UTF-8";
    private String suffixRegex;

    //////////////////////////////////////////////////////////////////////////
    //// 运行属性
    private boolean enabled = true;
    private boolean reloading = true;
    private int templateResolverOrder = Ordered.LOWEST_PRECEDENCE - 10;
    private String prefix = "classpath:/templates/";
    private String suffix = ".rocker.html";
    private String contentType = "text/html; charset=utf-8";
    private boolean exposeRequestAttributes=false;
    private boolean allowRequestOverride=false;
    private boolean exposeSessionAttributes = false;
    private boolean allowSessionOverride=false;
    private boolean exposeSpringMacroHelpers=true; /*expose springMacroRequestContext to model*/

    //////////////////////////////////////////////////////////////////////////
    //// get/set


    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getTemplateDirectory() {
        return templateDirectory;
    }

    public void setTemplateDirectory(String templateDirectory) {
        this.templateDirectory = templateDirectory;
    }

    public String getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public String getClassDirectory() {
        return classDirectory;
    }

    public void setClassDirectory(String classDirectory) {
        this.classDirectory = classDirectory;
    }

    public boolean isFailOnError() {
        return failOnError;
    }

    public void setFailOnError(boolean failOnError) {
        this.failOnError = failOnError;
    }

    public boolean isSkip() {
        return skip;
    }

    public void setSkip(boolean skip) {
        this.skip = skip;
    }

    public String getTouchFile() {
        return touchFile;
    }

    public void setTouchFile(String touchFile) {
        this.touchFile = touchFile;
    }

    public boolean isSkipTouch() {
        return skipTouch;
    }

    public void setSkipTouch(boolean skipTouch) {
        this.skipTouch = skipTouch;
    }

    public boolean isAddAsSources() {
        return addAsSources;
    }

    public void setAddAsSources(boolean addAsSources) {
        this.addAsSources = addAsSources;
    }

    public boolean isAddAsTestSources() {
        return addAsTestSources;
    }

    public void setAddAsTestSources(boolean addAsTestSources) {
        this.addAsTestSources = addAsTestSources;
    }

    public String getJavaVersion() {
        return javaVersion;
    }

    public void setJavaVersion(String javaVersion) {
        this.javaVersion = javaVersion;
    }

    public boolean isOptimize() {
        return optimize;
    }

    public void setOptimize(boolean optimize) {
        this.optimize = optimize;
    }

    public String getExtendsClass() {
        return extendsClass;
    }

    public void setExtendsClass(String extendsClass) {
        this.extendsClass = extendsClass;
    }

    public String getExtendsModelClass() {
        return extendsModelClass;
    }

    public void setExtendsModelClass(String extendsModelClass) {
        this.extendsModelClass = extendsModelClass;
    }

    public boolean isDiscardLogicWhitespace() {
        return discardLogicWhitespace;
    }

    public void setDiscardLogicWhitespace(boolean discardLogicWhitespace) {
        this.discardLogicWhitespace = discardLogicWhitespace;
    }

    public String getTargetCharset() {
        return targetCharset;
    }

    public void setTargetCharset(String targetCharset) {
        this.targetCharset = targetCharset;
    }

    public String getSuffixRegex() {
        return suffixRegex;
    }

    public void setSuffixRegex(String suffixRegex) {
        this.suffixRegex = suffixRegex;
    }

    public boolean isReloading() {
        return reloading;
    }

    public void setReloading(boolean reloading) {
        this.reloading = reloading;
    }

    public int getTemplateResolverOrder() {
        return templateResolverOrder;
    }

    public void setTemplateResolverOrder(int templateResolverOrder) {
        this.templateResolverOrder = templateResolverOrder;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public boolean isExposeRequestAttributes() {
        return exposeRequestAttributes;
    }

    public void setExposeRequestAttributes(boolean exposeRequestAttributes) {
        this.exposeRequestAttributes = exposeRequestAttributes;
    }

    public boolean isExposeSessionAttributes() {
        return exposeSessionAttributes;
    }

    public void setExposeSessionAttributes(boolean exposeSessionAttributes) {
        this.exposeSessionAttributes = exposeSessionAttributes;
    }

    public boolean isExposeSpringMacroHelpers() {
        return exposeSpringMacroHelpers;
    }

    public void setExposeSpringMacroHelpers(boolean exposeSpringMacroHelpers) {
        this.exposeSpringMacroHelpers = exposeSpringMacroHelpers;
    }

    public boolean isAllowRequestOverride() {
        return allowRequestOverride;
    }

    public void setAllowRequestOverride(boolean allowRequestOverride) {
        this.allowRequestOverride = allowRequestOverride;
    }

    public boolean isAllowSessionOverride() {
        return allowSessionOverride;
    }

    public void setAllowSessionOverride(boolean allowSessionOverride) {
        this.allowSessionOverride = allowSessionOverride;
    }

    public RockerProperties clone(){
        RockerProperties properties = new RockerProperties();
        properties = new RockerProperties();
        properties.setPrefix(getPrefix());
        properties.setSuffix(getSuffix());
        properties.setAddAsSources(isAddAsSources());
        properties.setAddAsTestSources(isAddAsTestSources());
        properties.setAllowRequestOverride(isAllowRequestOverride());
        properties.setAllowSessionOverride(isAllowSessionOverride());
        properties.setClassDirectory(getClassDirectory());
        properties.setContentType(getContentType());
        properties.setDiscardLogicWhitespace(isDiscardLogicWhitespace());
        properties.setEnabled(isEnabled());
        properties.setExposeRequestAttributes(isExposeRequestAttributes());
        properties.setExposeSessionAttributes(isExposeSessionAttributes());
        properties.setExposeSpringMacroHelpers(isExposeSpringMacroHelpers());
        properties.setExtendsClass(getExtendsClass());
        properties.setExtendsModelClass(getExtendsModelClass());
        properties.setFailOnError(isFailOnError());
        properties.setJavaVersion(getJavaVersion());
        properties.setOptimize(isOptimize());
        properties.setOutputDirectory(getOutputDirectory());
        properties.setReloading(isReloading());
        properties.setSkip(isSkip());
        properties.setSkipTouch(isSkipTouch());
        properties.setTargetCharset(getTargetCharset());
        properties.setSuffixRegex(getSuffixRegex());
        properties.setTemplateDirectory(getTemplateDirectory());
        properties.setTemplateResolverOrder(getTemplateResolverOrder());
        properties.setTouchFile(getTouchFile());
        return properties;
    }
}
