package entities;

import entitiesBeans.EnterpriseLocal;
import entitiesBeans.EnterstudentLocal;
import entitiesBeans.PositionLocal;
import entitiesBeans.StuentrelLocal;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import tools.PublicFields;
import tools.StaticFields;

/**
 *
 * @author Administrator
 */
@Named
@SessionScoped
public class Enterstudent implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private String requirement;
    private String payment;
    private String other;
    private Integer studnum = 0;
    private Integer positionid = 0;
    private Integer enterid = 0;
    private Enterprise enterprise;
    private final EnterpriseLocal entLocal = new EnterpriseLocal();
    private Position position;
    private final PositionLocal posLocal = new PositionLocal();
    private List<Stuentrel> stuEntList;
    private List<Enterstudent> enterStudList;
    private final EnterstudentLocal entstuLocal=new EnterstudentLocal();
    private final StuentrelLocal stuEntLocal = new StuentrelLocal();
    private HashMap<String, Integer> positionMap = new LinkedHashMap<>();

    public Enterstudent() {
    }

    public Enterstudent(Integer id) {
        this.id = id;
    }

    public Enterprise getEnterprise() {
        if (enterprise == null) {
            this.dealId0();
        } else if (null == this.enterprise.getId()) {
            this.dealId0();
        }
        return enterprise;
    }
    /*
     *当对象是内在对象时处理外键Enterprise
     */

    private void dealId0() {
        if (null == this.id) {
            if (null == enterprise) {
                this.enterprise = new Enterprise();
            }
        } else {
            enterprise = entLocal.getList("select * from enterprise" + StaticFields.currentGradeNum + " where id=" + enterid).get(0);
        }
    }

    public void setEnterprise(Enterprise enterprise) {
        this.enterprise = enterprise;
    }

    public Position getPosition() {
        if (position == null) {
            position = posLocal.getList("select * from position" + StaticFields.currentGradeNum + " where id=" + positionid).get(0);
        }
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRequirement() {
        return requirement;
    }

    public void setRequirement(String requirement) {
        this.requirement = requirement;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public int getPositionid() {
        return positionid;
    }

    public void setPositionid(int positionid) {
        this.positionid = positionid;
    }

    public int getEnterid() {
        return enterid;
    }

    public void setEnterid(int enterid) {
        this.enterid = enterid;
    }

    public int getStudnum() {
        return studnum;
    }

    public void setStudnum(int studnum) {
        this.studnum = studnum;
    }
    /*
     *只获得登录者学院的在同一个单位的学生信息
     */

    public List<Stuentrel> getStuEntList(String schoolId) {
        if (null == stuEntList) {
            //获得所有的学院编号
            //查找所有的学生选择企业表
            stuEntLocal.getList("select * from Stuentrel" + StaticFields.currentGradeNum + schoolId + " where entstuid=" + id);
        }
        return stuEntList;
    }

    public void setStuEntList(List<Stuentrel> stuEntList) {
        this.stuEntList = stuEntList;
    }
    public List<Enterstudent> getEnterStudList() {
        this.enterStudList = entstuLocal.getList("select * from Enterstudent" + StaticFields.currentGradeNum + " where enterid=" + this.getEnterprise().getId());
        return enterStudList;
    }

    public HashMap<String, Integer> getPositionMap() {
        if (this.positionMap.isEmpty()) {
            Iterator<Enterstudent> it = this.getEnterStudList().iterator();
            HashMap<Integer, String> temPositionMap = PublicFields.getReversePositionMap();
            while (it.hasNext()) {
                Enterstudent tem = it.next();
                positionMap.put(temPositionMap.get(tem.getPositionid()), tem.getPositionid());
            }
        }
        return positionMap;
    }
}
