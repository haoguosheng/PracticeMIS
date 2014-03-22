/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.Checkrecords;
import entities.City;
import entities.Practicenotes;
import entities.Student;
import entities.Stuentrel;
import entities.Teacherinfo;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import sessionBeans.CheckrecordsFacadeLocal;
import sessionBeans.PracticenotesFacadeLocal;
import sessionBeans.StudentFacadeLocal;
import sessionBeans.StuentrelFacadeLocal;
import sessionBeans.TeacherinfoFacadeLocal;
import tools.StaticFields;

/**
 *
 * @author Administrator 功能：为教师录入实习检查而服务的
 */
@Named
@SessionScoped
public class CheckRecordBean4Student implements Serializable {

    @EJB
    private CheckrecordsFacadeLocal checkEjb;
    @EJB
    private PracticenotesFacadeLocal practiceNoteEjb;
    @EJB
    private StuentrelFacadeLocal stuentrelEjb;
    @EJB
    private TeacherinfoFacadeLocal teacherEjb;
    @EJB
    private StudentFacadeLocal studentEjb;
    private @Inject
    CityBean cityBean;

    private Checkrecords checkrecords;
    private List<Checkrecords> checkList;

    private String studentNo;

    private Integer cityid;
    private Integer enterid;
    private final Calendar c = Calendar.getInstance();
    private int year = c.get(Calendar.YEAR), month = c.get(Calendar.MONTH), day = c.get(Calendar.DAY_OF_MONTH);
    private final int currentMonth = month;
    private LinkedHashMap<String, Integer> rankMap;
    private final LinkedHashMap<Integer, Integer> dayMap = new LinkedHashMap<>();
    private List<Practicenotes> practiceList = new LinkedList<>();

    private boolean readflag;
    private List<Stuentrel> stuInSameRel = new LinkedList<>();
    private String deleteRepDate, checkDate;
    private Part parts;
    private String staResult;
    private HttpSession mysession;
    private Teacherinfo teacher;
    private Student student;

    @PostConstruct
    public void init() {
        mysession = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        student = (Student) mysession.getAttribute(StaticFields.sessionStudent);
    }

    public String addCheRecord(Checkrecords c) {
        c.setState(1);
        checkEjb.edit(c);
        if (checkList != null) {
            checkList.clear();
        }
        return null;
    }

    public String addCheckRecord() {
        if (null != this.studentNo) {
            Calendar tempc = Calendar.getInstance();
            tempc.add(Calendar.YEAR, year - c.get(Calendar.YEAR));
            tempc.add(Calendar.MONTH, month - c.get(Calendar.MONTH));
            tempc.add(Calendar.DAY_OF_MONTH, day - c.get(Calendar.DAY_OF_MONTH));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String s = sdf.format(tempc.getTime());
            String sql = "select * from checkRecords where stuno='" + this.checkrecords.getStudent().getUno() + "' and checkdate='" + s + "' order by checkdate";
            List<Checkrecords> checkListTme = checkEjb.getList(sql);
            if (checkListTme.size() > 0) {//该生检查记录已经存在了
                this.checkrecords = checkListTme.get(0);
                FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage(this.checkrecords.getStudent().getName() + "的检查记录已经存在，不能再添加了"));
            } else {
                Collection<Part> temp = null;
                try {
                    checkrecords.setTeacherinfo(teacher);
                    checkrecords.setCheckdate(tempc.getTime());
                    checkrecords.setState(1);
                    //需要检查学生信息是否准备好      
                    checkEjb.create(checkrecords);
                    //          FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("添加成功，您可以继续添加"));
                    Checkrecords cr = checkEjb.getList("select * from checkrecords where stuno='" + this.checkrecords.getStudent().getUno() + "' and teachno='" + this.teacher.getUno() + "' and checkdate='" + s + "'").get(0);
                    InputStream is = null;
                    OutputStream outputStream = null;

                    temp = ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getParts();
                    File filePath = new File(StaticFields.saveImgTeachPath);
                    if (!filePath.exists()) {
                        filePath.mkdir();
                    }

                    for (Part c : temp) {
                        if ((c.getSubmittedFileName() != null) && (!c.getSubmittedFileName().isEmpty())) {
                            is = c.getInputStream();
                            File schoolFilePath = new File(StaticFields.saveImgTeachPath);
                            if (!schoolFilePath.exists()) {
                                schoolFilePath.mkdir();
                            }
                            File curFilePath = new File(StaticFields.saveImgTeachPath + "/" + cr.getId());
                            if (!curFilePath.exists()) {
                                curFilePath.mkdir();
                            }
                            String name = (String) c.getSubmittedFileName().subSequence(c.getSubmittedFileName().lastIndexOf("\\") + 1, c.getSubmittedFileName().length());
                            outputStream = new FileOutputStream(new File(StaticFields.saveImgTeachPath + "/" + cr.getId() + "/" + System.currentTimeMillis() + name.substring(name.lastIndexOf('.'))));
                            int myread = 0;
                            byte[] bytes = new byte[1024];
                            while ((myread = is.read(bytes)) != -1) {
                                outputStream.write(bytes, 0, myread);
                            }
                            outputStream.flush();
                            outputStream.close();
                            is.close();
                        }
                    }
//                    }
                } catch (IOException | ServletException ex) {
                    Logger.getLogger(CheckRecordBean4Student.class.getName()).log(Level.SEVERE, null, ex);
                }
                this.checkrecords = new Checkrecords();
                if (checkList != null) {
                    checkList.clear();
                }
                this.setYear(c.get(Calendar.YEAR));
                this.setMonth(c.get(Calendar.MONTH));
                this.setDay(c.get(Calendar.DAY_OF_MONTH));
            }
        } else {
            FacesContext.getCurrentInstance().addMessage("OK", new FacesMessage("请选择城市与学生！"));
        }
        return null;
    }

    public String deleteImg(String name) {
        File file = new File(StaticFields.saveImgTeachPath + "/" + this.checkrecords.getId() + "/" + name);
        if (file.isFile() && file.exists()) {
            file.delete();
        }
        return null;
    }
//通过学号查看学生的记录

    public String searchByno() {
        this.practiceList = this.student.getPracticenotesList();
        if (practiceList.size() > 0) {
            City district = this.practiceList.get(0).getStuentrel().getCity();
            cityBean.setCity(district.getCity());
            cityBean.setProvince(cityBean.getCity().getCity());
//                userinfobean.setCityId(district.getId());
        } else {
            FacesContext.getCurrentInstance().addMessage("OK", new FacesMessage("该生还未选择实习单位和提交实习周记！"));
        }
        return null;
    }

    public String backToCheck() {
        return "teacherCheck.xhtml";
    }

    public List<Checkrecords> getCheckList() {
        if (checkList == null) {
            checkList = new ArrayList<>();
        }
        checkList.clear();
        List<Stuentrel> listStuEnt = stuentrelEjb.getList("select * from stuentrel where  stuno ='" + student.getUno() + "' order by stuno");
        if (listStuEnt.size() > 0) {
            checkList = (ArrayList) checkEjb.getList("select * from CHECKRECORDS where stuno='" + student.getUno() + "' and state=1 order by checkdate");
        }
        return checkList;
    }

    public String seeDetail(Checkrecords checkPara) {
        this.checkrecords = checkPara;
        return "stuSeeCheckRecord.xhtml";
    }

    public LinkedHashMap<String, Integer> getRankMap() {
        if (null == rankMap) {
            rankMap = new LinkedHashMap<>();
            rankMap.put(StaticFields.rankString[0], 0);
            rankMap.put(StaticFields.rankString[1], 1);
            rankMap.put(StaticFields.rankString[2], 2);
            rankMap.put(StaticFields.rankString[3], 3);
        }
        return rankMap;
    }

    public LinkedHashMap<Integer, Integer> getDayMap() {
        dayMap.clear();
        Calendar c1 = Calendar.getInstance();
        c1.add(Calendar.YEAR, year - c.get(Calendar.YEAR));
        c1.add(Calendar.MONTH, month - currentMonth);
        for (int i = 0; i < c1.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
            dayMap.put(i + 1, i + 1);
        }
        return dayMap;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String directToNoteshowCheckRecord(Checkrecords checkrecordsPara) {
        setReadflag(true);
        this.checkrecords = checkrecordsPara;
        return "showCheckRecord.xhtml";
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public Checkrecords getCheckrecords() {
        if (checkrecords == null) {
            checkrecords = new Checkrecords();
        }
        return checkrecords;
    }

    public void setCheckrecords(Checkrecords checkrecords) {
        this.checkrecords = checkrecords;
    }
    private String oldStuno = "";

    public List<Practicenotes> getPracticeList() {
        if (null != this.getStudentNo()) {
            if (!oldStuno.equals(this.getStudentNo())) {
                this.oldStuno = this.getStudentNo();
//            String sql1 = "select * from student" + StaticFields.currentGradeNum + myUser.getSchoolId() + " where uno='" + this.checkrecords.getStuno()+ "'";
                String sql2 = "select * from practicenote where stuno='" + this.getStudentNo() + "' order by submitdate";
                this.practiceList = practiceNoteEjb.getList(sql2);
            }
        }
        return practiceList;
    }

    public String deleteSelectRecord(int checkrecordId) {
        checkEjb.executUpdate("delete from checkrecords where id=" + checkrecordId);
        this.checkrecords = null;
        setReadflag(true);
        checkList.clear();
        return "teacherCheck.xhtml";
    }

    public String editSelectRecord() {
        setReadflag(false);
        return "showCheckRecord.xhtml";
    }

    public String alterSelectRecord() {
        if (!this.readflag) {
            checkEjb.executUpdate("update checkrecords set checkcontent='" + checkrecords.getCheckcontent() + "', recommendation='" + checkrecords.getRecommendation() + "', rank='" + checkrecords.getRank() + "', remark='" + checkrecords.getRemark() + "' where id=" + checkrecords.getId());
            setReadflag(true);
        }
        return null;
    }

    public String returnTeacherCheck() {
        checkList.clear();
        return "teacherCheck.xhtml";
    }

    public Integer getCityid() {
        return cityid;
    }

    public void setCityid(Integer cityid) {
        this.cityid = cityid;
    }

    public Integer getEnterid() {
        return enterid;
    }

    public void setEnterid(Integer enterid) {
        this.enterid = enterid;
    }

    public void setCheckList(ArrayList<Checkrecords> checkList) {
        this.checkList = checkList;
    }

    public List<Stuentrel> getStuInSameRel() {
        stuInSameRel = stuentrelEjb.getList("select * from stuentrel where entstuid in (select id from enterstudent  where enterid in(select id from enterprise where cityid=" + cityid + ")) order by stuno");
        return stuInSameRel;
    }

    public String getStudentNo() {
        return studentNo;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }

    public String getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(String checkDate) {
        this.checkDate = checkDate;
    }

    public boolean isReadflag() {
        return readflag;
    }

    public void setReadflag(boolean readflag) {
        this.readflag = readflag;
    }

    public Part getParts() {
        return parts;
    }

    public void setParts(Part parts) {
        this.parts = parts;
    }

    public String[] getFiles(Checkrecords c) {
        File myFile = new File(StaticFields.saveImgTeachPath  + "/" + c.getId() + "/");
        return myFile.list();
    }

  
    /**
     * @return the staResult
     */
    public String getStaResult() {
        return staResult;
    }
}
