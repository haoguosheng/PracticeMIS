/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.User;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.Part;
import jxl.Cell;
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
public class UserinfoBean implements Serializable {

    private SQLTool<User> userDao = new SQLTool<User>();
    private String userno;
    private int cityId;
    private LinkedHashMap<String, String> studentMap;
    private User user;
    private Part excelFile;

    public LinkedHashMap<String, String> getStudentMap() {
        List<User> usrList = userDao.getBeanListHandlerRunner("select * from student" + getUser().getSchoolId() + " where UNO in (select stuno from stuentrel" + getUser().getSchoolId() + " where ENTERID in (select id from enterprise where cityid=" + cityId + "))", getUser());
        Iterator<User> it = usrList.iterator();
        studentMap = new LinkedHashMap<String, String>();
        while (it.hasNext()) {
            User tem = it.next();
            tem.setSchoolId(user.getSchoolId());
            studentMap.put(tem.getName(), tem.getUno());
        }
        return studentMap;
    }

    public String submit() {
        userDao.executUpdate("update student" + getUser().getSchoolId() + " set password='" + getUser().getPassword() + "', email='" + getUser().getEmail() + "', name='" + getUser().getName() + "', phone='" + getUser().getPhone() + "', roleId=" + getUser().getRoleid() + ", nameofunitid='" + getUser().getNameofunitid() + "' where uno='" + getUser().getUno() + "'");
        FacesContext.getCurrentInstance().addMessage("globalMessages", new FacesMessage("修改成功！"));
        return null;
    }

    public User getUser() {
        if (null == user) {
            this.user = new ForCallBean().getUser();
        }
        return user;
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

    public String importStudent(String classId, String schoolId) {
        try {
            InputStream ins = excelFile.getInputStream();
            Workbook book = Workbook.getWorkbook(ins);
            Sheet sheet = book.getSheet(0);
            int i = 0;
            while (true) {
                String sqlString = "INSERT INTO HGS.STUDENT0" + schoolId + " (UNO, PASSWORD, NAME, EMAIL, PHONE, ROLEID, NAMEOFUNITID) VALUES (";
                String uno = "'" + sheet.getCell(i, 0).getContents() + "',";
                if (uno.trim().length() == 0) {
                    break;
                }
                String password = "'" + sheet.getCell(i, 1).getContents() + "',";
                String NAME = "'" + sheet.getCell(i, 2).getContents() + "',";
                String EMAIL = "'" + sheet.getCell(i, 3).getContents() + "',";
                String PHONE = "'" + sheet.getCell(i, 4).getContents() + "',";
                String ROLEID = "2,";
                String NAMEOFUNITID = schoolId;
                sqlString = sqlString + uno + password + NAME + EMAIL + PHONE + ROLEID + NAMEOFUNITID + ")";
                try {
                    this.userDao.executUpdate(sqlString);
                } catch (Exception e) {
                    FacesContext.getCurrentInstance().addMessage("OK", new FacesMessage("导入出错了，请检查excel是否存在问题！"));
                }
                i++;
            }
            book.close();
            FacesContext.getCurrentInstance().addMessage("OK", new FacesMessage("导入完成！"));
        } catch (BiffException ex) {
            FacesContext.getCurrentInstance().addMessage("OK", new FacesMessage("导入出错了，请检查excel是否存在问题！"));
        } catch (IOException ex) {
            FacesContext.getCurrentInstance().addMessage("OK", new FacesMessage("导入出错了，请检查excel是否存在问题！"));
        }
        return null;
    }
}