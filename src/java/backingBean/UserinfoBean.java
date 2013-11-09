/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.Nameofunit;
import entities.Roleinfo;
import entities.User;
import entitiesBeans.UserLocal;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import tools.RepeatPaginator;
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
    private User user;
    private final UserLocal userDao = new UserLocal();
    private int cityId;
    private LinkedHashMap<String, String> studentMap1;
    private Part excelFile1;
    private List<User> studentList;
    private List<User> teacherList;
    private String schoolId, classId;
    private String studentname;
    private int myro[];
    private String successStudentNums;
    private final int columnNum = 6;
    private LinkedHashMap<String, String> teacherMap;
    private RepeatPaginator paginator;
    private User temUser;

    public LinkedHashMap<String, String> getTeacherMap() {
        if (null == teacherMap || teacherMap.isEmpty()) {
            teacherMap = new LinkedHashMap<>();
            switch (getUser().getRoleinfo().getCanseeall()) {
                case StaticFields.CanSeeAll: {
                    List<User> userList = userDao.getList("select * from teacherinfo" + StaticFields.currentGradeNum);
                    for (User tempUser : userList) {
                        teacherMap.put(tempUser.getName(), tempUser.getUno());
                    }
                }
                break;
                case StaticFields.CanSeeSelf: {
                    teacherMap.put(this.getUser().getName(), getUser().getUno());
                }
                break;
                case StaticFields.CanSeeOnlySchool: {
                    List<User> userList = userDao.getList("select * from teacherinfo" + StaticFields.currentGradeNum + " where NAMEOFUNITID='" + getUser().getNameofunitid() + "'");
                    for (User tempUser : userList) {
                        teacherMap.put(tempUser.getName(), tempUser.getUno());
                    }
                }
                break;
                case StaticFields.CanSeeNothing: {

                }
                break;
            }
        }
        return teacherMap;
    }

    public LinkedHashMap<String, String> getStudentMap() {
        if (0 != this.cityId) {
            String sqlString = "select * from student" + StaticFields.currentGradeNum + getUser().getSchoolId() + " where UNO in (select stuno from stuentrel" + StaticFields.currentGradeNum + getUser().getSchoolId() + " where ENTSTUID in (select id from ENTERSTUDENT" + StaticFields.currentGradeNum + " where enterid in(select id from enterprise" + StaticFields.currentGradeNum + " where cityid=" + cityId + ")))";
            List<User> usrList = userDao.getList(sqlString);
            if (null != usrList && usrList.size() > 0) {
                Iterator<User> it = usrList.iterator();
                studentMap1 = new LinkedHashMap<>();
                while (it.hasNext()) {
                    User tem = it.next();
                    studentMap1.put(tem.getName(), tem.getUno());
                }
                return studentMap1;
            } else {
                FacesContext.getCurrentInstance().addMessage("globalMessages", new FacesMessage("没有满足条件的学生！"));
            }
        } else {
            FacesContext.getCurrentInstance().addMessage("globalMessages", new FacesMessage("请选择城市！"));
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
        this.schoolId = schoolId;
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
                    if (!new SQLTool<Nameofunit>().getIdListHandlerRunner("select PARENTID from NAMEOFUNIT" + StaticFields.currentGradeNum + " where ID='" + NAMEOFUNITID + "'").get(0).equals(schoolId)) {
                        FacesContext.getCurrentInstance().addMessage("OK", new FacesMessage("要导入的数据中第一条并不是指定学院的学生，请检查！"));
                        return null;
                    }
                    successStudentNums = "";
                    for (; i < rownum; i++) {
                        NAMEOFUNITID = sheet.getCell(5, i).getContents();
                        String uno = sheet.getCell(0, i).getContents();
                        if (UserAnalysis.getSchoolId(uno).equals(this.schoolId) && PublicFields.getNameofunitIdList().contains(NAMEOFUNITID)) {
                            uno = "'" + uno + "',";
                            NAMEOFUNITID = "'" + NAMEOFUNITID + "'";//ClassId
                            String password = "'" + sheet.getCell(1, i).getContents() + "',";
                            String NAME = "'" + sheet.getCell(2, i).getContents() + "',";
                            String EMAIL = "'" + sheet.getCell(3, i).getContents() + "',";
                            String PHONE = "'" + sheet.getCell(4, i).getContents() + "',";
                            String ROLEID = StaticFields.studentRole + ",";
                            String sqlString = "INSERT INTO HGS.STUDENT" + StaticFields.currentGradeNum + schoolId + " (UNO, PASSWORD, NAME, EMAIL, PHONE, ROLEID, NAMEOFUNITID) VALUES (";
                            sqlString = sqlString + uno + password + NAME + EMAIL + PHONE + ROLEID + NAMEOFUNITID + ")";
                            this.userDao.executUpdate(sqlString);
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
        this.studentList = student;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public Part getExcelFile() {
        return excelFile1;
    }

    public void setExcelFile(Part excelFile) {
        this.excelFile1 = excelFile;
    }

    public List<User> getStudentList() {
        if (null != this.classId) {
            studentList = userDao.getList("select * from student" + StaticFields.currentGradeNum + getUser().getSchoolId() + " where nameofunitid ='" + this.getClassId() + "'");
        }
        return studentList;
    }

    public void save(User temuser) {
        userDao.edit(temuser);
        this.paginator = null;
    }

    public String saveTeacher(User userPara) {
        int assingRole = Integer.valueOf(new SQLTool<Roleinfo>().getIdListHandlerRunner("select Privilege from roleinfo where id=" + userPara.getRoleid()).get(0));
        if (getUser().getRoleinfo().getPrivilege() > assingRole) {
            FacesContext.getCurrentInstance().addMessage("globalMessages", new FacesMessage("选择的权限不能超越了允许范围！请超级管理员授权！"));
            return null;
        }
        this.userDao.executUpdate("update teacherinfo" + StaticFields.currentGradeNum + " set roleId=" + userPara.getRoleid() + ",nameofunitId='" + userPara.getNameofunitid() + "' where uno='" + userPara.getUno() + "'");
        if (userPara.getUno().equals(getUser().getUno())) {
            try {
                FacesContext context = FacesContext.getCurrentInstance();
                HttpSession mySession = (HttpSession) context.getExternalContext().getSession(true);
                HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
                mySession.invalidate();
                response.sendRedirect("../login/login.xhtml");
            } catch (IOException ex) {
                Logger.getLogger(UserinfoBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    public String deleteRow(User paraUser) {
        try {
            this.userDao.remove(paraUser);
            
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage("globalMessages", new FacesMessage("此学生有选择的企业，无法删除"));
        }
        this.paginator = null;
        return null;
    }

    public String deleteTeacher(User paraUser) {
        this.userDao.remove(paraUser);
        this.teacherList=null;
        return null;
    }

//    public void assingValue2Classid(FacesContext context, UIComponent toValidate, Object value) {
//        if (null != value && !"null".equals(value)) {
//            this.setClassId((String) value);
//        }
//    }
    public void assingValue2SchoolId(FacesContext context, UIComponent toValidate, Object value) {
        if (null != value && !"null".equals(value)) {
            this.setSchoolId((String) value);
        }
    }
    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
//        teacherList = userDao.getList("select * from teacherinfo" + StaticFields.currentGradeNum + " where nameofunitid ='" + schoolId + "'");
    }

    public String addStudent() {
        if (userDao.getList("select * from student" + StaticFields.currentGradeNum + getUser().getSchoolId() + " where uno='" + this.getUser().getUno() + "'").size() > 0) {
            FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("该学生已经存在，请重新添加！"));
        } else {
            // String nameofunitId = nameofDao.getList("select * from nameofunit where name='" + nameofunit + "'", new Nameofunit()).get(0).getId();
            if (userDao.executUpdate("insert into student" + getUser().getSchoolId() + "(uno, password,name, roleid, nameofunitid) values('" + this.getUser().getUno() + "', '111111','" + studentname + "',2,'" + this.classId + "')") > 0) {
                //  FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("添加新学生成功！"));
                studentList = userDao.getList("select * from student" + StaticFields.currentGradeNum + getUser().getSchoolId() + " where nameofunitid ='" + this.classId + "'");
            } else {
                FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("请选择班级！"));
            }
        }
        return "studentInfo.xhtml";
    }

    public String addTeacher() {
        if (this.getTemUser().getUno().trim().length() != StaticFields.teacherUnoLength) {
            FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("输入的工号不是6位"));
            return null;
        }
        if (getTemUser().getName().trim().length() == 0) {
            FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("请输入姓名"));
            return null;
        }
        if (userDao.getList("select * from teacherinfo" + StaticFields.currentGradeNum + "  where uno='" + this.getTemUser().getUno() + "'").size() > 0) {
            FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("该教师已经存在，请重新添加！"));
        } else {
            //String nameofunitId = nameofDao.getList("select * from nameofunit where name='" + nameofunit + "'", new Nameofunit()).get(0).getId();
            if (userDao.executUpdate("insert into teacherinfo" + StaticFields.currentGradeNum + " (uno, password,name, roleid, nameofunitid) values('" + this.getTemUser().getUno() + "', '111111','" + getTemUser().getName() + "',1,'" + this.schoolId + "')") > 0) {
                FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("添加新教师成功！"));
                teacherList = null;
            } else {
                FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("请选择学院！"));
            }
        }
        return null;
    }

    public String getStudentname() {
        return studentname;
    }

    public void setStudentname(String studentname) {
        this.studentname = studentname;
    }

    public List<User> getTeacherList() {
        if (null == teacherList || teacherList.isEmpty()) {
            this.teacherList = this.userDao.getList("select * from teacherinfo" + StaticFields.currentGradeNum + "  where  NAMEOFUNITID='" + this.getUser().getNameofunitid() + "'");
        }
        return teacherList;
    }

    public void setTeacherList(List<User> teacher) {
        this.teacherList = teacher;
    }

    public int[] getRo() {
        myro = new int[]{0, 1, 2};
        return myro;
    }

    public void setRo(int[] ro) {
        this.myro = ro;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String submit() {
        //判断是学生还是教师，如果是教师，则选择Teacher表，否则选择Student表
        String tem = UserAnalysis.getTableName(this.getUser().getUno());
        if (tem.contains("studen")) {
            userDao.executUpdate("update " + tem + StaticFields.currentGradeNum + this.getUser().getSchoolId() + " set password = '" + this.getUser().getPassword() + "'  , email='" + this.getUser().getEmail() + "', phone='" + this.getUser().getPhone() + "'   where uno='" + this.getUser().getUno() + "'  ");
        } else if (tem.contains("tea")) {
            userDao.executUpdate("update " + tem + StaticFields.currentGradeNum + " set password = '" + this.getUser().getPassword() + "'  , email='" + this.getUser().getEmail() + "'  , phone='" + this.getUser().getPhone() + "'    where uno='" + this.getUser().getUno() + "'  ");
        }
        return null;
    }

    public RepeatPaginator getPaginator() {
        if (null == paginator && null != this.getStudentList()) {
            paginator = new RepeatPaginator(this.getStudentList(), 10);
        }
        return paginator;
    }

    public void setPaginator(RepeatPaginator paginator) {
        this.paginator = paginator;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
        this.paginator = null;
    }

    public User getTemUser() {
        if (null == this.temUser) {
            temUser = new User();
        }
        return temUser;
    }

    public void setTemUser(User temUser) {
        this.temUser = temUser;
    }
}
