/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.Enterprise;
import entities.Stuentrel;
import entities.User;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import tools.ForCallBean;
import tools.SQLTool;

/**
 *
 * @author Idea
 */
@ManagedBean
@SessionScoped
public class StudentSelectEnt implements java.io.Serializable {

    private SQLTool<Stuentrel> seDao = new SQLTool<Stuentrel>();
    private SQLTool<Enterprise> epDao = new SQLTool<Enterprise>();
    private User user;
    private List<Stuentrel> stuForSameEnt, enter4SameStu;//选择同一企业的学生和同一学生选择的不同企业
    private int selectedEnt = 1;//学生选择的企业数目不能超过该值
    private int enterpriseid;
    private Enterprise enterprise;
    private Stuentrel stuEntRel = new Stuentrel();

    public String deleteSelectedEnterprise(String enterId) {
        User sessionuser = getUser();
        List<Stuentrel> liststu = seDao.getBeanListHandlerRunner("select * from stuentrel" + sessionuser.getSchoolId() + " where userid='" + this.getUser().getUno() + "' and enterid=" + enterId, stuEntRel);
        if (liststu.size() > 0) {
            seDao.executUpdate("delete from stuentel" + sessionuser.getSchoolId() + " where id=" + liststu.get(0).getId());
            FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("删除成功"));
        } else {
            FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("删除失败"));
        }
        return "viewSelectedEnt";
    }

    public User getUser() {
        if (null == (new ForCallBean().getReason()) || (new ForCallBean().getReason().intValue() == 1)) {
            new ForCallBean().setReason(0);
        }
        if (null == this.user) {
            this.user = new ForCallBean().getUser();
        }
        return user;
    }

    public List<Stuentrel> getEnter4SameStu() {
        this.enter4SameStu = this.seDao.getBeanListHandlerRunner("select * from stuentrel" + this.getUser().getSchoolId() + "  where stuno='" + this.getUser().getUno() + "'", stuEntRel);
        return enter4SameStu;
    }

    public List<Stuentrel> getStuForSameEnt(String enterId) {
        stuForSameEnt = this.seDao.getBeanListHandlerRunner("select * from Stuentrel" + this.getUser().getSchoolId() + " where ENTERID=" + enterId + " and stuno!='" + this.getUser().getUno() + "'", new Stuentrel());
        return stuForSameEnt;
    }

    public String userAddEnter() {
        if (this.getEnterpriseid() != 0) {
            if (seDao.getBeanListHandlerRunner("select * from STUENTREL" + this.getUser().getSchoolId() + " where stuno='" + this.getUser().getUno() + "'", new Stuentrel()).size() < this.selectedEnt) {
                Stuentrel stu = new Stuentrel();
                this.seDao.executUpdate("insert into stuentrel" + this.getUser().getSchoolId() + "(enterID,stuno) values(" + this.enterprise.getId() + ", '" + this.getUser().getUno() + "')");
                FacesContext.getCurrentInstance().addMessage("myMessage", new FacesMessage("实习单位选择成功！"));
            } else {
                FacesContext.getCurrentInstance().addMessage("myMessage", new FacesMessage("已经选择" + this.selectedEnt + "个实习企业，不能再次选择！"));
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
        this.enterprise = epDao.getBeanListHandlerRunner("select * from enterprise where id=" + this.getEnterpriseid(), new Enterprise()).get(0);
    }

    /**
     * @return the enterprise
     */
    public Enterprise getEnterprise() {
        return enterprise;
    }
}
