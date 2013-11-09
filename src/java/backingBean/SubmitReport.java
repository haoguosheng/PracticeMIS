/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.Practicenote;
import entities.Stuentrel;
import entities.User;
import entitiesBeans.PracticenoteLocal;
import entitiesBeans.StuentrelLocal;
import entitiesBeans.UserLocal;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import tools.SQLTool;
import tools.StaticFields;

/**
 *
 * @author myPC
 */
@Named
@RequestScoped
public class SubmitReport implements Serializable {

    private @Inject User user;
    private final PracticenoteLocal practDao = new PracticenoteLocal();
    private final StuentrelLocal seDao = new StuentrelLocal();
    private final UserLocal userDao = new UserLocal();
    private List<Practicenote> submittedNoteList;
    private Practicenote practiceNote = new Practicenote();
//    private boolean inputed; //检查本击是否已经提交
    private User studentUser;
    private String studentUserno;
    private Calendar c = Calendar.getInstance();
    private int year = c.get(Calendar.YEAR), month = c.get(Calendar.MONTH), day = c.get(Calendar.DAY_OF_MONTH), currentMonth = month;
    private LinkedHashMap<Integer, Integer> dayMap = new LinkedHashMap<>();
    private Stuentrel stuEnRel;
    private String deleteRepDate, alterDate;
    private boolean readflag;

    @PostConstruct
    public void init() {
        stuEnRel = new Stuentrel();
    }

    public void submitPracNote() {
        Calendar tempc = Calendar.getInstance();
        tempc.add(Calendar.YEAR, year - c.get(Calendar.YEAR));
        tempc.add(Calendar.MONTH, month - c.get(Calendar.MONTH));
        tempc.add(Calendar.DAY_OF_MONTH, day - c.get(Calendar.DAY_OF_MONTH));
        int week = tempc.get(Calendar.WEEK_OF_YEAR);
        boolean hasSubmitted = false;
        if (tempc.after(Calendar.getInstance())) {
            FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("实习日期不能超过今天"));
        } else {
            if (this.getSubmittedNoteList() != null) {
                Iterator<Practicenote> it = this.getSubmittedNoteList().iterator();
                while (it.hasNext()) {
                    Date submitDate = it.next().getSubmitdate();
                    Calendar myTem = (Calendar) tempc.clone();
                    myTem.setTime(submitDate);
                    if (week == myTem.get(Calendar.WEEK_OF_YEAR)) {
                        hasSubmitted = true;
                        break;
                    }
                }
            }
            if (hasSubmitted) {
                FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("请确认您输入了正确的日期，因为当前日期的周记已经提交过了"));
            } else {
                practiceNote.setSubmitdate(tempc.getTime());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String s = sdf.format(tempc.getTime());
                //注意：java.sql.PRACTICENOTE001Date只能读取日期("yyyy-MM-dd")
                //java.sql.Date date = Date.Valuseof(s);
                practiceNote.setStudententid(Integer.parseInt(new SQLTool<Stuentrel>().getIdListHandlerRunner("select id from stuentrel" + StaticFields.currentGradeNum + getUser().getSchoolId() + " where stuno='" + getUser().getUno() + "'").get(0)));
                practDao.executUpdate("insert into practicenote" + StaticFields.currentGradeNum + getUser().getSchoolId() + "(detail, submitdate,  studententid) values('" + practiceNote.getDetail() + "', '" + s + "', " + practiceNote.getStudententid() + ")");
                submittedNoteList = new LinkedList<>();//重新生成该列表
                practiceNote = new Practicenote();
                this.setYear(c.get(Calendar.YEAR));
                this.setMonth(c.get(Calendar.MONTH));
                this.setDay(c.get(Calendar.DAY_OF_MONTH));
                // FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("提交周记成功"));
            }
        }
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

    public List<Practicenote> getSubmittedNoteList() {
        if (null == submittedNoteList || submittedNoteList.isEmpty()) {
            int id = seDao.getList("select * from stuentrel" + StaticFields.currentGradeNum + getUser().getSchoolId() + " where stuno='" + getUser().getUno() + "'").get(0).getId();
            submittedNoteList = practDao.getList("select * from practicenote" + StaticFields.currentGradeNum + getUser().getSchoolId() + " where studententid=" + id);
        }
        return submittedNoteList;
    }
    private Practicenote temPraNote;//存放临时的周记报告，用于只读显示

    public String directToNote() {
        readflag = true;
        FacesContext context = FacesContext.getCurrentInstance();
        if (null != (context.getExternalContext().getRequestParameterMap().get("studentNo"))) {
            //主要用于教师查看学生的周记
            this.studentUser = userDao.getList("select * from student" + StaticFields.currentGradeNum + getUser().getSchoolId() + " where uno='" + context.getExternalContext().getRequestParameterMap().get("studentNo") + "'").get(0);
            this.submittedNoteList = practDao.getList("select * from practice" + StaticFields.currentGradeNum + getUser().getSchoolId() + " where stuno='" + this.studentUser.getUno() + "'");
        }
        String strToFormat = context.getExternalContext().getRequestParameterMap().get("submitDate");
        deleteRepDate = strToFormat;
        alterDate = strToFormat;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date requestDate = null;
        try {
            requestDate = sdf.parse(strToFormat);
        } catch (ParseException ex) {
            Logger.getLogger(SubmitReport.class.getName()).log(Level.SEVERE, null, ex);
        }
        Iterator<Practicenote> it = submittedNoteList.iterator();
        while (it.hasNext()) {
            Practicenote tem = it.next();
            if (requestDate.getDate() == tem.getSubmitdate().getDate()) {
                this.temPraNote = tem;
                break;
            }
        }
        return "showStudentReport.xhtml";
    }

    public String deleteSelectReport() {
        String s = deleteRepDate;
        practDao.executUpdate("delete from practicenote" + StaticFields.currentGradeNum + getUser().getSchoolId() + " where stuno='" + getUser().getUno() + "' and submitdate='" + s + "'");
        submittedNoteList = practDao.getList("select * from practicenote" + StaticFields.currentGradeNum + getUser().getSchoolId() + " where stuno='" + getUser().getUno() + "'");
        readflag = true;
        return "viewReports.xhtml";
    }

    public String editSelectReport() {
        readflag = false;
        return "showStudentReport.xhtml";
    }

    public String alterSelectReport() {
        String s = alterDate;
        practDao.executUpdate("update practicenote" + StaticFields.currentGradeNum + getUser().getSchoolId() + " set detail='" + temPraNote.getDetail() + "' where stuno='" + getUser().getUno() + "' and submitdate='" + s + "'");
        submittedNoteList = practDao.getList("select * from practicenote" + StaticFields.currentGradeNum + getUser().getSchoolId() + " where stuno='" + getUser().getUno() + "'");
        return "viewReports.xhtml";
    }

    public Practicenote getTemPraNote() {
        return temPraNote;
    }

    public Practicenote getPracticeNote() {
        return practiceNote;
    }

    public void setPracticeNote(Practicenote practiceNote) {
        this.practiceNote = practiceNote;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public User getStudentUser() {
        studentUser = userDao.getList("select * from student" + StaticFields.currentGradeNum + getUser().getSchoolId() + " where uno='" + studentUserno + "'").get(0);
        return studentUser;
    }

    public String getStudentUserno() {
        return studentUserno;
    }

    public void setStudentUserno(String studentUserno) {
        this.studentUserno = studentUserno;
    }

    public boolean isReadflag() {
        return readflag;
    }

    public void setReadflag(boolean readflag) {
        this.readflag = readflag;
    }

    public Stuentrel getStuEnRel() {
        stuEnRel = seDao.getList("select * from stuentrel where stuno='" + getUser().getUno()).get(0);
        return stuEnRel;
    }

    public void setStuEnRel(Stuentrel stuEnRel) {
        this.stuEnRel = stuEnRel;
    }

    public User getUser() {
//        if (null == user) {
//            FacesContext context = FacesContext.getCurrentInstance();
//            HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
//            user = (User) session.getAttribute("myUser");
//        }
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
