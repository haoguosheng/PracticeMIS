/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import entitiesBeans.EnterpriseLocal;
import entitiesBeans.UserLocal;
import java.io.Serializable;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import tools.StaticFields;

/**
 *
 * @author Administrator
 */
@Named
@SessionScoped
public class City implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private String name;
    private String pinyin;
    private User inputor;
    private String userno;
    private List<Enterprise> enterprisesList;
    private final EnterpriseLocal epDao = new EnterpriseLocal();
    private final UserLocal userLocal = new UserLocal();

    public City() {
    }

    public City(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getUserno() {
        return userno;
    }

    public void setUserno(String userno) {
        this.userno = userno;
    }

    public List<Enterprise> getEnterprises() {
        if (enterprisesList == null) {
            enterprisesList = epDao.getList("select * from enterprise" + StaticFields.currentGradeNum + " where cityid=" + id);
        }
        return enterprisesList;
    }

    public User getInputor() {
        if (inputor == null) {
            this.dealId0();
        } else if (null== this.inputor.getUno()) {
            this.dealId0();
        }
        return inputor;
    }
    /*
     *当对象是内在对象时处理外键inputor
     */

    private void dealId0() {
        if (null == this.id) {
            if (null == inputor) {
                this.inputor = new User();
            }
        } else {
            inputor = userLocal.getList("select * from teacherinfo" + StaticFields.currentGradeNum + "  where uno='" + this.getUserno() + "'").get(0);
        }
    }

    public void setEnterprises(List<Enterprise> enterprises) {
        this.enterprisesList = enterprises;
    }
}
