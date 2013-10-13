/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.Nameofunit;
import entities.User;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.Part;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import tools.ForCallBean;
import tools.SQLTool;

/**
 *
 * @author HgsPc
 */
@ManagedBean
@SessionScoped
public class UserinfoBean implements java.io.Serializable {

    private SQLTool<User> userDao = new SQLTool<User>();
    private String userno;
    private int cityId;
    private LinkedHashMap<String, String> studentMap;
    private User myUser;
    private Part excelFile;
    private List<User>student;
    private String schoolId;
   
    public UserinfoBean() {
    }

    public LinkedHashMap<String, String> getStudentMap() {
        List<User> usrList = userDao.getBeanListHandlerRunner("select * from student" + getUser().getSchoolId() + " where UNO in (select stuno from stuentrel" + getUser().getSchoolId() + " where ENTERID in (select id from enterprise where cityid=" + cityId + "))", getUser());
        if (null != usrList && usrList.size() > 0) {
            Iterator<User> it = usrList.iterator();
            studentMap = new LinkedHashMap<String, String>();
            while (it.hasNext()) {
                User tem = it.next();
                tem.setSchoolId(myUser.getSchoolId());
                studentMap.put(tem.getName(), tem.getUno());
            }
            return studentMap;
        } else {
            FacesContext.getCurrentInstance().addMessage("globalMessages", new FacesMessage("没有学生！"));
            return null;
        }
    }

    public String submit() {
        userDao.executUpdate("update student" + getUser().getSchoolId() + " set password='" + getUser().getPassword() + "', email='" + getUser().getEmail() + "', name='" + getUser().getName() + "', phone='" + getUser().getPhone() + "', roleId=" + getUser().getRoleid() + ", nameofunitid='" + getUser().getNameofunitid() + "' where uno='" + getUser().getUno() + "'");
        FacesContext.getCurrentInstance().addMessage("globalMessages", new FacesMessage("修改成功！"));
        return null;
    }

    public User getUser() {
        if (null == myUser) {
            this.myUser = new ForCallBean().getUser();
        }
        return myUser;
    }

    public String getUserno() {
        return userno;
    }

    public void setUserno(String userno) {
        this.userno = userno;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    /**
     * @return the excelFile
     */
    public Part getExcelFile() {
        return excelFile;
    }

    /**
     * @param excelFile the excelFile to set
     */
    public void setExcelFile(Part excelFile) {
        this.excelFile = excelFile;
    }

    public String importStudent(String schoolId) {
        try {
            InputStream ins = excelFile.getInputStream();
            Workbook book = Workbook.getWorkbook(ins);
            Sheet sheet = book.getSheet(0);
            int columnum = sheet.getColumns();//得到列数  
            int rownum = sheet.getRows();//得到行数
            if (columnum != 6) {
                FacesContext.getCurrentInstance().addMessage("OK", new FacesMessage("导入出错了，请检查excel是否存在问题！"));
            } else {
                for (int i = 0; i < rownum; i++) {
                    String sqlString = "INSERT INTO HGS.STUDENT" + schoolId + " (UNO, PASSWORD, NAME, EMAIL, PHONE, ROLEID, NAMEOFUNITID) VALUES (";
                    String uno = "'" + sheet.getCell(0, i).getContents() + "',";
                    if (uno.trim().length() == 0) {
                        break;
                    }
                    String password = "'" + sheet.getCell(1, i).getContents() + "',";
                    String NAME = "'" + sheet.getCell(2, i).getContents() + "',";
                    String EMAIL = "'" + sheet.getCell(3, i).getContents() + "',";
                    String PHONE = "'" + sheet.getCell(4, i).getContents() + "',";
                    String ROLEID = "2,";
                    String NAMEOFUNITID = "'" + sheet.getCell(5, i).getContents() + "'";//ClassId
                    sqlString = sqlString + uno + password + NAME + EMAIL + PHONE + ROLEID + NAMEOFUNITID + ")";
                    try {
                        this.userDao.executUpdate(sqlString);
                    } catch (Exception e) {
                        FacesContext.getCurrentInstance().addMessage("OK", new FacesMessage("共导入了"+i+"行，但仍然导入出错了，请检查excel是否存在问题！"));
                    }finally{
                        FacesContext.getCurrentInstance().addMessage("OK", new FacesMessage("共导入了"+i+"行，导入完成！"));
                        book.close();
                    }
                }
            }
        } catch (BiffException ex) {
            FacesContext.getCurrentInstance().addMessage("OK", new FacesMessage("导入出错了，请检查excel是否存在问题！"));
        } catch (IOException ex) {
            FacesContext.getCurrentInstance().addMessage("OK", new FacesMessage("导入出错了，请检查excel是否存在问题！"));
        }
        return null;
    }

    /**
     * @return the student
     */
    public List<User> getStudent() {
        System.err.println("@@@@@@@@@@@@@@@@@@@@@@@@@@"+this.schoolId);
        return student;
    }

    /**
     * @param student the student to set
     */
    public void setStudent(List<User> student) {
        this.student = student;
    }
    
    public void save(String nuo,String nameofunit,String parNameofunit ){
        
    }
    public void delete(User user){
        
    }
  public void check(String cc){
        this.setSchoolId(cc);
     
  }

    /**
     * @return the schoolId
//     */
    public String getSchoolId() {
        return schoolId;
    }

    /**
     * @param schoolId the schoolId to set
     */
    public void setSchoolId(String schoolId) {
        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"+schoolId);
        this.schoolId = schoolId;
          student=userDao.getBeanListHandlerRunner("select * from student" + schoolId + " where nameofunitid in(select id from nameofunit where parentid='"+schoolId+"')", myUser);
    }
 
}
