/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.Checkrecords;
import entities.Nameofunit;
import entities.Practicenote;
import entities.Stuentrel;
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
import tools.ApplicationForCallBean;
import tools.ForCallBean;
import tools.SQLTool;
import tools.StaticFields;
import tools.UserAnalysis;

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
    private SQLTool<Nameofunit> nameofDao = new SQLTool<Nameofunit>();
    private String userno;
    private int cityId;
    private LinkedHashMap<String, String> studentMap;
    private User myUser;
    private Part excelFile;
    private List<User> student;
    private List<User> teacher;
    private String schoolId;
    private String studentname;
    private String nameofunit;
    private String classId;
    private String successStudentNums;

    public UserinfoBean() {
    }

    public LinkedHashMap<String, String> getStudentMap() {
        List<User> usrList = userDao.getBeanListHandlerRunner("select * from student" + StaticFields.currentGradeNum + getUser().getSchoolId() + " where UNO in (select stuno from stuentrel" + StaticFields.currentGradeNum + getUser().getSchoolId() + " where ENTERID in (select id from enterprise" + StaticFields.currentGradeNum + " where cityid=" + cityId + "))", getUser());
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
        userDao.executUpdate("update student" + StaticFields.currentGradeNum + getUser().getSchoolId() + " set password='" + getUser().getPassword() + "', email='" + getUser().getEmail() + "', name='" + getUser().getName() + "', phone='" + getUser().getPhone() + "', roleId=" + getUser().getRoleid() + ", nameofunitid='" + getUser().getNameofunitid() + "' where uno='" + getUser().getUno() + "'");
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
            String practicNoteSelectString = "select * from HGS.PRACTICENOTE" + StaticFields.currentGradeNum + schoolId + " where substr(stuno,1,2)='" + gradeNum + "'";
            String checkSelectString = "select * from HGS.CHECKRECORDS" + StaticFields.currentGradeNum + schoolId + " where substr(stuno,1,2)='" + gradeNum + "'";
            String stuEntRelSelectString = "select * from HGS.STUENTREL" + StaticFields.currentGradeNum + schoolId + " where substr(stuno,1,2)='" + gradeNum + "'";
            String stuSelectString = "select * from HGS.STUDENT" + StaticFields.currentGradeNum + schoolId + " where substr(uno,1,2)='" + gradeNum + "'";

            List<Practicenote> praList = praDao.getBeanListHandlerRunner(practicNoteSelectString, new Practicenote());
            Iterator<Practicenote> it = praList.iterator();
            while (it.hasNext()) {
                Practicenote tem = new Practicenote();
                //创建一个新表
                String createPra = ("CREATE TABLE PRACTICENOTE" + gradeNum) + schoolId + " (ID INTEGER NOT NULL, DETAIL VARCHAR(2000), SUBMITDATE DATE DEFAULT date(current_date) , ENTERID INTEGER, POSITIONID INTEGER, STUNO VARCHAR(10), PRIMARY KEY (ID))";
                praDao.executUpdate(createPra);
                //把旧的数据插入新表
                String insPra = "INSERT INTO HGS.PRACTICENOTE" + gradeNum + "" + schoolId + " (DETAIL, SUBMITDATE, ENTERID, POSITIONID, STUNO) VALUES ('"
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
        if (null == schoolId || "null".equals(schoolId)) {
            FacesContext.getCurrentInstance().addMessage("OK", new FacesMessage("请选择学院"));
            return null;
        }
        if (null == this.excelFile || this.getFilename(excelFile).trim().length() == 0) {
            FacesContext.getCurrentInstance().addMessage("OK", new FacesMessage("请先选择要导入的Excel！"));

            return null;
        }
        try {
            InputStream ins = excelFile.getInputStream();
            Workbook book = Workbook.getWorkbook(ins);
            Sheet sheet = book.getSheet(0);
            int columnum = sheet.getColumns();//得到列数  
            int rownum = sheet.getRows();//得到行数
            if (columnum != 6) {
                FacesContext.getCurrentInstance().addMessage("OK", new FacesMessage("导入出错了，excel表格不是6列！"));
            } else {
                int i = 1;
                try {
                    String NAMEOFUNITID = sheet.getCell(5, i).getContents();
                    if (!nameofDao.getIdListHandlerRunner("select PARENTID from NAMEOFUNIT where ID='" + NAMEOFUNITID + "'").get(0).equals(schoolId)) {
                        FacesContext.getCurrentInstance().addMessage("OK", new FacesMessage("要导入的数据中第一条并不是指定学院的学生，请检查！"));
                        return null;
                    }
                    successStudentNums = "";
                    for (; i <= rownum; i++) {
                        NAMEOFUNITID = sheet.getCell(5, i).getContents();
                        String uno = sheet.getCell(0, i).getContents();
                        if (UserAnalysis.getSchoolId(uno).equals(this.schoolId) && ApplicationForCallBean.getUnitIdList().contains(NAMEOFUNITID)) {
                            uno = "'" + sheet.getCell(0, i).getContents() + "',";
                            NAMEOFUNITID = "'" + NAMEOFUNITID + "'";//ClassId
                            String password = "'" + sheet.getCell(1, i).getContents() + "',";
                            String NAME = "'" + sheet.getCell(2, i).getContents() + "',";
                            String EMAIL = "'" + sheet.getCell(3, i).getContents() + "',";
                            String PHONE = "'" + sheet.getCell(4, i).getContents() + "',";
                            String ROLEID = "2,";
                            String sqlString = "INSERT INTO HGS.STUDENT" + StaticFields.currentGradeNum + schoolId + " (UNO, PASSWORD, NAME, EMAIL, PHONE, ROLEID, NAMEOFUNITID) VALUES (";
                            sqlString = sqlString + uno + password + NAME + EMAIL + PHONE + ROLEID + NAMEOFUNITID + ")";
                            this.userDao.executUpdate(sqlString);
                            successStudentNums = successStudentNums + uno;
                        } else {
                            FacesContext.getCurrentInstance().addMessage("OK", new FacesMessage("学号为：" + uno + "的记录导入出错了，原因是数据库中不存在对应的学院表或班级编号错误！"));
                        }
                        FacesContext.getCurrentInstance().addMessage("OK", new FacesMessage("导入结束！"));
                    }
                } catch (Exception e) {
                    FacesContext.getCurrentInstance().addMessage("OK", new FacesMessage((successStudentNums.trim().length() == 0 ? "导入出错" : "下面这几个同学(学号)" + successStudentNums + "的信息导入成功，其他的导入出错了，在继续导入之前，请把这几个同学的信息从Excel中删除。") + "，请检查excel是否存在问题。"));
                } finally {
                    book.close();
                }
            }
        } catch (BiffException ex) {
            FacesContext.getCurrentInstance().addMessage("OK", new FacesMessage("导入出错了，请检查excel是否存在问题！"));
        } catch (IOException ex) {
            FacesContext.getCurrentInstance().addMessage("OK", new FacesMessage("导入出错了，请检查excel是否存在问题！"));
        }
        return null;
    }

    private String getFilename(Part part) {
        for (String cd : part.getHeader("content-disposition").split(";")) {
            if (cd.trim().startsWith("filename")) {
                String filename = cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
                return filename.substring(filename.lastIndexOf('/') + 1).substring(filename.lastIndexOf('\\') + 1); // MSIE fix.
            }
        }
        return null;
    }

    public void setStudent(List<User> student) {
        this.student = student;
    }

    public void save(String nuo, String nameofunit, String parNameofunit) {
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

    /**
     * @return the student
     */
    public List<User> getStudent() {
        return student;
    }

    public void save(String uno, String nameofunit) {
        String id = nameofDao.getBeanListHandlerRunner("select * from nameofunit where name='" + nameofunit + "'", new Nameofunit()).get(0).getId();
        this.userDao.executUpdate("update student" + getUser().getSchoolId() + " set nameofunitId='" + id + "' where uno='" + uno + "'");
        checkclass(classId);
    }

    public void saveTeacher(String uno, String nameofunit) {
        String id = nameofDao.getBeanListHandlerRunner("select * from nameofunit where name='" + nameofunit + "'", new Nameofunit()).get(0).getId();
        this.userDao.executUpdate("update teacherinfo set nameofunitId='" + id + "' where uno='" + uno + "'");
        checkschool(schoolId);
    }

    public String deleteRow(User user) {
        if (userDao.executUpdate("delete from student" + getUser().getSchoolId() + " where uno='" + user.getUno() + "'") > 0) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage("globalMessages", new FacesMessage("删除成功"));
        } else {
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage("globalMessages", new FacesMessage("此学生有选择的企业，无法删除"));
        }
        checkclass(classId);
        return null;
    }

    public String deleteTeacher(User user) {
        if (userDao.executUpdate("delete from teacherinfo  where uno='" + user.getUno() + "'") > 0) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage("globalMessages", new FacesMessage("删除成功"));
        } else {
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage("globalMessages", new FacesMessage("删除失败"));
        }
        checkschool(schoolId);
        return null;
    }

    public void checkclass(String cc) {
        this.setClassId(cc);

    }

    public void checkschool(String cc) {
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
        teacher = userDao.getBeanListHandlerRunner("select * from teacherinfo where nameofunitid ='" + schoolId + "'", getUser());
    }

    public String addStudent() {
        if (userDao.getBeanListHandlerRunner("select * from student" + getUser().getSchoolId() + " where uno='" + userno + "'", getUser()).size() > 0) {
            FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("该学生已经存在，请重新添加！"));
        } else {
            String nameofunitId = nameofDao.getBeanListHandlerRunner("select * from nameofunit where name='" + nameofunit + "'", new Nameofunit()).get(0).getId();
            userDao.executUpdate("insert into student" + getUser().getSchoolId() + "(uno, password,name, roleid, nameofunitid) values('" + userno + "', '111111','" + studentname + "',2,'" + nameofunitId + "')");
            FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("添加新学生成功！"));

        }
        return null;
    }

    public String addTeacher() {
        if (userDao.getBeanListHandlerRunner("select * from teacherinfo  where uno='" + userno + "'", getUser()).size() > 0) {
            FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("该教师已经存在，请重新添加！"));
        } else {
            String nameofunitId = nameofDao.getBeanListHandlerRunner("select * from nameofunit where name='" + nameofunit + "'", new Nameofunit()).get(0).getId();
            userDao.executUpdate("insert into teacherinfo (uno, password,name, roleid, nameofunitid) values('" + userno + "', '111111','" + studentname + "',1,'" + nameofunitId + "')");
            FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("添加新教师成功！"));

        }
        return null;
    }

    /**
     * @return the studentname
     */
    public String getStudentname() {
        return studentname;
    }

    /**
     * @param studentname the studentname to set
     */
    public void setStudentname(String studentname) {
        this.studentname = studentname;
    }

    /**
     * @return the nameofunit
     */
    public String getNameofunit() {
        return nameofunit;
    }

    /**
     * @param nameofunit the nameofunit to set
     */
    public void setNameofunit(String nameofunit) {
        this.nameofunit = nameofunit;
    }

    /**
     * @return the teacher
     */
    public List<User> getTeacher() {
        return teacher;
    }

    /**
     * @param teacher the teacher to set
     */
    public void setTeacher(List<User> teacher) {
        this.teacher = teacher;
    }

    /**
     * @return the classId
     */
    public String getClassId() {
        return classId;
    }

    /**
     * @param classId the classId to set
     */
    public void setClassId(String classId) {
        this.classId = classId;
        student = userDao.getBeanListHandlerRunner("select * from student" + getUser().getSchoolId() + " where nameofunitid ='" + classId + "'", getUser());
    }
}
