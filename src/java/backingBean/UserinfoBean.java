/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.City;
import entities.MyUser;
import entities.Nameofunit;
import entities.Student;
import entities.Teacherinfo;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
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
import sessionBeans.NameofunitFacadeLocal;
import sessionBeans.RoleinfoFacadeLocal;
import sessionBeans.StudentFacadeLocal;
import sessionBeans.TeacherinfoFacadeLocal;
import tools.PublicFields;
import tools.RepeatPaginator;
import tools.StaticFields;

/**
 *
 * @author HgsPc 功能：1）添加一个用户，如教师；2）用户修改信息，包括姓名、密码、邮箱、联系电话等;
 * 该类对应于教师与学生表，是作为这两个表的一个综合
 */
@Named
@SessionScoped
public class UserinfoBean implements java.io.Serializable {

    @Inject
    PublicFields publicFields;
    @EJB
    private StudentFacadeLocal studentEjb;
    @EJB
    private TeacherinfoFacadeLocal teacherEjb;
    @EJB
    private NameofunitFacadeLocal unitEjb;
    @EJB
    private RoleinfoFacadeLocal roleEjb;
    private City studentDistrict;

    private Teacherinfo teacher;
    private Student student;

    private List<Teacherinfo> teacherList;
    private List<Student> studentList;
    private LinkedHashMap<Teacherinfo, String> teacherMap;

    private Part excelFile1;
    private String schoolId, classId;
    private String studentname;
    private int myro[];
    private String successStudentNums;
    private final int columnNum = 6;

    private RepeatPaginator paginator;
    private HttpSession session;
    private MyUser user;

    @PostConstruct
    public void init() {
        session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        int length = (int) (session.getAttribute("userNoLength"));
        if (length == StaticFields.teacherUnoLength) {
            setUser((Teacherinfo) session.getAttribute("teacherUser"));

        } else if (length == StaticFields.stuUnoLength1 || length == StaticFields.stuUnoLength2) {
            setUser((Student) session.getAttribute("studentUser"));
        }
    }

    public LinkedHashMap<Teacherinfo, String> getTeacherMap() {
        if (teacherMap == null) {
            teacherMap = new LinkedHashMap<>();
        }
        teacherMap.clear();
        switch (this.getTeacher().getRoleinfo().getCanseeall()) {
            case StaticFields.CanSeeAll: {
                List<Teacherinfo> userList = teacherEjb.findAll();
                for (Teacherinfo tempUser : userList) {
                    teacherMap.put(tempUser, tempUser.getUno());
                }
            }
            break;
            case StaticFields.CanSeeSelf: {
                teacherMap.put(this.getTeacher(), getTeacher().getUno());
            }
            break;
            case StaticFields.CanSeeOnlySchool: {
                List<Teacherinfo> userList = teacherEjb.getList("select * from teacherinfo  where NAMEOFUNITID='" + this.getTeacher().getNameofunit().getId() + "' order by name");
                for (Teacherinfo tempUser : userList) {
                    teacherMap.put(tempUser, tempUser.getUno());
                }
            }
            break;
            case StaticFields.CanSeeNothing: {
            }
            break;
        }
        return teacherMap;
    }

    public List<Student> getStudentListByCity() {
        if (null != studentDistrict && 0 != studentDistrict.getId()) {
            String sqlString;
            List<Nameofunit> units = new LinkedList<>();
            if (this.user instanceof Teacherinfo) {
                units = this.getUser().getNameofunit().getNameofunitList();//通过学院找班级
            } else if (this.user instanceof Student) {
                //通过班级找学院
                units = this.getUser().getNameofunit().getNameofunit().getNameofunitList();
            }
            String unitsId = "";
            for (Nameofunit unit : units) {
                unitsId += "'" + unit.getId() + "',";
            }
            unitsId = unitsId.length() > 0 ? unitsId.substring(0, unitsId.length() - 1) : "";
            sqlString = "select * from student where UNO in (select stuno from stuentrel where cityid=" + studentDistrict.getId() + ") and NAMEOFUNITID in(" + unitsId + ") order by name ";
            studentList = studentEjb.getList(sqlString);
            if (null != studentList && studentList.size() > 0) {
                return studentList;
            } else {
                FacesContext.getCurrentInstance().addMessage("form2:studentMsg", new FacesMessage("没有满足条件的学生！"));
            }
        } else {
            FacesContext.getCurrentInstance().addMessage("form2:studentMsg", new FacesMessage("请选择城市！"));
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
                    if (unitEjb.getList("select * from NAMEOFUNIT  where ID='" + NAMEOFUNITID + "' order by id").get(0).getNameofunit().getId().equals(schoolId)) {
                        FacesContext.getCurrentInstance().addMessage("OK", new FacesMessage("要导入的数据中第一条并不是指定学院的学生，请检查！"));
                        return null;
                    }
                    successStudentNums = "";
                    for (; i < rownum; i++) {
                        NAMEOFUNITID = sheet.getCell(5, i).getContents();
                        String uno = sheet.getCell(0, i).getContents();
                        List<String> unitList = publicFields.getNameofunitIdList();
                        if (this.getTeacher().getNameofunit().getId().equals(schoolId) && unitList.contains(NAMEOFUNITID)) {
                            uno = "'" + uno + "',";
                            NAMEOFUNITID = "'" + NAMEOFUNITID + "'";//ClassId
                            String password = "'" + sheet.getCell(1, i).getContents() + "',";
                            String NAME = "'" + sheet.getCell(2, i).getContents() + "',";
                            String EMAIL = "'" + sheet.getCell(3, i).getContents() + "',";
                            String PHONE = "'" + sheet.getCell(4, i).getContents() + "',";
                            String ROLEID = StaticFields.studentRole + ",";
                            String sqlString = "INSERT INTO HGS.STUDENT (UNO, PASSWORD, NAME, EMAIL, PHONE, ROLEID, NAMEOFUNITID) VALUES (";
                            sqlString = sqlString + uno + password + NAME + EMAIL + PHONE + ROLEID + NAMEOFUNITID + ")";
                            this.studentEjb.executUpdate(sqlString);
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

    public void setStudent(List<Student> student) {
        this.studentList = student;
    }

    public Part getExcelFile() {
        return excelFile1;
    }

    public void setExcelFile(Part excelFile) {
        this.excelFile1 = excelFile;
    }

    public List<Student> getStudentList() {
        if (null != this.classId) {
            studentList = studentEjb.getList("select * from student where nameofunitid ='" + this.getClassId() + "' order by name");
        }
        return studentList;
    }

    public void save(Student temuser) {
        studentEjb.edit(temuser);
        this.paginator = null;
    }

    public String saveTeacher(Teacherinfo userPara) {
        int assingRole = userPara.getRoleinfo().getPrivilege();
        if (this.getTeacher().getRoleinfo().getPrivilege() > assingRole) {
            FacesContext.getCurrentInstance().addMessage("globalMessages", new FacesMessage("选择的权限不能超越了允许范围！请超级管理员授权！"));
            return null;
        }
        userPara.setRoleinfo(this.getTeacher().getRoleinfo());
        this.teacherEjb.edit(userPara);
        if (userPara.getUno().equals(this.getTeacher().getUno())) {
            try {
                HttpSession mySession = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
                HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
                mySession.invalidate();
                response.sendRedirect("../login/login.xhtml");
            } catch (IOException ex) {
                Logger.getLogger(UserinfoBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    public String deleteRow(Student paraUser) {
        try {
            this.studentEjb.remove(paraUser);
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage("globalMessages", new FacesMessage("此学生有选择的企业，无法删除"));
        }
        this.paginator = null;
        return null;
    }

    public String deleteTeacher(Teacherinfo paraUser) {
        this.teacherEjb.remove(paraUser);
        this.teacherList = null;
        return null;
    }

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
//        teacherList = userDao.getList("select * from teacherinfo  where nameofunitid ='" + schoolId + "'");
    }

    public String addStudent() {
        if (studentEjb.getList("select * from student where uno='" + this.getStudent().getUno() + "' order by name").size() > 0) {
            FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("该学生已经存在，请重新添加！"));
        } else {
            this.getStudent().setPassword(StaticFields.defaultPassword);
            this.getStudent().setRoleinfo(roleEjb.getList("select * from roleinfo where id=2").get(0));
            studentList = studentEjb.getList("select * from student where nameofunitid ='" + this.classId + "' order by name");
        }
        return "studentInfo.xhtml";
    }

    public String addTeacher() {
        if (this.getTeacher().getUno().trim().length() != StaticFields.teacherUnoLength) {
            FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("输入的工号不是6位"));
            return null;
        }
        if (getTeacher().getName().trim().length() == 0) {
            FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("请输入姓名"));
            return null;
        }
        if (teacherEjb.getList("select * from teacherinfo   where uno='" + this.getTeacher().getUno() + "' order by name").size() > 0) {
            FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("该教师已经存在，请重新添加！"));
        } else {
            //String nameofunitId = nameofDao.getList("select * from nameofunit where name='" + nameofunit + "'", new Nameofunit()).get(0).getId();
            if (teacherEjb.executUpdate("insert into teacherinfo  (uno, password,name, roleid, nameofunitid) values('" + this.getTeacher().getUno() + "', '111111','" + getTeacher().getName() + "',1,'" + this.schoolId + "')") > 0) {
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

    public List<Teacherinfo> getTeacherList() {
        String temId;
        if (null == this.schoolId || this.schoolId.trim().length() == 0) {
            temId = this.schoolId;
        } else {
            temId = this.getTeacher().getNameofunit().getId();
        }
        this.teacherList = this.teacherEjb.getList("select * from teacherinfo   where  NAMEOFUNITID='" + temId + "' order by name");
        return teacherList;
    }

    public void setTeacherList(List<Teacherinfo> teacher) {
        this.teacherList = teacher;
    }

    public int[] getRo() {
        myro = new int[]{0, 1, 2};
        return myro;
    }

    public void setRo(int[] ro) {
        this.myro = ro;
    }

    public String submit() {
        //判断是学生还是教师，如果是教师，则选择Teacher表，否则选择Student表
        if (this.user instanceof Student) {
            studentEjb.edit((Student) this.user);
        } else if (this.user instanceof Teacherinfo) {
            teacherEjb.edit((Teacherinfo) this.user);
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

    public Teacherinfo getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacherinfo teacher) {
        this.teacher = teacher;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public MyUser getUser() {
        return user;
    }

    public void setUser(MyUser user) {
        this.user = user;
    }

    public City getStudentDistrict() {
        return studentDistrict;
    }

    public void setStudentDistrict(City studentCity) {
        this.studentDistrict = studentCity;
    }
}
