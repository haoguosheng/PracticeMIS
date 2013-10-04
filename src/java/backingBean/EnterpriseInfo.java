/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.*;
import java.util.*;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import tools.ForCallBean;
import tools.RepeatPaginator;
import tools.SQLTool;

/**
 *
 * @author Administrator
 */
@ManagedBean
@SessionScoped
public class EnterpriseInfo implements java.io.Serializable {

    private SQLTool<Enterprise> epDao = new SQLTool<Enterprise>();
    private SQLTool<City> cDao = new SQLTool<City>();
    private SQLTool<Enterstudent> esDao = new SQLTool<Enterstudent>();
    private Enterstudent entStu = new Enterstudent();
    Integer id = 0;
    private List<Enterprise> ep;
    private Enterprise enterprise = new Enterprise();
    private int cityId, positionId;
    private int enterpriseid;
    private LinkedHashMap<String, Integer> enterMap = new LinkedHashMap<String, Integer>();
    private String bgcolor;//当不同企业被选择时，为了体现说明存在变化，这里把背景色也给改变
    private String enterName;
    private List<EnterpriseCity> entCityList;
    private String searchName;
    private static boolean isNull = true;
    private RepeatPaginator paginator;

    public synchronized String addEnterprise() {
        User user = new ForCallBean().getUser();
        if (epDao.getBeanListHandlerRunner("select * from Enterprise where name='" + this.enterName + "'",enterprise).size() > 0) {//已经存在这个公司了
            FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage(this.enterName + ",该公司已经存在，不能再添加了"));
        } else {
            this.enterprise.setUserno(new ForCallBean().getUser().getUno());
            epDao.executUpdate("insert into enterprise(name, cityid, enterurl, contactname, contacttelephone, contactaddress, userno) values('"
                    + enterName + "', " + this.cityId + ", '" + this.enterprise.getEnterurl() + "', '" + this.enterprise.getContactname() + "', '"
                    + this.enterprise.getContacttelephone() + "', '" + this.enterprise.getContactaddress() + "', '" + this.enterprise.getUserno() + "')");
            this.entStu.setPositionid(this.positionId);
            this.entStu.setEnterid(Integer.parseInt(epDao.getIdListHandlerRunner("select max(id) from enterprise").get(0)));
            esDao.executUpdate("insert into enterstudent" + user.getSchoolId() + "(enterid, requirement, payment, other, studnum, positionid) values("
                    + this.entStu.getEnterid() + ", '" + this.entStu.getRequirement() + "', '" + this.entStu.getPayment() + "', '" + this.entStu.getOther() + "', " + this.entStu.getStudnum() + ", " + this.entStu.getPositionid() + ")");
            FacesContext.getCurrentInstance().addMessage("latestMessage", new FacesMessage(this.enterName + "添加成功，您可以继续添加"));
            this.enterprise = new Enterprise();
        }
        return null;
    }

    public int getEnterpriseid() {
        return enterpriseid;
    }

    public void setEnterpriseid(int enterpriseid) {
        this.enterpriseid = enterpriseid;
        if (enterpriseid != 0) {
            this.enterprise = epDao.getBeanListHandlerRunner("select * from enterprise where id=" + this.enterpriseid, enterprise).get(0);
        }
    }

    /**
     * @return All the enterprise name in the same city
     */
    public LinkedHashMap<String, Integer> getEnterMap() {
        this.enterMap.clear();
        if (cityId != 0) {
            City temCity = cDao.getBeanListHandlerRunner("select * from city where id=" + cityId, new City()).get(0);
            List<Enterprise> lw = epDao.getBeanListHandlerRunner("select * from enterprise where cityid=" + temCity.getId(), enterprise);
            Iterator<Enterprise> itt = lw.iterator();
            while (itt.hasNext()) {
                Enterprise ent = itt.next();
                this.enterMap.put(ent.getName(), ent.getId());
            }
        }
        return this.enterMap;
    }

    /**
     * @param cityId the cityId to set
     */
    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getEnterName() {
        return enterName;
    }

    public Enterprise getEnterprise() {
        return enterprise;
    }

    /**
     * @return all the enterprise in the same city
     */
    public List<Enterprise> getEp() {
        if (isNull) {
            this.ep = epDao.getBeanListHandlerRunner("select * from enterprise order by cityid", new Enterprise());
        } else if (!isNull ) {
            this.ep = epDao.getBeanListHandlerRunner("select * from enterprise where locate('" + this.searchName + "',name)>0", enterprise);
        }
        return this.ep;
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

    public Enterstudent getEntStu() {
        return entStu;
    }

    public void setEntStu(Enterstudent entStu) {
        this.entStu = entStu;
    }

    /**
     * @return the positionId
     */
    public int getPositionId() {
        return positionId;
    }

    /**
     * @param positionId the positionId to set
     */
    public void setPositionId(int positionId) {
        this.positionId = positionId;
    }
 public void savaEnter(int id, String enName, int cid) {
        this.epDao.executUpdate("update enterprise set name='"+enName+"', cityid="+cid+" where id="+id);
//        Iterator<Enterprise> it=this.ep.iterator();
//        while(it.hasNext()){
//            Enterprise temCity=it.next();
//            if(temCity.getId()==id){
//                temCity.setName(enName);
//                temCity.setCity(city);
//                break;
//            }
//        }
        this.ep = null;
    }

    public void deleteNeed(int id) {
        try {
            this.esDao.executUpdate("delete from enterstudent where id=" + id );
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage("globalMessages", new FacesMessage("删除成功"));
        } catch (Exception e) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage("globalMessages", new FacesMessage("删除失败"));
        }
    }

    public void deleteRow(Enterprise en) {
        try {
            this.epDao.executUpdate("delete from enterprise where id="+en.getId());
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage("globalMessages", new FacesMessage("删除成功"));
        } catch (Exception e) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage("globalMessages", new FacesMessage("此企业已被选择，无法删除"));
        }

//        Iterator<EnterpriseCity> it=this.entCityList.iterator();
//        while(it.hasNext()){
//            EnterpriseCity temCity=it.next();
//            if(temCity.getEnt().getId()==en.getId()){
//                this.entCityList.remove(en);
//                break;
//            }
//        }
        this.ep = null;
        this.entCityList = null;
        paginator = null;
    }

    public void saveNeed(int id, String payment, String requirment, int num, String other, int positionId) {
        this.esDao.executUpdate("update enterstudent set payment='"+ payment+"', Requirement='"+requirment
                +"',Other='"+other+"',Studnum="+num+", positionid="+positionId
                +" where id=" + id);
    }

    /**
     * @return the entCityList
     */
    public List<EnterpriseCity> getEntCityList() {

        if (null == entCityList) {
            entCityList = new LinkedList();
            Iterator<Enterprise> it = this.getEp().iterator();
            while (it.hasNext()) {
                Enterprise tem = it.next();
                EnterpriseCity temec = new EnterpriseCity();
                temec.setEnt(tem);
                temec.setCityId(tem.getCity().getId());
                entCityList.add(temec);
            }
        }
        return entCityList;
    }

    /**
     * @param entCityList the entCityList to set
     */
    public void setEntCityList(List<EnterpriseCity> entCityList) {
        this.entCityList = entCityList;
    }

    /**
     * @return the searchName
     */
    public String getSearchName() {
        return searchName;
    }

    /**
     * @param searchName the searchName to set
     */
    public void setSearchName(String searchName) {
        this.searchName = searchName;
        this.entCityList = null;
    }
public String search() {
        paginator = null;
        isNull = false;
        this.entCityList = null;
        this.ep = null;
        return null;
    }

    public String searchAll() {
        isNull = true;
        searchName = "请输入要选择城市";
        this.entCityList = null;
        paginator = null;
        this.ep = null;
        return null;
    }

    /**
     * @return the paginator
     */
    public RepeatPaginator getPaginator() {
        if (paginator == null) {
            paginator = new RepeatPaginator(this.getEntCityList(), 10);
            paginator.init();
        }
        this.entCityList = null;
        this.ep = null;
        return paginator;
    }
    /**
     * @param paginator the paginator to set
     */
    public void setPaginator(RepeatPaginator paginator) {
        this.paginator = paginator;
    }
}
