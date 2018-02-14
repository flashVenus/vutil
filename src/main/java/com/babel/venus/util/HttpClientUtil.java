package com.babel.venus.util;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

/**
 * Date: 16/11/11
 * Time: 下午6:40
 *
 * @author lintc
 */
@SuppressWarnings({"deprecation"})
public class HttpClientUtil {

    private static final Logger log = LoggerFactory.getLogger(HttpClientUtil.class);

    public static ResponseData buildPost(String url, Map<String, Object> requestParamsMap, Map<String, Object> headers) {
    	log.debug("request post, url: " + url + " ,param:" + requestParamsMap);
        ResponseData responseData = new ResponseData();
        PrintWriter printWriter = null;
        BufferedReader bufferedReader = null;
        StringBuilder responseResult = new StringBuilder();
        StringBuilder params = new StringBuilder();
        HttpURLConnection httpURLConnection = null;
        // 组织请求参数
        Iterator it = requestParamsMap.entrySet().iterator();
        for (Map.Entry<String, Object> entry : requestParamsMap.entrySet()) {
            params.append(entry.getKey());
            params.append("=");
            params.append(entry.getValue());
            params.append("&");
        }
        if (params.length() > 0) {
            params.deleteCharAt(params.length() - 1);
        }
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            httpURLConnection = (HttpURLConnection) realUrl.openConnection();
            // 设置通用的请求属性
            httpURLConnection.setRequestProperty("accept", "*/*");
            httpURLConnection.setRequestProperty("connection", "Keep-Alive");
            httpURLConnection.setRequestProperty("Content-Length", String.valueOf(params.length()));
            if (headers != null) {
                for (Map.Entry<String, Object> entry : headers.entrySet()) {
                    httpURLConnection.setRequestProperty(entry.getKey(), (String) entry.getValue());
                }
            }

            httpURLConnection.setRequestMethod("POST");
            // 发送POST请求必须设置如下两行
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setConnectTimeout(1000 * 15); // 链接超时 1000*10 毫秒
            httpURLConnection.setReadTimeout(1000 * 15); // 读取超时 1000*15毫秒
            // 获取URLConnection对象对应的输出流
            printWriter = new PrintWriter(new OutputStreamWriter(httpURLConnection.getOutputStream(), "UTF-8"));
            // 发送请求参数
            printWriter.write(params.toString());
            // flush输出流的缓冲
            printWriter.flush();
            // 根据ResponseCode判断连接是否成功
            int responseCode = httpURLConnection.getResponseCode();
            if (responseCode != 200) {
                log.warn("--> http req failed, code :" + responseCode);
            }
            // 定义BufferedReader输入流来读取URL的ResponseData
            bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8"));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                responseResult.append(line);
            }
        } catch (Exception e) {
            responseData.setFlag(false);
            log.warn("send post request error!" + e.getMessage(), e);
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            try {
                if (printWriter != null) {
                    printWriter.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
        responseData.setResult(responseResult.toString());
        return responseData;
    }

    public static ResponseData buildPost(String url, String requestParamsMap, Map<String, Object> headers) {
        ResponseData responseData = new ResponseData();
        log.debug("request post, url: " + url + " ,param:" + requestParamsMap);
        PrintWriter printWriter = null;
        BufferedReader bufferedReader = null;
        StringBuilder responseResult = new StringBuilder();
        HttpURLConnection httpURLConnection = null;

        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            httpURLConnection = (HttpURLConnection) realUrl.openConnection();
            // 设置通用的请求属性
            httpURLConnection.setRequestProperty("accept", "*/*");
            httpURLConnection.setRequestProperty("connection", "Keep-Alive");
            httpURLConnection.setRequestProperty("Content-Length", String.valueOf(requestParamsMap.length()));
            if (headers != null) {
                for (Map.Entry<String, Object> entry : headers.entrySet()) {
                    httpURLConnection.setRequestProperty(entry.getKey(), entry.getValue() + "");
                }
            }

            httpURLConnection.setRequestMethod("POST");
            // 发送POST请求必须设置如下两行
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setConnectTimeout(1000 * 15); // 链接超时 1000*10 毫秒
            httpURLConnection.setReadTimeout(1000 * 15); // 读取超时 1000*30毫秒
            // 获取URLConnection对象对应的输出流
            printWriter = new PrintWriter(new OutputStreamWriter(httpURLConnection.getOutputStream(), "UTF-8"));
            // 发送请求参数
            printWriter.write(requestParamsMap);
            // flush输出流的缓冲
            printWriter.flush();
            // 根据ResponseCode判断连接是否成功
            int responseCode = httpURLConnection.getResponseCode();
            if (responseCode != 200) {
                log.warn("--> req http failed, code " + responseCode);
            }
            // 定义BufferedReader输入流来读取URL的ResponseData
            bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8"));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                responseResult.append(line);
            }
        } catch (Exception e) {
            responseData.setFlag(false);
            log.error("send post request error!" + e.getMessage(), e);
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            try {
                if (printWriter != null) {
                    printWriter.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
        responseData.setResult(responseResult.toString());
        return responseData;
    }

    public static ResponseData buildGet(String url, Map<String, Object> params, Map<String, Object> headers) {
        ResponseData responseData = new ResponseData();
        if (params != null && params.size() > 0) {
            url += "?";
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                try {
                    url += entry.getKey() + "=" + URLEncoder.encode(entry.getValue() + "", "UTF-8") + "&";
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            url = url.substring(0, url.length() - 1);
        }
        String result = null;
        //创建默认的httpClient实例
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            //用get方法发送http请求
            HttpGet get = new HttpGet(url);
            if (headers != null) {
                for (Map.Entry<String, Object> entry : headers.entrySet()) {
                    get.setHeader(entry.getKey(), entry.getValue() + "");
                }
            }
            CloseableHttpResponse httpResponse = null;
            //发送get请求
            httpResponse = httpClient.execute(get);
            try {
                //response实体
                HttpEntity entity = httpResponse.getEntity();
                if (null != entity) {
                    result = EntityUtils.toString(entity, "UTF-8");
                }
            } finally {
                httpResponse.close();
            }
        } catch (Exception e) {
            responseData.setFlag(false);
            log.error("buildGet error, ", e);
        } finally {
            try {
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        responseData.setResult(result);
        return responseData;
    }


    private static String getEncode(InputStream inputStream) {
        String code = "gb2312";
        try {
            byte[] head = new byte[3];
            inputStream.read(head);
            if (head[0] == -17 && head[1] == -69 && head[2] == -65)
                code = "UTF-8";
            if (head[0] == -1 && head[1] == -2)
                code = "UTF-16";
            if (head[0] == -2 && head[1] == -1)
                code = "Unicode";

            inputStream.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return code;
    }
}
