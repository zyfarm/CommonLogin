package com.myelin.future.common.retobj;

/**
 * Created by gabriel on 14-12-11.
 */
public class ResultData {
    private Integer errCode;
    private String errMsg;
    private Object data;

    public ResultData() {
        this.errCode=0;
        this.errMsg="success";
        this.data=null;
    }

    public ResultData(Integer errCode, String errMsg, Object data) {
        this.errCode = errCode;
        this.errMsg = errMsg;
        this.data = data;
    }

    public Integer getErrCode() {
        return errCode;
    }

    public void setErrCode(Integer errCode) {
        this.errCode = errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
