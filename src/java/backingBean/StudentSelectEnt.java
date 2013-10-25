/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.Enterprise;
import entities.Enterstudent;
import entities.Stuentrel;
import entities.User;
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
    private SQLTool<Enterprise> epDao;
    private User myUser;
    private List<Stuentrel> stuForSameEnt, enter4SameStu;//选择同一企业的学生和同一学生选择的不同企业
    private int enterpriseid;
    private Enterprise enterprise;
    private Stuentrel stuEntRel;

    @PostConstruct
    public void init() {
        seDao = new SQLTool<>();
        epDao = new SQLTool<>();
        esDao = new SQLTool<>();
        stuEntRel = new Stuentrel();
    }

    public String deleteSelectedEnterprise(String id) {
        User sessionuser = getUser();
        seDao.executUpdate("delete from stuentrel" + StaticFields.currentGradeNum + sessionuser.getSchoolId() + " where id=" + id);
        return null;
    }

    public User getUser() {
        if (null == this.myUser) {
            this.myUser = checkLogin.getUser();
        }
        return myUser;
    }

    public List<Stuentrel> getEnter4SameStu() {
        this.enter4SameStu = this.seDao.getBeanListHandlerRunner("select * from stuentrel" + StaticFields.currentGradeNum + this.getUser().getSchoolId() + "  where stuno='" + this.getUser().getUno() + "'", stuEntRel);
        for (Stuentrel s : enter4SameStu) {
            s.setSchoolId(this.getUser().getSchoolId());
        }
        return enter4SameStu;
    }

    public List<Stuentrel> getStuForSameEnt(String entstuId) {
        stuForSameEnt = this.seDao.getBeanListHandlerRunner("select * from Stuentrel" + StaticFields.currentGradeNum + checkLogin.getUser().getSchoolId() + " where entstuid=" + entstuId + " and stuno!='" + this.getUser().getUno() + "'", new Stuentrel());
        for (Stuentrel s : stuForSameEnt) {
            s.setSchoolId(this.getUser().getSchoolId());
        }
        return stuForSameEnt;
    }

    public String userAddEnter(String enterid, String posiId) {
        if (this.getEnterpriseid() != 0) {
            if (seDao.getBeanListHandlerRunner("select * from STUENTREL" + StaticFields.currentGradeNum + this.getUser().getSchoolId() + " where stuno='" + this.getUser().getUno() + "'", new Stuentrel()).size() < StaticFields.selectedEnt) {
//                Stuentrel stu = new Stuentrel();
                this.seDao.executUpdate("insert into stuentrel" + StaticFields.currentGradeNum + this.getUser().getSchoolId() + "(entstuID,stuno) values(" + esDao.getBeanListHandlerRunner("select * from enterstudent where enterid=" + enterid + " and positionid=" + posiId, new Enterstudent()).get(0).getId() + ", '" + this.getUser().getUno() + "')");
                //  FacesContext.getCurrentInstance().addMessage("myMessage", new FacesMessage("实习单位选择成功！"));
            } else {
                FacesContext.getCurrentInstance().addMessage("myMessage", new FacesMessage("已经选择" + StaticFields.selectedEnt + "个实习企业，不能再次选择！"));
            }
        } else {
            FacesContext.getCurrentInstance().addMessage("myMessage", new FacesMessage("请先选择实习单位"));
        }

        return null;
    }

    /**
     * @return the enterpriseid
     */
    public int getEnterpriseid() {
        return enterpriseid;
    }

    /**
     * @param enterpriseid the enterpriseid to set
     */
    public void setEnterpriseid(int enterpriseid) {
        this.enterpriseid = enterpriseid;
        this.enterprise = epDao.getBeanListHandlerRunner("select * from enterprise" + StaticFields.currentGradeNum + " where id=" + this.getEnterpriseid(), new Enterprise()).get(0);
        this.enterprise.setSchoolId(this.getUser().getSchoolId());
    }

    /**
     * @return the enterprise
     */
    public Enterprise getEnterprise() {
        return enterprise;
    }

    /**
     * @return the checkLogin
     */
    public CheckLogin getCheckLogin() {
        return checkLogin;
    }

    /**
     * @param checkLogin the checkLogin to set
     */
    public void setCheckLogin(CheckLogin checkLogin) {
        this.checkLogin = checkLogin;
    }
}
