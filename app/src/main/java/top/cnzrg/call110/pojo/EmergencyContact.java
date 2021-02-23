package top.cnzrg.call110.pojo;

import java.io.Serializable;

/**
 * FileName: EmergencyContact
 * Author: ZRG
 * Date: 2019/8/27 14:46
 */
public class EmergencyContact implements Serializable {
    private Integer _id;
    private String name;
    private String phone;

    public Integer get_id() {
        return _id;
    }

    public void set_id(Integer _id) {
        this._id = _id;
    }

    public EmergencyContact() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "EmergencyContact{" +
                "_id='" + _id + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
