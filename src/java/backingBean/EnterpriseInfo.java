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
    private SQLTool<Position> posDao;
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
    private String enterurl, contactname, contacttelephone, contactaddress;
    private LinkedHashMap<String, Integer> positionMap;

    @PostConstruct
    public void init() {
        epDao = new SQLTool<>();
        cDao = new SQLTool<>();
        esDao = new SQLTool<>();
        posDao = new SQLTool<>();
        enterprise = new Enterprise();
        enterMap = new LinkedHashMap<>();
    }

    public LinkedHashMap<String, Integer> getPositionMap() {
        if (null == this.positionMap || this.positionMap.isEmpty()) {
            this.positionMap = new LinkedHashMap();
            List<Enterstudent> reqStu = esDao.getBeanListHandlerRunner("select * from enterstudent" + StaticFields.currentGradeNum + " where enterId=" + id, new Enterstudent());
            for (int j = 0; j < reqStu.size(); j++) {
                Position tempP = posDao.getBeanListHandlerRunner("select * from position" + StaticFields.currentGradeNum + " where id=" + reqStu.get(j).getPositionid(), new Position()).get(0);
                this.positionMap.put(tempP.getName(), tempP.getId());
            }
        }
        return positionMap;
    }

    public String direct2Need(int ent) {
        this.setEnterpriseid(ent);
        return "enterpriseNeedInfo";
    }

    public synchronized String addEnterprise() {
        if (epDao.getBeanListHandlerRunner("select * from Enterprise" + StaticFields.currentGradeNum + " where name='" + this.enterName + "'", enterprise).size() > 0) {//已经存在这个公司了
            FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage(this.enterName + ",该公司已经存在，不能再添加了"));
        } else {
            User temUser = this.getCheckLogin().getUser();
            epDao.executUpdate("insert into enterprise" + StaticFields.currentGradeNum + " (name, cityid, enterurl, contactname, contacttelephone, contactaddress, userno) values('"
                    + enterName + "', " + this.cityId + ", '" + this.enterurl + "', '" + this.contactname + "', '"
                    + this.contacttelephone + "', '" + this.contactaddress + "', '" + temUser.getUno() + "')");
            // FacesContext.getCurrentInstance().addMessage("latestMessage", new FacesMessage(this.enterName + "添加成功，您可以继续添加"));
            this.added = true;
            //获取id，以便添加需求信息
            this.setEnterpriseid((int) Integer.valueOf(epDao.getIdListHandlerRunner("select * from Enterprise" + StaticFields.currentGradeNum + " where name='" + this.enterName + "'").get(0)));
        }
        return null;
    }
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

    public void deleteRow(Enterprise en) throws Exception {
        this.epDao.executUpdate("delete from enterprise" + StaticFields.currentGradeNum + "  where id=" + en.getId());
        this.setEnterpriseList(null);
        this.entCityList = null;
        paginator = null;
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
        this.searchType = enterAll;
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
        } else {
            this.enterprise = new Enterprise();
        }
        return enterprise;
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

    /**
     * @return the enterurl
     */
    public String getEnterurl() {
        return enterurl;
    }

    /**
     * @param enterurl the enterurl to set
     */
    public void setEnterurl(String enterurl) {
        this.enterurl = enterurl;
    }

    /**
     * @return the contactname
     */
    public String getContactname() {
        return contactname;
    }

    /**
     * @param contactname the contactname to set
     */
    public void setContactname(String contactname) {
        this.contactname = contactname;
    }

    /**
     * @return the contacttelephone
     */
    public String getContacttelephone() {
        return contacttelephone;
    }

    /**
     * @param contacttelephone the contacttelephone to set
     */
    public void setContacttelephone(String contacttelephone) {
        this.contacttelephone = contacttelephone;
    }

    /**
     * @return the contactaddress
     */
    public String getContactaddress() {
        return contactaddress;
    }

    /**
     * @param contactaddress the contactaddress to set
     */
    public void setContactaddress(String contactaddress) {
        this.contactaddress = contactaddress;
    }

    /**
     * @param enterpriseid the enterpriseid to set
     */
    public void setEnterpriseid(int enterpriseid) {
        this.enterpriseid = enterpriseid;
    }
}
