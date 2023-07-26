package com.zjl.validator;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangjiling
 * @date 2023/7/26 21:08
 */
public class ValidationResult {
    private Boolean hasErrors;

    private Map<String, String> errMsgMap = new HashMap<>();

    public Boolean getHasErrors() {
        return hasErrors;
    }

    public void setHasErrors(Boolean hasErrors) {
        this.hasErrors = hasErrors;
    }

    public Map<String, String> getErrMsgMap() {
        return errMsgMap;
    }

    public void setErrMsgMap(Map<String, String> errMsgMap) {
        this.errMsgMap = errMsgMap;
    }

    public String getErrMsg() {
        return StringUtils.join(errMsgMap.values().toArray(), ",");
    }
}
