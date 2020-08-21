package com.lemon.utils;

import com.alibaba.fastjson.JSONObject;
import com.lemon.pojo.CaseInfo;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class HttpUtils {
    //添加日志，并日志打印输出
    private static Logger logger= Logger.getLogger(HttpUtils.class);
    /**
     * 根据请求参数发送http请求
     * @param caseInfo  请求参数
     * @param headers   请求头
     * @return
     * @throws IOException
     */
    public static String call(CaseInfo caseInfo,Map<String, String> headers) throws IOException {
        String responseBody="";
        String params = caseInfo.getParams();
        String url = caseInfo.getUrl();
        String method = caseInfo.getMethod();
        String contentType = caseInfo.getContentType();
        //2.判断请求方式
        //2.1如果是get
        if ("get".equalsIgnoreCase(method)) {
            responseBody=HttpUtils.get(url, headers);
            //2.2如果是post
        } else if ("post".equalsIgnoreCase(method)) {
            //2.2.1判断参数类型，如果是json
            if ("json".equalsIgnoreCase(contentType)) {
                //2.2.2判断参数类型，如果是form
            } else if ("form".equalsIgnoreCase(contentType)) {
                params = jsonStr2KeyValueStr(params);
                //覆盖默认请求头中的Content-Type
                headers.put("Content-Type", "application/x-www-form-urlencoded");
            }
            responseBody=HttpUtils.post(url, params, headers);
         //2.3如果是patch
        } else if ("patch".equalsIgnoreCase(method)) {
            responseBody=HttpUtils.patch(url, params, headers);
        }
        return responseBody;
    }


    /**
     * get
     * @param url
     * @throws IOException
     */
    public static String get(String url, Map<String,String> headers) throws IOException {
        //工具类中用静态方法，静态方法中不设置属性，不用创建对象，直接类名.引用
        //1.创建get请求，并传入接口地址
        HttpGet get = new HttpGet(url);
        //2.设置请求头
        setHeaders(headers, get);
        //get请求没有参数，所以跳过设置请求体
        //3.创建HttpClient客户端
        CloseableHttpClient client = HttpClients.createDefault();
        //4.获取响应对象
        HttpResponse response = client.execute(get);
        //5.格式化响应对象
        return printResponse(response);
    }

    /**
     * post
     * @param url
     * @param params
     * @throws IOException
     */
    public static String post(String url, String params, Map<String,String> headers) throws IOException {
        //1.创建HttpPost请求，并传入接口地址
        HttpPost post = new HttpPost(url);
        //2.设置请求头
        setHeaders(headers, post);
        //3.创建，设置请求体
        StringEntity body = new StringEntity(params, "utf-8");
        post.setEntity(body);
        //4.创建客户端
        CloseableHttpClient client = HttpClients.createDefault();
        //CloseableHttpClient client = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build();
        //5.获取响应对象
        HttpResponse response = client.execute(post);
        return printResponse(response);
    }

    /**
     * patch
     * @param url
     * @param params
     * @throws IOException
     */
    public static String patch(String url, String params,Map<String, String> headers) throws IOException {
        //1.创建HttpPatch请求，并传入接口地址
        HttpPatch patch = new HttpPatch(url);
        //2.设置请求头
        setHeaders(headers, patch);
        //3.设置请求体
        StringEntity body = new StringEntity(params, "utf-8");
        patch.setEntity(body);
        //4.创建客户端
        CloseableHttpClient client = HttpClients.createDefault();
        //5.获取响应对象
        HttpResponse response = client.execute(patch);
        return printResponse(response);
    }

    /**
     * 打印响应信息
     * @param response
     * @return
     * @throws IOException
     */
    public static String printResponse(HttpResponse response) throws IOException {
        //5.1获取响应状态码
        int statusCode = response.getStatusLine().getStatusCode();
        logger.info(statusCode);
        //5.获取响应头
        Header[] allHeaders = response.getAllHeaders();
        logger.info(Arrays.toString(allHeaders));
        //5.3获取响应体
        HttpEntity entity = response.getEntity();
        String body = EntityUtils.toString(entity);
        logger.info(body);
        return body;
    }

    /**
     * 抽取请求头的键值对
     * @param headers  包含了请求头的map集合
     * @param request     请求对象
     */
    //HttpRequest父接口（ctrl+H，查看继承树）
    public static void setHeaders(Map<String, String> headers, HttpRequest request) {
        //获取所有的请求头name
        Set<String> headerNames = headers.keySet();
        //遍历所有的请求头name
        for (String headerName : headerNames) {
            //获取请求头name对应的value
            String headerValue = headers.get(headerName);
            //设置请求头name,value
            request.setHeader(headerName,headerValue);
        }
    }
    /**
     * 实现：json参数转成key=value(form格式)
     * json-->map-->key=value
     * @param json
     * @return
     */
    public static String jsonStr2KeyValueStr(String json) {
        Map<String,String> map = JSONObject.parseObject(json, Map.class);
        Set<String> keySet = map.keySet();
        String formParams = "";
        for (String key : keySet) {
            String value = map.get(key);
            formParams += key + "=" + map.get(key) + "&";
        }
        return formParams.substring(0,formParams.length()-1);
    }

}
