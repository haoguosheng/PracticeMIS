package entities;

import entitiesBeans.EnterstudentLocal;
import entitiesBeans.PositionLocal;
import entitiesBeans.PracticenoteLocal;
import entitiesBeans.UserLocal;
import java.io.Serializable;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import tools.StaticFields;
import tools.UserAnalysis;

/**
 *
 * @author Administrator
 */
@Named
@SessionScoped
public class Position implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private String name;
    private String pinyin;
    private String userno;
    private User inputor;
    private List<Enterstudent> enterStudList;
    private final EnterstudentLocal esLocal = new EnterstudentLocal();
    private final UserLocal userLocal=new UserLocal();
    private List<Practicenote> practList;
    private final PracticenoteLocal praLocal=new PracticenoteLocal();
    private final PositionLocal myLocal=new PositionLocal();

    public Position() {
    }

    public Position(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public String getUserno() {
        return userno;
    }

    public void setUserno(String userno) {
        this.userno = userno;
    }

    public List<Enterstudent> getEnterStudList() {
        if (enterStudList == null) {
            enterStudList = esLocal.getList("select * from enterstudent" +StaticFields.currentGradeNum+"  where positionId=" + id);
        }
        return enterStudList;
    }

    public void setEnterStudList(List<Enterstudent> enterStudList) {
        this.enterStudList = enterStudList;
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
    public List<Practicenote> getPractList() {
        if (null==practList  ) {
            practList = praLocal.getList("select * from practicenote" +StaticFields.currentGradeNum+ UserAnalysis.getSchoolId(userno) + " where positionid=" + id);
        }
        return practList;
    }
    public void setPractList(List<Practicenote> practList) {
        this.practList = practList;
    }
}
