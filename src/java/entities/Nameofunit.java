/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.List;
import tools.SQLTool;
import tools.StaticFields;

/**
 *
 * @author Administrator
 */
public class Nameofunit implements Serializable {

    private static final long serialVersionUID = 1L;
    private String id;
    private String name;
    private String parentid;
    private String pinyin;
    private int pri;
    private String userno;
    private String schoolId;
    private List<User> userList;
    private Nameofunit parNameofunit;
    private List<Nameofunit> childnameofunits;
    private SQLTool<User> userDao = new SQLTool<User>();
    private SQLTool<Nameofunit> nameDao = new SQLTool<Nameofunit>();
    private User teacher;

    public Nameofunit() {
    }

    public Nameofunit(String id) {
        this.id = id;
    }

    public Nameofunit(String id, String name, int pri) {
        this.id = id;
        this.name = name;
        this.pri = pri;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public int getPri() {
        return pri;
    }

    public void setPri(int pri) {
        this.pri = pri;
    }

    public String getUserno() {
        return userno;
    }

    public void setUserno(String userno) {
        this.userno = userno;
    }

    public String getParentid() {
        return parentid;
    }

    public void setParentid(String parentid) {
        this.parentid = parentid;
    }

    /**
     * @return the userList
     */
    public List<User> getUserList() {
        if (userList == null) {
            if (userno.length() == 6) {
                userList = userDao.getBeanListHandlerRunner("select * from teacherinfo where uno='" + userno + "'", new User());
            } else {
                userList = userDao.getBeanListHandlerRunner("select * from student" +StaticFields.currentGradeNum+ schoolId + " where uno='" + userno + "'", new User());
            }
        }
        return userList;
    }

    /**
     * @param userList the userList to set
     */
    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    /**
     * @return the parNameofunit
     */
    public Nameofunit getParNameofunit() {
        if (parNameofunit == null) {
            parNameofunit = nameDao.getBeanListHandlerRunner("select * from nameofunit where id='" + parentid + "'", new Nameofunit()).get(0);
        }
        return parNameofunit;
    }

    /**
     * @param parNameofunit the parNameofunit to set
     */
    public void setParNameofunit(Nameofunit parNameofunit) {
        this.parNameofunit = parNameofunit;
    }

    /**
     * @return the childnameofunits
     */
    public List<Nameofunit> getChildnameofunits() {
        if (childnameofunits == null) {
            childnameofunits = nameDao.getBeanListHandlerRunner("select * from nameofunit where parentid='" + id + "'", new Nameofunit());
        }
        return childnameofunits;
    }

    /**
     * @param childnameofunits the childnameofunits to set
     */
    public void setChildnameofunits(List<Nameofunit> childnameofunits) {
        this.childnameofunits = childnameofunits;
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

    /**
     * @return the teacher
     */
    public User getTeacher() {
        if(null==teacher){
            teacher=userDao.getBeanListHandlerRunner("select * from teacherinfo where uno='"+this.getUserno()+"'", new User()).get(0);
        }
        return teacher;
    }
}
