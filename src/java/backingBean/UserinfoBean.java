/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.Nameofunit;
import entities.Roleinfo;
import entities.User;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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
    private SQLTool<Nameofunit> nameofDao1;
    private SQLTool<Roleinfo> roleofDao1;
    private String userno;
    private int cityId1;
    private LinkedHashMap<String, String> studentMap1;
    private Part excelFile1;
    private List<User> student1;
    private List<User> teacherList;
    private String schoolId1;
    private String studentname1;
    private String classId1;
    private int myro[];
    private String successStudentNums;
    private int columnNum = 6;

    @PostConstruct
    public void init() {
        userDao1 = new SQLTool<>();
        nameofDao1 = new SQLTool<>();
        roleofDao1 = new SQLTool<>();
    }

    private User getCheckLoginUser() {
        return this.getCheckLogin().getUser();
    }

    public LinkedHashMap<String, String> getStudentMap() {
        List<User> usrList = userDao1.getBeanListHandlerRunner("select * from student" + StaticFields.currentGradeNum + getCheckLoginUser().getSchoolId() + " where UNO in (select stuno from stuentrel" + StaticFields.currentGradeNum + getCheckLoginUser().getSchoolId() + " where ENTERID in (select id from enterprise" + StaticFields.currentGradeNum + " where cityid=" + cityId1 + "))", getCheckLoginUser());
        if (null != usrList && usrList.size() > 0) {
            Iterator<User> it = usrList.iterator();
            studentMap1 = new LinkedHashMap<String, String>();
            while (it.hasNext()) {
                User tem = it.next();
                tem.setSchoolId(getCheckLoginUser().getSchoolId());
                studentMap1.put(tem.getName(), tem.getUno());
            }
            return studentMap1;
        } else {
            FacesContext.getCurrentInstance().addMessage("globalMessages", new FacesMessage("没有学生！"));
            return null;
        }
    }

    public String submit() {
        userDao1.executUpdate("update student" + StaticFields.currentGradeNum + getCheckLoginUser().getSchoolId() + " set password='" + getCheckLoginUser().getPassword() + "', email='" + getCheckLoginUser().getEmail() + "', name='" + getCheckLoginUser().getName() + "', phone='" + getCheckLoginUser().getPhone() + "', roleId=" + getCheckLoginUser().getRoleid() + ", nameofunitid='" + getCheckLoginUser().getNameofunitid() + "' where uno='" + getCheckLoginUser().getUno() + "'");
        // FacesContext.getCurrentInstance().addMessage("globalMessages", new FacesMessage("修改成功！"));
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
                FacesContext.getCurrentInstance().addMessage("OK", new FacesMessage("导入出错了，excel表格不是" + columnNum + "列！"));
            } else {
                int i = 1;
                try {
                    String NAMEOFUNITID = sheet.getCell(5, i).getContents();
                    if (!nameofDao1.getIdListHandlerRunner("select PARENTID from NAMEOFUNIT" + StaticFields.currentGradeNum + " where ID='" + NAMEOFUNITID + "'").get(0).equals(schoolId)) {
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
                            String ROLEID = PublicFields.getStudentRole()+",";
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
        if (null != this.classId1) {
            student1 = userDao1.getBeanListHandlerRunner("select * from student" + StaticFields.currentGradeNum + getCheckLoginUser().getSchoolId() + " where nameofunitid ='" + classId1 + "'", getCheckLoginUser());
        }
        return student1;
    }

    public void save(String uno, String nameofunitId) {
        this.userDao1.executUpdate("update student" + StaticFields.currentGradeNum + getCheckLoginUser().getSchoolId() + " set nameofunitId='" + nameofunitId + "' where uno='" + uno + "'");

    }
     public String saveTeacher(String uno,int roleId,String nameofUnitId) {
        int assingRole = Integer.valueOf(this.roleofDao1.getIdListHandlerRunner("select Privilege from roleinfo where id=" + roleId).get(0));
        if (getCheckLoginUser().getRoleinfo().getPrivilege() > assingRole) {
            FacesContext.getCurrentInstance().addMessage("globalMessages", new FacesMessage("选择的权限不能超越了允许范围！请超级管理员授权！"));
            return null;
        }
        this.userDao1.executUpdate("update teacherinfo" + StaticFields.currentGradeNum + " set roleId=" + roleId+ ",nameofunitId='" + nameofUnitId + "' where uno='" + uno + "'");
        checkschool(schoolId1);
        if (uno.equals(getCheckLoginUser().getUno())) {
            try {
                FacesContext context = FacesContext.getCurrentInstance();
                HttpSession mySession = (HttpSession) context.getExternalContext().getSession(true);
                HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
                mySession.invalidate();
                response.sendRedirect("../login/login.xhtml");
            } catch (IOException ex) {
                Logger.getLogger(UserinfoBean.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    public String direct2BackupImport() {
        return "importStudent";
    }

    public String deleteRow(User user) {
        if (userDao1.executUpdate("delete from student" + StaticFields.currentGradeNum + "" + getCheckLoginUser().getSchoolId() + " where uno='" + user.getUno() + "'") > 0) {
            //   FacesContext context = FacesContext.getCurrentInstance();
            //   context.addMessage("globalMessages", new FacesMessage("删除成功"));
        } else {
            FacesContext.getCurrentInstance().addMessage("globalMessages", new FacesMessage("此学生有选择的企业，无法删除"));
        }
        return null;
    }

    public String deleteTeacher(User user) {
        if (userDao1.executUpdate("delete from teacherinfo" + StaticFields.currentGradeNum + "  where uno='" + user.getUno() + "'") > 0) {
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

    public void assingValue2SchoolId(FacesContext context, UIComponent toValidate, Object value) {
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
        teacherList = userDao1.getBeanListHandlerRunner("select * from teacherinfo" + StaticFields.currentGradeNum + " where nameofunitid ='" + schoolId + "'", getCheckLoginUser());
    }

    public String addStudent() {
        if (userDao1.getBeanListHandlerRunner("select * from student" + StaticFields.currentGradeNum + getCheckLoginUser().getSchoolId() + " where uno='" + userno + "'", getCheckLoginUser()).size() > 0) {
            FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("该学生已经存在，请重新添加！"));
        } else {
            // String nameofunitId = nameofDao.getBeanListHandlerRunner("select * from nameofunit where name='" + nameofunit + "'", new Nameofunit()).get(0).getId();
            if (userDao1.executUpdate("insert into student" + getCheckLoginUser().getSchoolId() + "(uno, password,name, roleid, nameofunitid) values('" + userno + "', '111111','" + studentname1 + "',2,'" + classId1 + "')") > 0) {
                //  FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("添加新学生成功！"));
                student1 = userDao1.getBeanListHandlerRunner("select * from student" + StaticFields.currentGradeNum + getCheckLoginUser().getSchoolId() + " where nameofunitid ='" + classId1 + "'", getCheckLoginUser());
            } else {
                FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("请选择班级！"));
            }
        }
        return "studentInfo.xhtml";
    }

    public String addTeacher() {
        if (userno.trim().length() != StaticFields.teacherUnoLength1) {
            FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("输入的工号不是6位"));
            return null;
        }
        if (studentname1.trim().length() == 0) {
            FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("请输入姓名"));
            return null;
        }
        if (userDao1.getBeanListHandlerRunner("select * from teacherinfo" + StaticFields.currentGradeNum + "  where uno='" + userno + "'", getCheckLoginUser()).size() > 0) {
            FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("该教师已经存在，请重新添加！"));
        } else {
            //String nameofunitId = nameofDao.getBeanListHandlerRunner("select * from nameofunit where name='" + nameofunit + "'", new Nameofunit()).get(0).getId();
            if (userDao1.executUpdate("insert into teacherinfo" + StaticFields.currentGradeNum + " (uno, password,name, roleid, nameofunitid) values('" + userno + "', '111111','" + studentname1 + "',1,'" + this.schoolId1 + "')") > 0) {
                FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("添加新教师成功！"));
                teacherList = userDao1.getBeanListHandlerRunner("select * from teacherinfo" + StaticFields.currentGradeNum + " where nameofunitid ='" + schoolId1 + "'", getCheckLoginUser());
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
    public List<User> getTeacherList() {
        if (this.checkLogin.getUser().getRoleinfo().getCanseeall() != StaticFields.CanSeeAll) {
            this.teacherList = this.userDao1.getBeanListHandlerRunner("select * from teacherinfo" + StaticFields.currentGradeNum + "  where  NAMEOFUNITID='" + this.checkLogin.getUser().getNameofunitid() + "'", new User());
        }
        return teacherList;
    }

    /**
     * @param teacher the teacher to set
     */
    public void setTeacherList(List<User> teacher) {
        this.teacherList = teacher;
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
