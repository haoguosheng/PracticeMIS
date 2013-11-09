/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.Enterstudent;
import entities.Stuentrel;
import entities.User;
import entitiesBeans.EnterstudentLocal;
import entitiesBeans.StuentrelLocal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import tools.StaticFields;

/**
 *
 * @author Idea
 */
@Named
@RequestScoped
public class StudentSelectEnt implements java.io.Serializable {

    private @Inject
    User user;
    private final StuentrelLocal seDao = new StuentrelLocal();
    private final EnterstudentLocal esDao = new EnterstudentLocal();
    private List<Stuentrel> stuForSameEnt, stuentrel4SameStu;//选择同一企业的学生和同一学生选择的不同企业
    private Enterstudent entStuRel;
    private Stuentrel stuRel;
    private int enterStuId;

    public String deleteSelectedEnterprise(Stuentrel stuEntTem) {
        seDao.remove(stuEntTem, getUser().getSchoolId());
        this.stuentrel4SameStu = null;
        return null;
    }

    public List<Stuentrel> getStuentrel4SameStu() {
        if (null == this.stuentrel4SameStu || this.stuentrel4SameStu.isEmpty()) {
            this.stuentrel4SameStu = new ArrayList<>();
            stuentrel4SameStu = seDao.getList("select * from stuentrel" + StaticFields.currentGradeNum + this.getUser().getSchoolId() + "  where stuno ='" + this.getUser().getUno() + "'");
        }
        return stuentrel4SameStu;
    }

    public List<Stuentrel> getStuForSameEnt() {
        if (null == stuForSameEnt || stuForSameEnt.isEmpty()) {
            //先找到Enterstudent        
            //再找到EnterId
            //再找List<Enterstudent>
            if (null != this.getStuRel()) {
                int enterIdTem = this.getEntStuRel().getEnterid();
                List<Enterstudent> entStuListTem = esDao.getList("select * from enterstudent" + StaticFields.currentGradeNum + "  where enterid=" + enterIdTem);
                //再找List<Stuentrel>
                if (null == entStuListTem || entStuListTem.isEmpty()) {
                } else {
                    String esId = "";
                    for (Enterstudent es : entStuListTem) {
                        esId += es.getId() + ",";
                    }
                    esId = esId.substring(0, esId.length() - 1);
                    stuForSameEnt = seDao.getList("select * from stuentrel" + StaticFields.currentGradeNum + this.getUser().getSchoolId() + "  where entstuid in (" + esId + ")");
                }
            } else {
                stuForSameEnt = new LinkedList<>();
            }
        }
        return stuForSameEnt;
    }

    public String userAddEnter(int entStuId) {
        if (this.canSelect()) {
            this.seDao.executUpdate("insert into stuentrel" + StaticFields.currentGradeNum + this.getUser().getSchoolId() + " (stuno,entstuid) values('" + this.getUser().getUno() + "', " + entStuId + ")");
        } else {
            FacesContext.getCurrentInstance().addMessage("myMessage", new FacesMessage("已经选择" + StaticFields.selectedEnt + "个实习企业，不能再次选择！"));
        }
        return null;
    }

    public Enterstudent getEntStuRel() {
        if (null == entStuRel && null != this.getStuRel()) {
            int entStuId = this.getStuRel().getEntstuid();
            entStuRel = esDao.find(entStuId);
        }
        return entStuRel;
    }

    public void setEntStuRel(Enterstudent entStuRel) {
        this.entStuRel = entStuRel;
    }

    public Stuentrel getStuRel() {
        if (null == stuRel) {
            String schoolId = this.getUser().getSchoolId();
            List<Stuentrel> stuRelList = seDao.getList("select * from STUENTREL" + StaticFields.currentGradeNum + schoolId + " where stuno='" + this.getUser().getUno() + "'");
            if (stuRelList.size() > 0) {
                this.stuRel = stuRelList.get(0);
            }
        }
        return stuRel;
    }

    public void setStuRel(Stuentrel stuRel) {
        this.stuRel = stuRel;
    }

    public int getEnterStuId() {
        return enterStuId;
    }

    public void setEnterStuId(int enterStuId) {
        this.enterStuId = enterStuId;
    }

    public User getUser() {
//        if (null == user) {
//            FacesContext context = FacesContext.getCurrentInstance();
//            HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
//            user = (User) session.getAttribute("myUser");
//        }
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean canSelect() {
        List<Stuentrel> stuList = seDao.getList("select * from STUENTREL" + StaticFields.currentGradeNum + this.getUser().getSchoolId() + " where stuno='" + this.getUser().getUno() + "'");
        if (stuList.size() >= StaticFields.selectedEnt) {
            return false;
        } else {
            return true;
        }
    }
}
