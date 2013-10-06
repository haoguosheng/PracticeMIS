/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.Checkrecords;
import entities.Practicenote;
import entities.Stuentrel;
import entities.User;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
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
import tools.ApplicationForCallBean;
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
    private SQLTool<Practicenote> praDao = new SQLTool<Practicenote>();
    private SQLTool<Checkrecords> chkDao = new SQLTool<Checkrecords>();
    private SQLTool<Stuentrel> stuRelDao = new SQLTool<Stuentrel>();
    private String userno;
    private int cityId;
    private LinkedHashMap<String, String> studentMap;
    private User myUser;
    private Part excelFile;
    private List<User> student;
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

    public String backupStu(String schoolId, String gradeNum) {
        if (null != schoolId) {
            //备份学生学号的前2位等于传入的参数：gradeNum
            //数据先取出，再插入新表
            //先取所学号作为外键的表，再取学生表；
            String practicNoteSelectString = "select * from HGS.PRACTICENOTE" + schoolId + " where substr(stuno,1,2)='" + gradeNum + "'";
            String checkSelectString = "select * from HGS.CHECKRECORDS" + schoolId + " where substr(stuno,1,2)='" + gradeNum + "'";
            String stuEntRelSelectString = "select * from HGS.STUENTREL" + schoolId + " where substr(stuno,1,2)='" + gradeNum + "'";
            String stuSelectString = "select * from HGS.STUDENT" + schoolId + " where substr(uno,1,2)='" + gradeNum + "'";

            List<Practicenote> praList = praDao.getBeanListHandlerRunner(practicNoteSelectString, new Practicenote());
            Iterator<Practicenote> it = praList.iterator();
            while (it.hasNext()) {
                Practicenote tem = new Practicenote();
                //创建一个新表
                String createPra = ("CREATE TABLE PRACTICENOTE" + gradeNum) + schoolId + " (ID INTEGER NOT NULL, DETAIL VARCHAR(2000), SUBMITDATE DATE DEFAULT date(current_date) , ENTERID INTEGER, POSITIONID INTEGER, STUNO VARCHAR(10), PRIMARY KEY (ID))";
                praDao.executUpdate(createPra);
                //把旧的数据插入新表
                String insPra = "INSERT INTO HGS.PRACTICENOTE" + schoolId + " (DETAIL, SUBMITDATE, ENTERID, POSITIONID, STUNO) VALUES ('"
                        + tem.getDetail() + "', '" + tem.getSubmitdate() + "', " + tem.getEnterid() + "," + tem.getPositionid() + ",'" + tem.getStuno() + "')";
                praDao.executUpdate(insPra);
                //把旧的数据从旧表中删除
                praDao.executUpdate(insPra);

            }
        } else {
            FacesContext.getCurrentInstance().addMessage("OK", new FacesMessage("请指定要备份的学生所在的学院！"));
        }
        return null;
    }

    public String restorStu(String schoolId) {
        if (null != schoolId) {
        } else {
            FacesContext.getCurrentInstance().addMessage("OK", new FacesMessage("请指定要恢复的学生所在的学院！"));
        }
        return null;
    }

    public String importStudent(String schoolId) {
        if (null != schoolId) {
            try {
                InputStream ins = excelFile.getInputStream();
                Workbook book = Workbook.getWorkbook(ins);
                Sheet sheet = book.getSheet(0);
                int columnum = sheet.getColumns();//得到列数  
                int rownum = sheet.getRows();//得到行数
                if (columnum != 6) {
                    FacesContext.getCurrentInstance().addMessage("OK", new FacesMessage("导入出错了，请检查excel是否存在问题！"));
                } else {
                    int i = 0;
                    try {
                        for (; i < rownum; i++) {
                            String uno = "";
                            String sqlString = "INSERT INTO HGS.STUDENT" + schoolId + " (UNO, PASSWORD, NAME, EMAIL, PHONE, ROLEID, NAMEOFUNITID) VALUES (";
                            String NAMEOFUNITID = "'" + sheet.getCell(5, i).getContents() + "'";//ClassId
                            uno = "'" + sheet.getCell(0, i).getContents() + "',";
                            if (ApplicationForCallBean.getUnitIdList().contains(NAMEOFUNITID)) {
                                String password = "'" + sheet.getCell(1, i).getContents() + "',";
                                String NAME = "'" + sheet.getCell(2, i).getContents() + "',";
                                String EMAIL = "'" + sheet.getCell(3, i).getContents() + "',";
                                String PHONE = "'" + sheet.getCell(4, i).getContents() + "',";
                                String ROLEID = "2,";
                                sqlString = sqlString + uno + password + NAME + EMAIL + PHONE + ROLEID + NAMEOFUNITID + ")";
                                this.userDao.executUpdate(sqlString);
                            } else {
                                FacesContext.getCurrentInstance().addMessage("OK", new FacesMessage(",学号为：" + uno + "的记录导入出错了，原因是班级编号错误！"));
                            }
                        }
                    } catch (Exception e) {
                        FacesContext.getCurrentInstance().addMessage("OK", new FacesMessage("共导入了" + i + "行，但仍然导入出错了，请检查excel是否存在问题！"));
                    } finally {
                        FacesContext.getCurrentInstance().addMessage("OK", new FacesMessage("共导入了" + i + "行，导入完成！"));
                        book.close();
                    }
                }
            } catch (BiffException ex) {
                FacesContext.getCurrentInstance().addMessage("OK", new FacesMessage("导入出错了，请检查excel是否存在问题！"));
            } catch (IOException ex) {
                FacesContext.getCurrentInstance().addMessage("OK", new FacesMessage("导入出错了，请检查excel是否存在问题！"));
            }
        } else {
            FacesContext.getCurrentInstance().addMessage("OK", new FacesMessage("请指定要导入的学生所在的学院！"));
        }
        return null;
    }

    public List<User> getStudent() {
        return student;
    }

    public void setStudent(List<User> student) {
        this.student = student;
    }

    public void save(String nuo, String nameofunit, String parNameofunit) {
    }

    public void delete(User user) {
    }

    public void check(String cc) {
        this.setSchoolId(cc);
    }

    /**
     * @return the schoolId //
     */
    public String getSchoolId() {
        return schoolId;
    }

    /**
     * @param schoolId the schoolId to set
     */
    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
        student = userDao.getBeanListHandlerRunner("select * from student" + schoolId + " where nameofunitid in(select id from nameofunit where parentid='" + schoolId + "')", myUser);
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

    public Part getExcelFile() {
        return excelFile;
    }

    public void setExcelFile(Part excelFile) {
        this.excelFile = excelFile;
    }
}
