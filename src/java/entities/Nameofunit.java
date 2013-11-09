/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import entitiesBeans.NameofunitLocal;
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
public class Nameofunit implements Serializable {

    private static final long serialVersionUID = 1L;
    private String id;
    private String name;
    private String parentid;
    private String pinyin;
    private Integer pri=0;
    private Integer mytype=0;
    private String userno;

    private List<User> teacherList;
    private List<User> studentList;
    private User inputor;
    private final UserLocal userLocal = new UserLocal();
    private Nameofunit parent;
    private List<Nameofunit> childrenList;
    private final NameofunitLocal myLocal = new NameofunitLocal();

    public Nameofunit() {
    }

    public Nameofunit(String id) {
        this.id = id;
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

    public List<User> getTeacherList() {
        if (null == this.teacherList) {
            this.teacherList = this.userLocal.getList("select * from teacherinfo where nameofunitid='" + this.id);
        }
        return teacherList;
    }

    public Nameofunit getParent() {
        if (parent == null) {
            List<Nameofunit> parentList = myLocal.getList("select * from nameofunit" + StaticFields.currentGradeNum + " where parentid='" + parentid + "'");
            if (parentList.isEmpty()) {
                return null;
            } else {
                parent = parentList.get(0);
            }
        }
        return parent;
    }

    public void setParent(Nameofunit parNameofunit) {
        this.parent = parNameofunit;
    }

    public List<Nameofunit> getChildnameofunits() {
        if (childrenList == null) {
            childrenList = myLocal.getList("select * from nameofunit" + StaticFields.currentGradeNum + "  where parentid='" + id + "'");
        }
        return childrenList;
    }

    public void setChildnameofunits(List<Nameofunit> childnameofunits) {
        this.childrenList = childnameofunits;
    }

    public User getInputor() {
        if (inputor == null) {
            this.dealId0();
        } else if (null == this.inputor.getUno()) {
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

    public int getMytype() {
        return mytype;
    }

    public void setMytype(int mytype) {
        this.mytype = mytype;
    }

    public List<User> getStudentList() {
        if (null == this.studentList) {
            this.studentList = this.userLocal.getList("select * from student" + StaticFields.currentGradeNum + this.id);
        }
        return studentList;
    }

    public void setStudentList(List<User> studentList) {
        this.studentList = studentList;
    }

    public void setTeacherList(List<User> teacherList) {
        this.teacherList = teacherList;
    }
}
