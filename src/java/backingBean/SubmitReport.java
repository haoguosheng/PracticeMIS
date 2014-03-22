/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.MyUser;
import entities.Practicenotes;
import entities.Student;
import entities.Stuentrel;
import entities.Teacherinfo;
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
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import sessionBeans.PracticenotesFacadeLocal;
import sessionBeans.StudentFacadeLocal;
import sessionBeans.StuentrelFacadeLocal;
import tools.StaticFields;

/**
 *
 * @author myPC
 */
@Named
@RequestScoped
public class SubmitReport implements Serializable {

    @EJB
    private PracticenotesFacadeLocal practEjb;
    @EJB
    private StuentrelFacadeLocal stuEntRelEjb;
    @EJB
    private StudentFacadeLocal studentEjb;
    private List<Practicenotes> submittedNoteList;
    private Practicenotes practiceNote = new Practicenotes();
    private Student studentUser;
    private String studentUserno;
    private Calendar c = Calendar.getInstance();
    private int year = c.get(Calendar.YEAR), month = c.get(Calendar.MONTH), day = c.get(Calendar.DAY_OF_MONTH), currentMonth = month;
    private LinkedHashMap<Integer, Integer> dayMap = new LinkedHashMap<>();
    private Stuentrel stuEnRel;
    private String deleteRepDate, alterDate;
    private boolean readflag;
    MyUser user;
    HttpSession mySession = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);

    @PostConstruct
    public void init() {
        mySession = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
         int length = (int) (mySession.getAttribute("userNoLength"));
        if (length == StaticFields.teacherUnoLength) {
            user = (Teacherinfo) mySession.getAttribute("teacherUser");

        } else if (length == StaticFields.stuUnoLength1 || length == StaticFields.stuUnoLength2) {
            user = (Student) mySession.getAttribute("studentUser");
        }
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
                Iterator<Practicenotes> it = this.getSubmittedNoteList().iterator();
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
                practiceNote.setStuentrel(stuEntRelEjb.getList("select * from stuentrel where stuno='" +user.getUno() + "' order by stuno").get(0));
                practiceNote.setState(1);
                practEjb.create(practiceNote);
                submittedNoteList = new LinkedList<>(); //重新生成该列表
                practiceNote = new Practicenotes();
                this.setYear(c.get(Calendar.YEAR));
                this.setMonth(c.get(Calendar.MONTH));
                this.setDay(c.get(Calendar.DAY_OF_MONTH));
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

    public List<Practicenotes> getSubmittedNoteList() {
        if (null == submittedNoteList || submittedNoteList.isEmpty()) {
            int id = stuEntRelEjb.getList("select * from stuentrel where stuno='" + user.getUno() + "' order by stuno").get(0).getId();
            submittedNoteList = practEjb.getList("select * from practicenote where studententid=" + id + " order by submitdate");
        }
        return submittedNoteList;
    }
    private Practicenotes temPraNote;//存放临时的周记报告，用于只读显示

    public String directToNote() {
        readflag = true;
        FacesContext context = FacesContext.getCurrentInstance();
        if (null != (context.getExternalContext().getRequestParameterMap().get("studentNo"))) {
            //主要用于教师查看学生的周记
            this.studentUser = studentEjb.getList("select * from student where uno='" + context.getExternalContext().getRequestParameterMap().get("studentNo") + "' order by name").get(0);
            this.submittedNoteList = practEjb.getList("select * from practice where stuno='" + this.studentUser.getUno() + "' order by submitdate");
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
        Iterator<Practicenotes> it = submittedNoteList.iterator();
        while (it.hasNext()) {
            Practicenotes tem = it.next();
            if (requestDate.getDate() == tem.getSubmitdate().getDate()) {
                this.temPraNote = tem;
                break;
            }
        }
        return "showStudentReport.xhtml";
    }

    public String deleteSelectReport() {
        String s = deleteRepDate;
        practEjb.executUpdate("delete from practicenote where stuno='" + user.getUno() + "' and submitdate='" + s + "'");
        submittedNoteList = practEjb.getList("select * from practicenote where stuno='" + user.getUno() + "' order by submitdate");
        readflag = true;
        return "viewReports.xhtml";
    }

    public String editSelectReport() {
        readflag = false;
        return "showStudentReport.xhtml";
    }

    public String alterSelectReport() {
        String s = alterDate;
        practEjb.executUpdate("update practicenote set detail='" + temPraNote.getDetail() + "' where stuno='" + user.getUno() + "' and submitdate='" + s + "'");
        submittedNoteList = practEjb.getList("select * from practicenote where stuno='" + user.getUno() + "' order by submitdate");
        return "viewReports.xhtml";
    }

    public Practicenotes getTemPraNote() {
        return temPraNote;
    }

    public Practicenotes getPracticeNote() {
        return practiceNote;
    }

    public void setPracticeNote(Practicenotes practiceNote) {
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

    public Student getStudentUser() {
        studentUser = studentEjb.getList("select * from student where uno='" + studentUserno + "' order by name").get(0);
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
        stuEnRel = stuEntRelEjb.getList("select * from stuentrel where stuno='" + user.getUno() + "' order by id").get(0);
        return stuEnRel;
    }

    public void setStuEnRel(Stuentrel stuEnRel) {
        this.stuEnRel = stuEnRel;
    }

}
