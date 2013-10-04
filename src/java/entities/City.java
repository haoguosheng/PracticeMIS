/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.List;
import tools.SQLTool;

/**
 *
 * @author Administrator
 */
public class City implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private String name;
    private String pinyin;
    private String userno;
    private List<Enterprise> enterprises;
    private SQLTool<Enterprise> epDao = new SQLTool<Enterprise>();

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
        if(enterprises == null){
            enterprises = epDao.getBeanListHandlerRunner("select * from enterprise where id=" + id, new Enterprise());
        }
        return enterprises;
    }

    public void setEnterprises(List<Enterprise> enterprises) {
        this.enterprises = enterprises;
    }
}
