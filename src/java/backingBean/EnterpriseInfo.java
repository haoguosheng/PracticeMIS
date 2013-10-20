/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.*;
import java.util.*;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import tools.RepeatPaginator;
import tools.SQLTool;
import tools.StaticFields;

/**
 *
 * @author Administrator
 */
@Named
@SessionScoped
public class EnterpriseInfo implements java.io.Serializable {

    @Inject
    private CheckLogin checkLogin;
    private SQLTool<Enterprise> epDao;
    private SQLTool<City> cDao;
    private SQLTool<Enterstudent> esDao;
    private Enterstudent entStu;
    Integer id = 0;
    private List<Enterprise> enterpriseList;
    private Enterprise enterprise;
    private int cityId, positionId;
    private int enterpriseid;
    private LinkedHashMap<String, Integer> enterMap;
    private String bgcolor;//当不同企业被选择时，为了体现说明存在变化，这里把背景色也给改变
    private String enterName;
    private List<EnterpriseCity> entCityList;
    private String searchName;
    private RepeatPaginator paginator;
    private int searchType;
    private final int cityEnter = 1, enterAll = 2, searchEnter = 3;
    private boolean added = false;

    @PostConstruct
    public void init() {
        epDao = new SQLTool<>();
        cDao = new SQLTool<>();
        esDao = new SQLTool<>();
        enterprise = new Enterprise();
        enterMap = new LinkedHashMap<>();
        entStu = new Enterstudent();
    }

    public String direct2Need() {
        return "enterpriseNeedInfo";
    }

    public synchronized String addEnterpriseNeed() {
        this.entStu.setPositionid(this.positionId);
        this.entStu.setEnterid(Integer.parseInt(epDao.getIdListHandlerRunner("select max(id) from enterprise" + StaticFields.currentGradeNum).get(0)));
                esDao.executUpdate("insert into enterstudent (enterid, requirement, payment, other, studnum, positionid) values("
                + this.enterpriseid + ", '" + this.entStu.getRequirement() + "', '" + this.entStu.getPayment() + "', '" + this.entStu.getOther() + "', " + this.entStu.getStudnum() + ", " + this.entStu.getPositionid() + ")");
        return null;
    }

    public synchronized String addEnterprise() {
        if (epDao.getBeanListHandlerRunner("select * from Enterprise" + StaticFields.currentGradeNum + " where name='" + this.enterName + "'", enterprise).size() > 0) {//已经存在这个公司了
            FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage(this.enterName + ",该公司已经存在，不能再添加了"));
        } else {
            User temUser = this.getCheckLogin().getUser();
            this.enterprise.setUserno(temUser.getUno());
            epDao.executUpdate("insert into enterprise" + StaticFields.currentGradeNum + " (name, cityid, enterurl, contactname, contacttelephone, contactaddress, userno) values('"
                    + enterName + "', " + this.cityId + ", '" + this.enterprise.getEnterurl() + "', '" + this.enterprise.getContactname() + "', '"
                    + this.enterprise.getContacttelephone() + "', '" + this.enterprise.getContactaddress() + "', '" + this.enterprise.getUserno() + "')");
            // FacesContext.getCurrentInstance().addMessage("latestMessage", new FacesMessage(this.enterName + "添加成功，您可以继续添加"));
            this.enterprise = new Enterprise();
        }
        this.added = true;
        //获取id，以便添加需求信息
        this.enterpriseid = Integer.valueOf(epDao.getIdListHandlerRunner("select * from Enterprise" + StaticFields.currentGradeNum + " where name='" + this.enterName + "'").get(0));
        return null;
    }

//    public void setEnterpriseid(int enterpriseid) {
//        this.enterpriseid = enterpriseid;
//        if (enterpriseid != 0) {
//            this.enterprise = epDao.getBeanListHandlerRunner("select * from enterprise" + StaticFields.currentGradeNum + " where id=" + this.enterpriseid, enterprise).get(0);
//        }
//    }
    public LinkedHashMap<String, Integer> getEnterMap() {
        this.enterMap.clear();
        if (cityId != 0) {
            City temCity = cDao.getBeanListHandlerRunner("select * from city" + StaticFields.currentGradeNum + " where id=" + cityId, new City()).get(0);
            List<Enterprise> lw = epDao.getBeanListHandlerRunner("select * from enterprise" + StaticFields.currentGradeNum + " where cityid=" + temCity.getId(), enterprise);
            Iterator<Enterprise> itt = lw.iterator();
            while (itt.hasNext()) {
                Enterprise ent = itt.next();
                this.enterMap.put(ent.getName(), ent.getId());
            }
        }
        return this.enterMap;
    }

    private String[] color = {"A", "B", "C", "D", "E", "F"};
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

    public void setEnterName(String enterName) {
        this.enterName = enterName;
    }

    public int getCityId() {
        return cityId;
    }

    public void savaEnter(int id, String enName, int cid) {
        this.epDao.executUpdate("update enterprise" + StaticFields.currentGradeNum + "  set name='" + enName + "', cityid=" + cid + " where id=" + id);
        this.setEnterpriseList(null);
    }

    public String deleteNeed(int id) {
        this.esDao.executUpdate("delete from enterstudent" + StaticFields.currentGradeNum + "  where id=" + id);
        this.enterprise.setEnterstudentList(null);
        return null;
    }

    public void deleteRow(Enterprise en) throws Exception {
        this.epDao.executUpdate("delete from enterprise" + StaticFields.currentGradeNum + "  where id=" + en.getId());
        this.setEnterpriseList(null);
        this.entCityList = null;
        paginator = null;
    }

    public void saveNeed(int id, String payment, String requirment, int num, String other, int positionId) {
        this.esDao.executUpdate("update enterstudent" + StaticFields.currentGradeNum + "  set payment='" + payment + "', Requirement='" + requirment
                + "',Other='" + other + "',Studnum=" + num + ", positionid=" + positionId
                + " where id=" + id);
    }

    public List<EnterpriseCity> getEntCityList() {
        if (this.cityId == 0 && (null == this.searchName || this.searchName.trim().equals(""))) {
            return new LinkedList();
        } else {
            entCityList = new LinkedList();
            Iterator<Enterprise> it = this.getEnterpriseList().iterator();
            while (it.hasNext()) {
                Enterprise tem = it.next();
                EnterpriseCity temec = new EnterpriseCity();
                temec.setEnt(tem);
                temec.setCityId(tem.getCityId());
                entCityList.add(temec);
            }
            return entCityList;
        }
    }

    public void setEntCityList(List<EnterpriseCity> entCityList) {
        this.entCityList = entCityList;
    }

    public String getSearchName() {
        return searchName;
    }

    public void setSearchName(String searchName) {
        this.searchName = searchName;
    }

    public String search() {
        this.searchType = this.searchEnter;
        return null;
    }

    public String searchAll() {
        searchName = "请输入要选择的单位";
        this.searchType = this.enterAll;
        return null;
    }

    public RepeatPaginator getPaginator() {
        List<EnterpriseCity> tem = this.getEntCityList();
        paginator = new RepeatPaginator(tem, 10);
        paginator.init();
        return paginator;
    }

    public String toAddEnterprise() {
        return "addEnterpriseInfo";
    }

    public void setPaginator(RepeatPaginator paginator) {
        this.paginator = paginator;
    }

    public Enterstudent getEntStu() {
        return entStu;
    }

    public void setEntStu(Enterstudent entStu) {
        this.entStu = entStu;
    }

    public int getPositionId() {
        return positionId;
    }

    public void setPositionId(int positionId) {
        this.positionId = positionId;
    }

    public CheckLogin getCheckLogin() {
        return checkLogin;
    }

    public void setCheckLogin(CheckLogin checkLogin) {
        this.checkLogin = checkLogin;
    }

    public int getEnterpriseid() {
        return enterpriseid;
    }

    public void setCityId(int cityId) {
        this.searchType = this.cityEnter;
        this.cityId = cityId;
    }

    public String getEnterName() {
        return enterName;
    }

    public Enterprise getEnterprise() {
        if (this.enterpriseid != 0) {
            this.enterprise = epDao.getBeanListHandlerRunner("select * from Enterprise" + StaticFields.currentGradeNum + " where id=" + this.enterpriseid, enterprise).get(0);
            return enterprise;
        } else {
            return null;
        }
    }

    /**
     * @return the enterpriseList
     */
    public List<Enterprise> getEnterpriseList() {
        switch (searchType) {
            case enterAll:
                this.setEnterpriseList(epDao.getBeanListHandlerRunner("select * from enterprise", new Enterprise()));
                break;
            case cityEnter:
                this.setEnterpriseList(epDao.getBeanListHandlerRunner("select * from enterprise" + StaticFields.currentGradeNum + " where cityId=" + this.cityId, enterprise));
                break;
            case searchEnter:
                this.setEnterpriseList(epDao.getBeanListHandlerRunner("select * from enterprise" + StaticFields.currentGradeNum + " where locate('" + this.searchName + "',name)>0", enterprise));
                break;
        }
        return enterpriseList;
    }

    /**
     * @param enterpriseList the enterpriseList to set
     */
    public void setEnterpriseList(List<Enterprise> enterpriseList) {
        this.enterpriseList = enterpriseList;
    }

    /**
     * @return the added
     */
    public boolean isAdded() {
        return added;
    }
}
