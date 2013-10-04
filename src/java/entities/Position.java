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
public class Position implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private String name;
    private String pinyin;
    private String userno;
    private String schoolId;
    private List<Enterstudent> enterStudList;
    private List<Practicenote> practList;
    private SQLTool<Enterstudent> esDao = new SQLTool<Enterstudent>();
    private SQLTool<Practicenote> practDao = new SQLTool<Practicenote>();

    public Position() {
    }

    public Position(Integer id) {
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

    /**
     * @return the enterStudList
     */
    public List<Enterstudent> getEnterStudList() {
        if (enterStudList == null) {
            enterStudList = esDao.getBeanListHandlerRunner("select * from enterstudent where positionId=" + id, new Enterstudent());
        }
        return enterStudList;
    }

    /**
     * @param enterStudList the enterStudList to set
     */
    public void setEnterStudList(List<Enterstudent> enterStudList) {
        this.enterStudList = enterStudList;
    }

    /**
     * @return the practList
     */
    public List<Practicenote> getPractList() {
        if (practList == null) {
            practList = practDao.getBeanListHandlerRunner("select * from practicenote" + schoolId + " where positionid=" + id, new Practicenote());
        }
        return practList;
    }

    /**
     * @param practList the practList to set
     */
    public void setPractList(List<Practicenote> practList) {
        this.practList = practList;
    }

    /**
     * @return the schoolId
     */
    public String getSchoolId() {
        return schoolId;
    }

    /**
     * @param schoolId the schoolId to set
     */
    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }
}
