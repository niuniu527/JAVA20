package com.lemon.cases;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.lemon.pojo.CaseInfo;
import com.lemon.pojo.WriteBackData;
import com.lemon.utils.ExcelUtils;
import com.lemon.utils.HttpUtils;
import com.lemon.utils.UserData;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BaseCase {
    //添加日志，并日志打印输出
    private static Logger logger= Logger.getLogger(BaseCase.class);
    //获取testng.xml中sheetIndex
    public int sheetIndex;

    @BeforeClass
    @Parameters({"sheetIndex"})
    public void beforeClass(int sheetIndex) {
        this.sheetIndex = sheetIndex;
    }

    @AfterSuite
    public void finish() throws Exception {
        ExcelUtils.batchWrite();
    }


    /**
     * 添加回写对象到集合中
     * @param sheetIndex
     * @param rowNum
     * @param cellNum
     * @param content
     */
    public void addWriteBackData(int sheetIndex, int rowNum, int cellNum, String content) {
        //响应回写
        WriteBackData wbd = new WriteBackData(sheetIndex, rowNum, cellNum, content);
        //批量回写，存储到一个List集合
        ExcelUtils.wbdList.add(wbd);
    }

    /**
     * responseBody中通过jsonpath取出对应参数，存入到UserData.VARS
     * @param responseBody
     * @param jsonPathExpression jsonPath表达式
     * @param userDataKey        VARS中的key
     */
    public void getParamsInUserData(String responseBody, String jsonPathExpression, String userDataKey) {
        Object userDataValue = JSONPath.read(responseBody, jsonPathExpression);
        System.out.println("userDataKey:"+userDataKey);
        System.out.println("userDataValue:"+userDataValue);
        //存储到UserData-VARS中
        if (userDataValue != null) {
            UserData.VARS.put(userDataKey, userDataValue);
        }
    }

    /**
     * 获取鉴权头，加入默认请求头并返回headers
     * @return
     */
    public Map<String, String> getAuthorizationHeaders() {
        //从用户变量中取出登录接口存入的token值
        Object token = UserData.VARS.get("${token}");
        //Authorization token
        Map<String, String> headers = new HashMap<>();
        //添加鉴权头
        headers.put("Authorization", "JWT " + token);
        //添加默认头
        headers.putAll(UserData.DEFAULT_HEADERS);
        return headers;
    }

    /**
     * 接口响应断言
     * @param expectedResult 断言的期望值
     * @param responseBody   接口响应内容
     * @return 接口响应断言结果
     */
    public boolean responseAssert(String expectedResult, String responseBody) {
        //expectedResult(json)转成map
        Map<String, Object> map = JSONObject.parseObject(expectedResult, Map.class);
        //遍历map
        Set<String> keySet = map.keySet();
        boolean responseAssertFlag = true;
        for (String actualValueExpression : keySet) {
            //获取期望值
            Object expectedValue = map.get(actualValueExpression);
            //通过实际值表达式从响应体获取实际值
            Object actualValue = JSONPath.read(responseBody, actualValueExpression);
            //断言：只失败一次，整个断言就失败
            if (!expectedValue.equals(actualValue)) {
                //断言失败
                responseAssertFlag = false;
                break;
            }
        }
        logger.info("接口响应断言结果：" + responseAssertFlag);
        return responseAssertFlag;
    }
    /**
     * 参数化替换
     * @param caseInfo
     */
    public void replaceHolder(CaseInfo caseInfo) {
        //替换测试用例中参数、sql、期望结果、url中的占位符为真实数据
        //1.需要替换的对象
        String params = caseInfo.getParams();
        String expectedResult = caseInfo.getExpectedResult();
        String url = caseInfo.getUrl();
        //2.获取所有的占位符（占位符和实际值存储在UserData.VARS中）
        Set<String> keySet = UserData.VARS.keySet();
        //3.遍历所有占位符，取出实际值
        for (String placeHolder : keySet) {
            String value = UserData.VARS.get(placeHolder).toString();
            //4.判断对应用例不为空时，执行参数化替换，并塞回
            if (StringUtils.isNotEmpty(params)) {
                params = params.replace(placeHolder, value);
                caseInfo.setParams(params);
            }
            if (StringUtils.isNotEmpty(expectedResult)) {
                expectedResult = expectedResult.replace(placeHolder, value);
                caseInfo.setExpectedResult(expectedResult);
            }
            if (StringUtils.isNotEmpty(url)) {
                url = url.replace(placeHolder, value);
                caseInfo.setUrl(url);
            }
        }
        logger.info(caseInfo);
    }

}
