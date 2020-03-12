package util;


/**
 * @Author Felix J
 * @Description
 * @Date 2019/11/19 13:56
 */
public class RemoteResult {
    private int code;
    private String message;
    private Object data;
    private String reqCode;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getReqCode() {
        return reqCode;
    }

    public void setReqCode(String reqCode) {
        this.reqCode = reqCode;
    }
}
