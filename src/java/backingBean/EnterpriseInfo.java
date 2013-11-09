package backingBean;

import entities.*;
import entitiesBeans.EnterpriseLocal;
import java.util.*;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import tools.RepeatPaginator;
import tools.StaticFields;

/**
 *
 * @author 维护与实习与见习单位相关的信息 通过城市可以查看企业及企业需求
 */
@Named
@SessionScoped
public class EnterpriseInfo implements java.io.Serializable {

    private @Inject
    Enterprise enterprise;
    private @Inject
    User user;
    private String cityId;
    private String enterId;//在selectMyEnterprise.xhtml中set该值，并得到对应单位的需求信息
    private final EnterpriseLocal enterpriseLocal = new EnterpriseLocal();
    private List<Enterprise> enterpriseList;
    private List<Enterstudent> enterStuList;
    private LinkedHashMap<String, Integer> enterMap;
    private String bgcolor;//当不同企业被选择时，为了体现说明存在变化，这里把背景色也给改变
    private String searchName;
    private RepeatPaginator paginator;
    private int searchType;//, cityId;
    private final int cityEnter = 1, enterAll = 2, searchEnter = 3;
    private boolean cityChange = false;
    private boolean added = false;
    HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
    /*
     *功能：添加企业
     */

    public synchronized String addEnterprise() {
        if (enterpriseLocal.getList("select * from Enterprise" + StaticFields.currentGradeNum + " where name='" + this.enterprise.getName() + "'").size() > 0) {//已经存在这个公司了
            FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage(this.enterprise.getName() + ",该公司已经存在，不能再添加了"));
        } else {
            this.enterprise.setUserno(this.getUser().getUno());
            enterpriseLocal.create(this.enterprise);
            // FacesContext.getCurrentInstance().addMessage("latestMessage", new FacesMessage(this.enterName + "添加成功，您可以继续添加"));
            this.added = true;
            //获取id，以便添加需求信息
            this.enterprise.setId(enterpriseLocal.getList("select * from Enterprise" + StaticFields.currentGradeNum + " where name='" + this.enterprise.getName() + "'").get(0).getId());
            session.setAttribute("myenterprise", this.enterprise);
        }
        return null;
    }

    public LinkedHashMap<String, Integer> getEnterMap() {
        if (null != this.cityId) {
            if (cityChange) {
                if (null != this.enterMap) {
                    this.enterMap.clear();
                }else{
                    this.enterMap=new LinkedHashMap<>();
                }
                List<Enterprise> lw = this.getEnterpriseList();
                Iterator<Enterprise> itt = lw.iterator();
                while (itt.hasNext()) {
                    Enterprise ent = itt.next();
                    this.enterMap.put(ent.getName(), ent.getId());
                }
                cityChange = false;
            }
        }
        return this.enterMap;
    }
    private final String[] color = {"A", "B", "C", "D", "E", "F"};
    Random rand = new Random();

    public String getBgcolor() {
        String randColor;
        this.bgcolor = "#";
        for (int i = 0; i < 3; i++) {
            randColor = this.color[rand.nextInt(5)];
            this.bgcolor = this.bgcolor + randColor + randColor;
        }
        return bgcolor;
    }

    public void savaEnter(Enterprise enterTem) {
        this.enterpriseLocal.edit(enterTem);
        paginator = null;
    }

    public void delete(Enterprise en) throws Exception {
        this.enterpriseLocal.remove(en);
        paginator = null;
    }

    public String getSearchName() {
        return searchName;
    }

    public void setSearchName(String searchName) {
        this.searchName = searchName;
    }

    public String search() {
        paginator = null;
        this.searchType = this.searchEnter;
        return null;
    }

    public String searchAll() {
        paginator = null;
        this.searchType = enterAll;
        return null;
    }

    public RepeatPaginator getPaginator() {
        if (null == paginator) {
            paginator = new RepeatPaginator(getEnterpriseList(), StaticFields.pageSize);
        }
        return paginator;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Enterprise getEnterprise() {
        return enterprise;
    }

    public List<Enterprise> getEnterpriseList() {
        switch (searchType) {
            case enterAll:
                this.setEnterpriseList(enterpriseLocal.findAll());
                break;
            case cityEnter:
                this.setEnterpriseList(enterpriseLocal.getList("select * from enterprise" + StaticFields.currentGradeNum + " where cityId=" + this.cityId));
                break;
            case searchEnter:
                this.setEnterpriseList(enterpriseLocal.getList("select * from enterprise" + StaticFields.currentGradeNum + " where locate('" + this.searchName + "',name)>0"));
                break;
            default:
                this.setEnterpriseList(new LinkedList());
        }
        return enterpriseList;
    }

    public void setEnterpriseList(List<Enterprise> enterpriseList) {
        this.enterpriseList = enterpriseList;
    }

    public List<Enterstudent> getEnterStuList() {
        if (null != this.getEnterprise()) {
            enterStuList = this.getEnterprise().getEnterstudentList();
        }
        return enterStuList;
    }

    public void setEnterprise(Enterprise enterprise) {
        this.enterprise = enterprise;
    }

    public boolean isAdded() {
        return added;
    }

    public String getCityId() {
        return cityId;
    }
    private String oldCityId = "";

    public void setCityId(String cityId) {
        this.cityId = cityId;
        if (null != cityId) {
            if (!cityId.equals(oldCityId)) {
                oldCityId = cityId;
                cityChange = true;
                this.paginator = null;
            }
            this.searchType = cityEnter;
        }
    }

    public boolean buttonShowOrNot(Enterprise ent) {
        return ent.getInputor().getUno().equals(user.getUno());
    }

    public String getEnterId() {

        return enterId;
    }

    public void setEnterId(String enterId) {
        this.setEnterprise(enterprise = enterpriseLocal.find(enterId));
        this.enterId = enterId;
    }
}
