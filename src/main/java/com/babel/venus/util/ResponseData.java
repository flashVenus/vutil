package com.babel.venus.util;

/**
 * User: joey
 * Date: 2017/10/30
 * Time: 21:16
 * request请求返回的结果
 */
public class ResponseData {

    private boolean flag =true;
    private String result;

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
