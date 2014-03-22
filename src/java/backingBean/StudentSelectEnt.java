/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.Enterprise;
import entities.Enterstudent;
import entities.MyUser;
import entities.Student;
import entities.Stuentrel;
import entities.Teacherinfo;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import sessionBeans.EnterstudentFacadeLocal;
import sessionBeans.StuentrelFacadeLocal;
import tools.StaticFields;

@Named
@RequestScoped
public class StudentSelectEnt implements java.io.Serializable {

    @EJB
    private StuentrelFacadeLocal stuEntRelEjb;
    @EJB
    private EnterstudentFacadeLocal entStuEjb;
    private List<Stuentrel> stuForSameEnt, stuentrel4SameStu;  //选择同一企业的学生和同一学生选择的不同企业
    private Enterstudent entStuRel;
    private Stuentrel stuRel;
    private int enterStuId;
    MyUser user;
    HttpSession mySession = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);

    @PostConstruct
    public void init() {
        mySession = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
         int length = (int) (mySession.getAttribute("userNoLength"));
        if (length == StaticFields.teacherUnoLength) {
            user = (Teacherinfo) mySession.getAttribute("teacherUser");

        } else if (length == StaticFields.stuUnoLength1 || length == StaticFields.stuUnoLength2) {
            user = (Student) mySession.getAttribute("studentUser");
        }
    }

    public String deleteSelectedEnterprise(Stuentrel stuEntTem) {
        stuEntRelEjb.remove(stuEntTem);
        this.stuentrel4SameStu = null;
        return null;
    }

    public List<Stuentrel> getStuentrel4SameStu() {
        if (null == this.stuentrel4SameStu || this.stuentrel4SameStu.isEmpty()) {
            this.stuentrel4SameStu = new ArrayList<>();
            stuentrel4SameStu = stuEntRelEjb.getList("select * from stuentrel  where stuno ='" + this.user.getUno() + "' order by id");
        }
        return stuentrel4SameStu;
    }

    public List<Stuentrel> getStuForSameEnt(Enterprise enter) {
//        if (null == stuForSameEnt || stuForSameEnt.isEmpty()) {
        //先找到Enterstudent        
        //再找到EnterId
        //再找List<Enterstudent>
        if (null != this.getStuRel()) {
//                int enterIdTem = this.getEntStuRel().getEnterid();
            List<Enterstudent> entStuListTem = entStuEjb.getList("select * from enterstudent   where enterid=" + enter.getId() + " order by id");
            //再找List<Stuentrel>
            if (null == entStuListTem || entStuListTem.isEmpty()) {
            } else {
                String esId = "";
                for (Enterstudent es : entStuListTem) {
                    esId += es.getId() + ",";
                }
                esId = esId.substring(0, esId.length() - 1);
                stuForSameEnt = stuEntRelEjb.getList("select * from stuentrel  where stuno !='" + user.getUno() + "' and entstuid in (" + esId + ") order by stuno");
            }
        } else {
            stuForSameEnt = new LinkedList<>();
        }
//        }
        return stuForSameEnt;
    }

    public String userAddEnter(int entStuId, int cityId) {
        if (this.canSelect()) {
            this.stuEntRelEjb.executUpdate("insert into stuentrel (stuno,entstuid,cityId) values('" + this.user.getUno() + "', " + entStuId + ", " + cityId + ")");
        } else {
            FacesContext.getCurrentInstance().addMessage("myMessage", new FacesMessage("已经选择" + StaticFields.selectedEnt + "个实习企业，不能再次选择！"));
        }
        return null;
    }

    public Enterstudent getEntStuRel() {
        if (null == entStuRel && null != this.getStuRel()) {
            int entStuId = this.getStuRel().getEnterstudent().getId();
            entStuRel = entStuEjb.find(entStuId);
        }
        return entStuRel;
    }

    public void setEntStuRel(Enterstudent entStuRel) {
        this.entStuRel = entStuRel;
    }

    public Stuentrel getStuRel() {
        if (null == stuRel) {
            List<Stuentrel> stuRelList = stuEntRelEjb.getList("select * from STUENTREL where stuno='" + this.user.getUno() + "' order by stuno");
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

    public boolean canSelect() {
        List<Stuentrel> stuList = stuEntRelEjb.getList("select * from STUENTREL where stuno='" + this.user.getUno() + "' order by stuno");
        if (stuList.size() >= StaticFields.selectedEnt) {
            return false;
        } else {
            return true;
        }
    }
}
