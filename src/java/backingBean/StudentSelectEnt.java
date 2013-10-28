/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.Enterstudent;
import entities.Stuentrel;
import entities.User;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import tools.SQLTool;
import tools.StaticFields;

/**
 *
 * @author Idea
 */
@Named
@SessionScoped
public class StudentSelectEnt implements java.io.Serializable {

    @Inject
    private CheckLogin checkLogin;
    private SQLTool<Stuentrel> seDao;
    private SQLTool<Enterstudent> esDao;
    private User myUser;
    private List<Stuentrel> stuForSameEnt, enter4SameStu;//选择同一企业的学生和同一学生选择的不同企业
    private Enterstudent entStuRel;
    private Stuentrel stuRel;
    private int enterStuId;

    @PostConstruct
    public void init() {
        seDao = new SQLTool<>();
        esDao = new SQLTool<>();
    }

    public String deleteSelectedEnterprise() {
        User sessionuser = getUser();
        List<Stuentrel> temList=sessionuser.getStuentrelList();
        seDao.executUpdate("delete from stuentrel" + StaticFields.currentGradeNum + sessionuser.getSchoolId() + " where id=" + temList.get(temList.size()-1).getId());
        this.enter4SameStu = null;
        return "selectMyEnterprise";
    }

    public User getUser() {
        if (null == this.myUser) {
            this.myUser = checkLogin.getUser();
        }
        return myUser;
    }

    public List<Stuentrel> getEnter4SameStu() {
        if (null == this.enter4SameStu || this.enter4SameStu.isEmpty()) {
            this.enter4SameStu = new ArrayList<>();
            enter4SameStu = seDao.getBeanListHandlerRunner("select * from stuentrel" + StaticFields.currentGradeNum + this.getUser().getSchoolId() + "  where stuno ='" + this.getUser().getUno() + "'", new Stuentrel());
        }
        return enter4SameStu;
    }

    public List<Stuentrel> getStuForSameEnt() {
        if (null == stuForSameEnt || stuForSameEnt.isEmpty()) {
            //先找到Enterstudent        
            //再找到EnterId
            //再找List<Enterstudent>
            if (null != this.getStuRel()) {
                int enterIdTem = this.getEntStuRel().getEnterid();
                List<Enterstudent> entStuListTem = esDao.getBeanListHandlerRunner("select * from enterstudent" + StaticFields.currentGradeNum + "  where enterid=" + enterIdTem, new Enterstudent());
                //再找List<Stuentrel>
                if (null == entStuListTem || entStuListTem.isEmpty()) {
                } else {
                    String esId = "";
                    for (Enterstudent es : entStuListTem) {
                        esId += es.getId() + ",";
                    }
                    esId = esId.substring(0, esId.length() - 1);
                    stuForSameEnt = seDao.getBeanListHandlerRunner("select * from stuentrel" + StaticFields.currentGradeNum + this.getUser().getSchoolId() + "  where entstuid in (" + esId + ")", new Stuentrel());
                }
            }else{
                stuForSameEnt=new LinkedList<>();
            }
        }
        return stuForSameEnt;
    }

    public String userAddEnter(int entStuId) {
        if (seDao.getBeanListHandlerRunner("select * from STUENTREL" + StaticFields.currentGradeNum + this.getUser().getSchoolId() + " where stuno='" + this.getUser().getUno() + "'", new Stuentrel()).size() < StaticFields.selectedEnt) {
            this.seDao.executUpdate("insert into stuentrel" + StaticFields.currentGradeNum + this.getUser().getSchoolId() + " (stuno,entstuid) values('" + this.getUser().getUno() + "', " + entStuId + ")");
        } else {
            FacesContext.getCurrentInstance().addMessage("myMessage", new FacesMessage("已经选择" + StaticFields.selectedEnt + "个实习企业，不能再次选择！"));
        }
        return null;
    }

    public CheckLogin getCheckLogin() {
        return checkLogin;
    }

    public void setCheckLogin(CheckLogin checkLogin) {
        this.checkLogin = checkLogin;
    }

    public Enterstudent getEntStuRel() {
        if (null == entStuRel && null != this.getStuRel()) {
            int entStuId = this.getStuRel().getEntstuid();
            String sqlString = "select * from enterstudent" + StaticFields.currentGradeNum + " where id=" + entStuId;
            entStuRel = esDao.getBeanListHandlerRunner(sqlString, new Enterstudent()).get(0);
        }
        return entStuRel;
    }

    public void setEntStuRel(Enterstudent entStuRel) {
        this.entStuRel = entStuRel;
    }

    public Stuentrel getStuRel() {
        if (null == stuRel) {
            String schoolId = this.getUser().getSchoolId();
            List<Stuentrel> stuRelList = seDao.getBeanListHandlerRunner("select * from STUENTREL" + StaticFields.currentGradeNum + schoolId + " where stuno='" + this.getUser().getUno() + "'", new Stuentrel());
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

}
