package com.lemon.pojo;

import cn.afterturn.easypoi.excel.annotation.Excel;

/**
 * excel表格映射类要求：必须有私有属性，必须有get/set，必须有空参构造（默认可以不写）
 */
public class CaseInfo {
    //@Excel:用来excel列和java类映射关系
    @Excel(name = "用例编号")
    private int id;
    @Excel(name = "接口名称")
    private String name;
    @Excel(name = "请求方式")
    private String method;
    @Excel(name = "url")
    private String url;
    //不映射 Desc(用例描述)
    @Excel(name = "参数")
    private String params;
    @Excel(name = "参数类型")
    private String contentType;
    @Excel(name = "期望结果")
    private String expectedResult;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getExpectedResult() {
        return expectedResult;
    }

    public void setExpectedResult(String expectedResult) {
        this.expectedResult = expectedResult;
    }


    @Override
    public String toString() {
        return "CaseInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", method='" + method + '\'' +
                ", url='" + url + '\'' +
                ", params='" + params + '\'' +
                ", contentType='" + contentType + '\'' +
                ", expectedResult='" + expectedResult + '\'' +
                                '}';
    }
}
