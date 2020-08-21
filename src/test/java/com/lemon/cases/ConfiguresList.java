package com.lemon.cases;

import com.lemon.pojo.CaseInfo;
import com.lemon.utils.Constants;
import com.lemon.utils.ExcelUtils;
import com.lemon.utils.HttpUtils;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class ConfiguresList extends BaseCase{
    @Test(dataProvider = "datas")
    public void test(CaseInfo caseInfo) throws IOException, SQLException {
        //执行获取配置列表接口测试逻辑
        //1.参数化替换
        replaceHolder(caseInfo);
        //2.获取鉴权头
        Map<String, String> authorizationHeaders = getAuthorizationHeaders();
        //3.调用获取用户信息接口
        //把添加了鉴权头和请求头的headers传入call方法中并调用
        String responseBody = HttpUtils.call(caseInfo, authorizationHeaders);
        //4.断言响应结果（实际值与期望值相等，断言成功，否则失败）
        boolean responseAssertFlag = responseAssert(caseInfo.getExpectedResult(), responseBody);
        //5.添加响应回写内容（继承BaseCase）
        addWriteBackData(sheetIndex, caseInfo.getId(), Constants.RESPONSE_CELL_NUM, responseBody);
        //6.断言回写（三元运算）
        String assertResult=responseAssertFlag?Constants.ASSERT_SUCCESS:Constants.ASSERT_FAIL;
        addWriteBackData(sheetIndex, caseInfo.getId(), Constants.ASSERT_CELL_NUM, assertResult);
        //7.报表断言（一定放在代码最后，否则报错后面不执行）
        Assert.assertEquals(assertResult,Constants.ASSERT_SUCCESS);
}
    @DataProvider
    public Object[] datas() throws Exception {
        List read = ExcelUtils.read(sheetIndex, 1, CaseInfo.class);
        //List中有toArray()，直接将集合转成数组
        return read.toArray();
    }
}

