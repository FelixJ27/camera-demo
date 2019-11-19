package util;

import java.util.List;

/**
 * @Author Felix J
 * @Description
 * @Date 2019/11/19 13:56
 */
public class DataView<T> {
    private Integer code;
    private String message;
    private String reqCode;
    private List<T> data;
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReqCode() {
        return reqCode;
    }

    public void setReqCode(String reqCode) {
        this.reqCode = reqCode;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
