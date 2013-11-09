/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import entitiesBeans.RoleinfoLocal;
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
public class Roleinfo implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private String resouceids;
    private String name;
    private Integer privilege=0;
    private Integer canseeall=0;

    private List<User> teacherList;
    private final UserLocal userLocal = new UserLocal();
    private final RoleinfoLocal myLocal=new RoleinfoLocal();

    public Roleinfo() {
    }

    public Roleinfo(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getResouceids() {
        return resouceids;
    }

    public void setResouceids(String resouceids) {
        this.resouceids = resouceids;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPrivilege() {
        return privilege;
    }

    public void setPrivilege(Integer privilege) {
        this.privilege = privilege;
    }

    public Integer getCanseeall() {
        return canseeall;
    }

    public void setCanseeall(Integer canseeall) {
        this.canseeall = canseeall;
    }

    /**
     * @return the teacherList
     */
    public List<User> getTeacherList() {
        if ( null==teacherList) {
            teacherList = userLocal.getList("select * from teacherinfo" +StaticFields.currentGradeNum+"  where roleid=" + id);
        }
        return teacherList;
    }
    public void setTeacherList(List<User> teacherList) {
        this.teacherList = teacherList;
    }
}
