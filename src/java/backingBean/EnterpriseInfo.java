package backingBean;

import entities.*;
import java.util.*;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import sessionBeans.EnterpriseFacadeLocal;
import tools.RepeatPaginator;
import tools.StaticFields;

/**
 *
 * @author 维护与实习与见习单位相关的信息 通过城市可以查看企业及企业需求
 */
@Named
@SessionScoped
public class EnterpriseInfo implements java.io.Serializable {

    @EJB
    private EnterpriseFacadeLocal enterpriseLocal;
    private Enterprise enterprise = new Enterprise();
    private Enterprise myaddEnterprise = new Enterprise();
    private City district;
    private Integer enterId;//在selectMyEnterprise.xhtml中set该值，并得到对应单位的需求信息

    private List<Enterprise> enterpriseList;
    private List<Enterstudent> enterStuList = new LinkedList<>();
    private LinkedHashMap<String, Integer> enterMap = new LinkedHashMap<>();
    private String bgcolor;//当不同企业被选择时，为了体现说明存在变化，这里把背景色也给改变
    private String searchName;
    private RepeatPaginator paginator;
    private int searchType;//, cityId;
    private final int cityEnter = 1, enterAll = 2, searchEnter = 3;
    private boolean added = false;
    HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
    /*
     *功能：添加企业
     */
    MyUser user;

    @PostConstruct
    public void init() {
        int length = (int) (session.getAttribute("userNoLength"));
        if (length == StaticFields.teacherUnoLength) {
            user = (Teacherinfo) session.getAttribute("teacherUser");

        } else if (length == StaticFields.stuUnoLength1 || length == StaticFields.stuUnoLength2) {
            user = (Student) session.getAttribute("studentUser");
        }
    }

    public synchronized String addEnterprise() {
        if (enterpriseLocal.getList("select * from Enterprise  where name='" + this.myaddEnterprise.getName() + "'").size() > 0) {//已经存在这个公司了
            FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage(this.myaddEnterprise.getName() + ",该公司已经存在，不能再添加了"));
        } else {
            this.myaddEnterprise.setUserno(user.getUno());
            enterpriseLocal.create(this.myaddEnterprise);
            // FacesContext.getCurrentInstance().addMessage("latestMessage", new FacesMessage(this.enterName + "添加成功，您可以继续添加"));
            this.added = true;
            //获取id，以便添加需求信息
            this.myaddEnterprise.setId(enterpriseLocal.getList("select * from Enterprise  where name='" + this.myaddEnterprise.getName() + "'").get(0).getId());
            session.setAttribute("myenterprise", this.myaddEnterprise);
        }
        return null;
    }

    public LinkedHashMap<String, Integer> getEnterMap() {
        enterMap.clear();
        if (district != null && district.getId() != 0) {
            enterpriseList = enterpriseLocal.getList("select * from enterprise  where cityid=" + district.getId() + " order by cityid");
            Iterator<Enterprise> it = enterpriseList.iterator();
            while (it.hasNext()) {
                Enterprise ent = it.next();
                enterMap.put(ent.getName(), ent.getId());
            }
        }
        return enterMap;
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

    public Enterprise getEnterprise() {
        return enterprise;
    }

    public void setEnterprise(Enterprise enterprise) {
        this.enterprise = enterprise;
    }

    public Enterprise getMyaddEnterprise() {
        return myaddEnterprise;
    }

    public void setMyaddEnterprise(Enterprise myaddEnterprise) {
        this.myaddEnterprise = myaddEnterprise;
    }

    public List<Enterprise> getEnterpriseList() {
        switch (searchType) {
            case enterAll:
                this.setEnterpriseList(enterpriseLocal.findAll());
                break;
            case cityEnter:
                this.setEnterpriseList(enterpriseLocal.getList("select * from enterprise  where cityId=" + this.district.getId() + " order by cityid"));
                break;
            case searchEnter:
                this.setEnterpriseList(enterpriseLocal.getList("select * from enterprise  where locate('" + this.searchName + "',name)>0 order by cityid"));
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

    public boolean isAdded() {
        return added;
    }

    public City getDistrict() {
        return district;
    }
    private int oldCityId;

    public void setDistrict(City district) {
        this.district = district;
        if (null != district.getId()) {
            if (district.getId()!=oldCityId) {
                oldCityId = district.getId();
                this.paginator = null;
            }
            this.searchType = cityEnter;
        }
    }

    public boolean buttonShowOrNot(Enterprise ent) {
        return ent.getUserno().equals(user.getUno());

    }

    public Integer getEnterId() {
        return enterId;
    }

    public void setEnterId(Integer enterId) {
        this.setEnterprise(enterprise = enterpriseLocal.find(enterId));
        this.enterId = enterId;
    }
}
