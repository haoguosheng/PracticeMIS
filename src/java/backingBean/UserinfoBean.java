/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.Checkrecords;
import entities.Nameofunit;
import entities.Practicenote;
import entities.Roleinfo;
import entities.Stuentrel;
import entities.User;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.Part;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import tools.PublicFields;
import tools.SQLTool;
import tools.StaticFields;
import tools.UserAnalysis;

/**
 *
 * @author HgsPc
 */
@Named
@SessionScoped
public class UserinfoBean implements java.io.Serializable {

    @Inject
    private CheckLogin checkLogin;
    private SQLTool<User> userDao1;
    private SQLTool<Practicenote> praDao;
    private SQLTool<Checkrecords> chkDao1;
    private SQLTool<Stuentrel> stuRelDao1;
    private SQLTool<Nameofunit> nameofDao1;
    private SQLTool<Roleinfo> roleofDao1;
    private String userno;
    private int cityId1;
    private LinkedHashMap<String, String> studentMap1;
    private Part excelFile1;
    private List<User> student1;
    private List<User> teacher1;
    private String schoolId1;
    private String studentname1;
    private String classId1;
    private int myro[];
    private LinkedHashMap<String, Integer> roo;
    private String successStudentNums;
    private int columnNum=6;

    @PostConstruct
    public void init() {
        userDao1 = new SQLTool<>();
        praDao = new SQLTool<>();
        chkDao1 = new SQLTool<>();
        stuRelDao1 = new SQLTool<>();
        nameofDao1 = new SQLTool<>();
        roleofDao1=new SQLTool<>();
    }

    public LinkedHashMap<String, String> getStudentMap() {
        List<User> usrList = userDao1.getBeanListHandlerRunner("select * from student" + StaticFields.currentGradeNum + this.getCheckLogin().getUser().getSchoolId() + " where UNO in (select stuno from stuentrel" + StaticFields.currentGradeNum + this.getCheckLogin().getUser().getSchoolId() + " where ENTERID in (select id from enterprise" + StaticFields.currentGradeNum + " where cityid=" + cityId1 + "))", this.getCheckLogin().getUser());
        if (null != usrList && usrList.size() > 0) {
            Iterator<User> it = usrList.iterator();
            studentMap1 = new LinkedHashMap<String, String>();
            while (it.hasNext()) {
                User tem = it.next();
                tem.setSchoolId(this.getCheckLogin().getUser().getSchoolId());
                studentMap1.put(tem.getName(), tem.getUno());
            }
            return studentMap1;
        } else {
            FacesContext.getCurrentInstance().addMessage("globalMessages", new FacesMessage("没有学生！"));
            return null;
        }
    }

    public String submit() {
        userDao1.executUpdate("update student" + StaticFields.currentGradeNum + this.getCheckLogin().getUser().getSchoolId() + " set password='" + this.getCheckLogin().getUser().getPassword() + "', email='" + this.getCheckLogin().getUser().getEmail() + "', name='" + this.getCheckLogin().getUser().getName() + "', phone='" + this.getCheckLogin().getUser().getPhone() + "', roleId=" + this.getCheckLogin().getUser().getRoleid() + ", nameofunitid='" + this.getCheckLogin().getUser().getNameofunitid() + "' where uno='" + this.getCheckLogin().getUser().getUno() + "'");
        // FacesContext.getCurrentInstance().addMessage("globalMessages", new FacesMessage("修改成功！"));
        return null;
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
        if (null == this.excelFile1 || this.getFilename(excelFile1).trim().length() == 0) {
            FacesContext.getCurrentInstance().addMessage("OK", new FacesMessage("请先选择要导入的Excel！"));
            return null;
        }
        this.schoolId1 = schoolId;
        try {
            InputStream ins = excelFile1.getInputStream();
            Workbook book = Workbook.getWorkbook(ins);
            Sheet sheet = book.getSheet(0);
            int columnum = sheet.getColumns();//得到列数  
            int rownum = sheet.getRows();//得到行数
            if (columnum != columnNum) {
                FacesContext.getCurrentInstance().addMessage("OK", new FacesMessage("导入出错了，excel表格不是"+columnNum+"列！"));
            } else {
                int i = 1;
                try {
                    String NAMEOFUNITID = sheet.getCell(5, i).getContents();
                    if (!nameofDao1.getIdListHandlerRunner("select PARENTID from NAMEOFUNIT"+StaticFields.currentGradeNum+" where ID='" + NAMEOFUNITID + "'").get(0).equals(schoolId)) {
                        FacesContext.getCurrentInstance().addMessage("OK", new FacesMessage("要导入的数据中第一条并不是指定学院的学生，请检查！"));
                        return null;
                    }
                    successStudentNums = "";
                    for (; i < rownum; i++) {
                        NAMEOFUNITID = sheet.getCell(5, i).getContents();
                        String uno = sheet.getCell(0, i).getContents();
                        if (UserAnalysis.getSchoolId(uno).equals(this.schoolId1) && PublicFields.getUnitIdList().contains(NAMEOFUNITID)) {
                            uno = "'" + uno + "',";
                            NAMEOFUNITID = "'" + NAMEOFUNITID + "'";//ClassId
                            String password = "'" + sheet.getCell(1, i).getContents() + "',";
                            String NAME = "'" + sheet.getCell(2, i).getContents() + "',";
                            String EMAIL = "'" + sheet.getCell(3, i).getContents() + "',";
                            String PHONE = "'" + sheet.getCell(4, i).getContents() + "',";
                            String ROLEID = "2,";
                            String sqlString = "INSERT INTO HGS.STUDENT" + StaticFields.currentGradeNum + schoolId + " (UNO, PASSWORD, NAME, EMAIL, PHONE, ROLEID, NAMEOFUNITID) VALUES (";
                            sqlString = sqlString + uno + password + NAME + EMAIL + PHONE + ROLEID + NAMEOFUNITID + ")";
                            this.userDao1.executUpdate(sqlString);
                            successStudentNums = successStudentNums + uno;
                        } else {
                            FacesContext.getCurrentInstance().addMessage("OK", new FacesMessage("学号为：" + uno + "的记录导入出错了，原因是数据库中不存在对应的学院表或班级编号错误！"));
                        }
                    }
                } catch (Exception e) {
                    FacesContext.getCurrentInstance().addMessage("OK", new FacesMessage("共导入了" + i + "行，但仍然导入出错了，请检查excel是否存在问题！"));
                } finally {
                    book.close();
                }
            }
        } catch (BiffException | IOException ex) {
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
        this.student1 = student;
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
        return cityId1;
    }

    public void setCityId(int cityId) {
        this.cityId1 = cityId;
    }

    public Part getExcelFile() {
        return excelFile1;
    }

    public void setExcelFile(Part excelFile) {
        this.excelFile1 = excelFile;
    }

    /**
     * @return the student
     */
    public List<User> getStudent() {
        if(null!=this.classId1){
            student1 = userDao1.getBeanListHandlerRunner("select * from student"+StaticFields.currentGradeNum+ this.getCheckLogin().getUser().getSchoolId() + " where nameofunitid ='" + classId1 + "'", this.getCheckLogin().getUser());
        }
        return student1;
    }

    public void save(String uno, String nameofunit) {
        //String id = nameofDao.getBeanListHandlerRunner("select * from nameofunit where name='" + nameofunit + "'", new Nameofunit()).get(0).getId();
        if (this.userDao1.executUpdate("update student"+StaticFields.currentGradeNum + this.getCheckLogin().getUser().getSchoolId() + " set nameofunitId='" + nameofunit + "' where uno='" + uno + "'") > 0) {
            //  FacesContext context = FacesContext.getCurrentInstance();
            // context.addMessage("globalMessages", new FacesMessage("修改成功"));
        }
    }

    public void saveTeacher(String uno, String roleid, String nameofunit) {
        //String id = nameofDao.getBeanListHandlerRunner("select * from nameofunit where name='" + nameofunit + "'", new Nameofunit()).get(0).getId();
        if (this.userDao1.executUpdate("update teacherinfo"+StaticFields.currentGradeNum+" set roleId=" + roleid + ",nameofunitId='" + nameofunit + "' where uno='" + uno + "'") > 0) {
            //  FacesContext context = FacesContext.getCurrentInstance();
            // context.addMessage("globalMessages", new FacesMessage("修改成功"));
        }
        checkschool(schoolId1);
    }

    public String direct2BackupImport() {
        return "importStudent";
    }

    public String deleteRow(User user) {
        if (userDao1.executUpdate("delete from student"+StaticFields.currentGradeNum+"" + this.getCheckLogin().getUser().getSchoolId() + " where uno='" + user.getUno() + "'") > 0) {
            //   FacesContext context = FacesContext.getCurrentInstance();
            //   context.addMessage("globalMessages", new FacesMessage("删除成功"));
        } else {
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage("globalMessages", new FacesMessage("此学生有选择的企业，无法删除"));
        }
        return null;
    }

    public String deleteTeacher(User user) {
        if (userDao1.executUpdate("delete from teacherinfo"+StaticFields.currentGradeNum+"  where uno='" + user.getUno() + "'") > 0) {
//            FacesContext context = FacesContext.getCurrentInstance();
//            context.addMessage("globalMessages", new FacesMessage("删除成功"));
        } else {
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage("globalMessages", new FacesMessage("此老师有学生选择，删除失败"));
        }
        checkschool(schoolId1);
        return null;
    }

    public void assingValue2Classid(FacesContext context, UIComponent toValidate, Object value) {
        if (null != value && !"null".equals(value)) {
            this.setClassId((String) value);
        }
    }
    public void assingValue2SchoolId (FacesContext context, UIComponent toValidate, Object value) {
        if (null != value && !"null".equals(value)) {
            this.setSchoolId((String) value);
        }
    }

    public void checkschool(String cc) {
        this.setSchoolId(cc);

    }

    /**
     * @return the schoolId //
     */
    public String getSchoolId() {
        return schoolId1;
    }

    /**
     * @param schoolId the schoolId to set
     */
    public void setSchoolId(String schoolId) {
        this.schoolId1 = schoolId;
        teacher1 = userDao1.getBeanListHandlerRunner("select * from teacherinfo"+StaticFields.currentGradeNum+" where nameofunitid ='" + schoolId + "'", this.getCheckLogin().getUser());
    }

    public String addStudent() {
        if (userDao1.getBeanListHandlerRunner("select * from student"+StaticFields.currentGradeNum+ this.getCheckLogin().getUser().getSchoolId() + " where uno='" + userno + "'", this.getCheckLogin().getUser()).size() > 0) {
            FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("该学生已经存在，请重新添加！"));
        } else {
            // String nameofunitId = nameofDao.getBeanListHandlerRunner("select * from nameofunit where name='" + nameofunit + "'", new Nameofunit()).get(0).getId();
            if (userDao1.executUpdate("insert into student" + this.getCheckLogin().getUser().getSchoolId() + "(uno, password,name, roleid, nameofunitid) values('" + userno + "', '111111','" + studentname1 + "',2,'" + classId1 + "')") > 0) {
                //  FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("添加新学生成功！"));
                student1 = userDao1.getBeanListHandlerRunner("select * from student"+StaticFields.currentGradeNum+ this.getCheckLogin().getUser().getSchoolId() + " where nameofunitid ='" + classId1 + "'", this.getCheckLogin().getUser());
            } else {
                FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("请选择班级！"));
            }
        }
        return "studentInfo.xhtml";
    }

    public String addTeacher() {
        if(userno.trim().length()!=StaticFields.teacherUnoLength1){
             FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("输入的工号不是6位"));
             return null;
        }
        if (userDao1.getBeanListHandlerRunner("select * from teacherinfo"+StaticFields.currentGradeNum+"  where uno='" + userno + "'", this.getCheckLogin().getUser()).size() > 0) {
            FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("该教师已经存在，请重新添加！"));
        } else {
            //String nameofunitId = nameofDao.getBeanListHandlerRunner("select * from nameofunit where name='" + nameofunit + "'", new Nameofunit()).get(0).getId();
            if (userDao1.executUpdate("insert into teacherinfo"+StaticFields.currentGradeNum+" (uno, password,name, roleid, nameofunitid) values('" + userno + "', '111111','" + studentname1 + "',1,'" + this.schoolId1 + "')") > 0) {
                FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("添加新教师成功！"));
                teacher1 = userDao1.getBeanListHandlerRunner("select * from teacherinfo"+StaticFields.currentGradeNum+" where nameofunitid ='" + schoolId1 + "'", this.getCheckLogin().getUser());
            } else {
                FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("请选择学院！"));
            }
        }
        return null;
    }

    /**
     * @return the studentname
     */
    public String getStudentname() {
        return studentname1;
    }

    /**
     * @param studentname the studentname to set
     */
    public void setStudentname(String studentname) {
        this.studentname1 = studentname;
    }


    /**
     * @return the teacher
     */
    public List<User> getTeacher() {
        return teacher1;
    }

    /**
     * @param teacher the teacher to set
     */
    public void setTeacher(List<User> teacher) {
        this.teacher1 = teacher;
    }

    /**
     * @return the classId
     */
    public String getClassId() {
        return classId1;
    }

    /**
     * @param classId the classId to set
     */
    public void setClassId(String classId) {
        this.classId1 = classId;
    }

    /**
     * @return the ro
     */
    public int[] getRo() {
        myro = new int[]{0, 1, 2};
        return myro;
    }

    /**
     * @param ro the ro to set
     */
    public void setRo(int[] ro) {
        this.myro = ro;
    }

    /**
     * @return the roo
     */
    public LinkedHashMap<String, Integer> getRoo() {
        List<Roleinfo> role = roleofDao1.getBeanListHandlerRunner("select * from roleinfo"+StaticFields.currentGradeNum+"", new Roleinfo());
        if (null != role && role.size() > 0) {
            Iterator<Roleinfo> it = role.iterator();
            roo = new LinkedHashMap<String, Integer>();
            while (it.hasNext()) {
                Roleinfo ro1 = it.next();
                roo.put(ro1.getName(), ro1.getId());
            }
        }
        return roo;
    }

    /**
     * @param roo the roo to set
     */
    public void setRoo(LinkedHashMap<String, Integer> roo) {
        this.roo = roo;
    }

    /**
     * @return the checkLogin
     */
    public CheckLogin getCheckLogin() {
        return checkLogin;
    }

    /**
     * @param checkLogin the checkLogin to set
     */
    public void setCheckLogin(CheckLogin checkLogin) {
        this.checkLogin = checkLogin;
    }
}
