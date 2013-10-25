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
    private int mytype;
    private String userno;
    private String schoolId;
    private List<User> userList;
    private Nameofunit parNameofunit;
    private List<Nameofunit> childnameofunits;
    private final SQLTool<User> userDao = new SQLTool<>();
    private final SQLTool<Nameofunit> nameDao = new SQLTool<>();
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

    public List<User> getUserList() {
        if (userList == null) {
            if (userno.length() == 6) {
                userList = userDao.getBeanListHandlerRunner("select * from teacherinfo"+StaticFields.currentGradeNum+" where uno='" + userno + "'", new User());
            } else {
                userList = userDao.getBeanListHandlerRunner("select * from student" +StaticFields.currentGradeNum+ schoolId + " where uno='" + userno + "'", new User());
            }
        }
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public Nameofunit getParNameofunit() {
        if (parNameofunit == null) {
            parNameofunit = nameDao.getBeanListHandlerRunner("select * from nameofunit" +StaticFields.currentGradeNum+" where id='" + parentid + "'", new Nameofunit()).get(0);
        }
        return parNameofunit;
    }

    public void setParNameofunit(Nameofunit parNameofunit) {
        this.parNameofunit = parNameofunit;
    }

    public List<Nameofunit> getChildnameofunits() {
        if (childnameofunits == null) {
            childnameofunits = nameDao.getBeanListHandlerRunner("select * from nameofunit" +StaticFields.currentGradeNum+"  where parentid='" + id + "'", new Nameofunit());
        }
        return childnameofunits;
    }

    public void setChildnameofunits(List<Nameofunit> childnameofunits) {
        this.childnameofunits = childnameofunits;
    }


    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public User getTeacher() {
        if(null==teacher){
            teacher=userDao.getBeanListHandlerRunner("select * from teacherinfo" +StaticFields.currentGradeNum+"  where uno='"+this.getUserno()+"'", new User()).get(0);
        }
        return teacher;
    }

    public int getMytype() {
        return mytype;
    }

    public void setMytype(int mytype) {
        this.mytype = mytype;
    }
}
