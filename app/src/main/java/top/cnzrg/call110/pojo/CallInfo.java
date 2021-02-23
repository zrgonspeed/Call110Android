package top.cnzrg.call110.pojo;

import java.io.Serializable;

public class CallInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String phone;         // +15555215554
    private String longitude;    // 经度
    private String latitude;    // 纬度
    private String time;

    public CallInfo() {
        super();
    }

    public CallInfo(String phone, String longitude, String latitude, String time) {
        super();
        this.phone = phone;
        this.longitude = longitude;
        this.latitude = latitude;
        this.time = time;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
