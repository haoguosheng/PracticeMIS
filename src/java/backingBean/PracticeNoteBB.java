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
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import sessionBeans.PracticenotesFacadeLocal;
import tools.StaticFields;

/**
 *
 * @author myPC
 */
@Named
@SessionScoped
public class PracticeNoteBB implements Serializable {
    @EJB
    private PracticenotesFacadeLocal practEjb;
    Practicenotes practiceNote;
    private Student studentUser;
    private int positionId;
    private List<Practicenotes> submittedNoteList;
    private final Calendar c = Calendar.getInstance();
    private int year = c.get(Calendar.YEAR), month = c.get(Calendar.MONTH), day = c.get(Calendar.DAY_OF_MONTH);
    private final int currentMonth = month;
    private final LinkedHashMap<Integer, Integer> dayMap = new LinkedHashMap<>();
    private List<Stuentrel> stuEnRelList;
    private boolean readflag;
    private Practicenotes temPraNote;//存放临时的周记报告，用于只读显示
    private Part parts;
    private MyUser user;
    HttpSession mySession = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);

    @PostConstruct
    public void init() {
        mySession = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
         int length = (int) (mySession.getAttribute("userNoLength"));
        if (length == StaticFields.teacherUnoLength) {
            user = (Teacherinfo) mySession.getAttribute("teacherUser");

        } else if (length == StaticFields.stuUnoLength1 || length == StaticFields.stuUnoLength2) {
            user = (Student) mySession.getAttribute("studentUser");
            studentUser=(Student)getUser();
        }
    }

    public String addPractice(Practicenotes p) {
        p.setState(1);
        practEjb.edit(p);
        submittedNoteList = null;//重新生成该列表
        return null;
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
            if (hasSubmitted) {
                FacesContext.getCurrentInstance().addMessage("ok", new FacesMessage("请确认您输入了正确的日期，因为当前日期的周记已经提交过了"));
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String s = sdf.format(tempc.getTime());
                //注意：java.sql.Date只能读取日期("yyyy-MM-dd")
                //java.sql.Date date = Date.Valuseof(s);
                List<Stuentrel> temList = this.studentUser.getStuentrelList();
                this.practiceNote.setStudent((Student)getUser());
                this.practiceNote.setStuentrel(temList.get(temList.size() - 1));
                this.practiceNote.setState(1);
                practEjb.create(this.practiceNote);
                Practicenotes pn = practEjb.getList("select * from practicenote  where stuno='" + getUser().getUno() + "' and submitdate='" + s + "'").get(0);
                InputStream is = null;
                OutputStream outputStream = null;
                Collection<Part> temp = null;
                try {
                    temp = ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getParts();
                    File filePath = new File(StaticFields.saveImgStuPath);
                    if (!filePath.exists()) {
                        filePath.mkdir();
                    }
                    for (Part c : temp) {
                        if ((c.getSubmittedFileName() != null) && (!c.getSubmittedFileName().isEmpty())) {
                            is = c.getInputStream();
                            File schoolFilePath = new File(StaticFields.saveImgStuPath + "/");
                            if (!schoolFilePath.exists()) {
                                schoolFilePath.mkdir();
                            }
                            File curFilePath = new File(StaticFields.saveImgStuPath + "/"  + pn.getId());
                            if (!curFilePath.exists()) {
                                curFilePath.mkdir();
                            }
                            String name = (String) c.getSubmittedFileName().subSequence(c.getSubmittedFileName().lastIndexOf("\\") + 1, c.getSubmittedFileName().length());
                            outputStream = new FileOutputStream(new File(StaticFields.saveImgStuPath + "/" + pn.getId() + "/" + System.currentTimeMillis() + name.substring(name.lastIndexOf('.'))));
                            int myread = 0;
                            byte[] bytes = new byte[1024];
                            while ((myread = is.read(bytes)) != -1) {
                                outputStream.write(bytes, 0, myread);
                            }
                            outputStream.flush();
                            outputStream.close();
                            is.close();
                        }
//                        }
                    }
                } catch (IOException | ServletException ex) {
                    Logger.getLogger(CheckRecordBean4Teacher.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (submittedNoteList != null) {
                    submittedNoteList.clear();
                }
            }
        }
    }

    public String[] getFiles(Practicenotes p) {
        File myFile = new File(StaticFields.saveImgStuPath + "/"  + p.getId() + "/");
        return myFile.list();
    }

    public String deleteImg(String name) {
        File file = new File(StaticFields.saveImgStuPath + "/"  + this.temPraNote.getId() + "/" + name);
        if (file.isFile() && file.exists()) {
            file.delete();
        }
        return null;
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
    /*
     **功能：获得已经提交的实习周记
     */

    public List<Practicenotes> getSubmittedNoteList() {
        if (null == submittedNoteList || submittedNoteList.isEmpty()) {
            submittedNoteList = new ArrayList<>();
            List<Stuentrel> stuEntRelList = this.studentUser.getStuentrelList();
            if (stuEntRelList.size() > 0) {
                String tem = "";
                for (Stuentrel s : stuEntRelList) {
                    tem += s.getId() + ",";
                }
                try {
                    submittedNoteList = practEjb.getList("select * from practicenote where studentEntId in(" + tem.substring(0, tem.length() - 1) + ") and stuno='" + getUser().getUno() + "' order by submitdate");
                } catch (Exception e) {
                    submittedNoteList = new ArrayList<>();
                }
            } else {//stuEntRelList.size()==0
                try {
                    FacesContext context = FacesContext.getCurrentInstance();
                    HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
                    response.sendRedirect("selectMyEnterprise.xhtml");
                } catch (IOException ex) {
                    Logger.getLogger(UserinfoBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return submittedNoteList;
    }

    public String directToNote(Practicenotes practicenotePara) {
        readflag = true;
        temPraNote = practicenotePara;
        temPraNote.setStudent(this.studentUser);
        return "showStudentReport.xhtml";
    }

    public String deleteSelectReport(Practicenotes tempNote) {
        practEjb.remove(tempNote);
        submittedNoteList.clear();
        return "submitReports.xhtml";
    }

    public String editSelectReport() {
        readflag = false;
        return "showStudentReport.xhtml";
    }

    public boolean outDate() {
        boolean result;
        Calendar temC = Calendar.getInstance();
        temC.clear();
        temC.set(temPraNote.getSubmitdate().getYear() + 1900, temPraNote.getSubmitdate().getMonth(), temPraNote.getSubmitdate().getDay());
        temC.add(Calendar.DATE, StaticFields.dayAfterDisabled);
        if (temC.after(c)) {//超过指定的天数就不允许再修改了
            result = true;
        } else {
            result = false;
        }
        return result;
    }

    public String alterSelectReport() {
        practEjb.edit(temPraNote);
        //       practDao.executUpdate("update practicenote" + StaticFields.currentGradeNum + this.getUser().getSchoolId() + " set detail='" + temPraNote.getDetail() + "' where STUDENTENTID=" + temPraNote.getStudententid() + " and submitdate='" + s + "'");
        //       submittedNoteList = practDao.getList("select * from practicenote" + StaticFields.currentGradeNum + this.getUser().getSchoolId() + " where stuno='" + this.getUser().getUno() + "'");
        submittedNoteList.clear();
        readflag = true;
        return "showStudentReport.xhtml";
    }

    public String returnSubmitReport() {
        submittedNoteList.clear();
        return "submitReports.xhtml";
    }

    /*
     *获得学生选择的企业；
     *如果学生还没有选择企业，则转向选择企业页面
     */
    public List<Stuentrel> getStuEnRelList() {
        if (null == stuEnRelList) {
            List<Stuentrel> stuEntList = this.studentUser.getStuentrelList();
            if (stuEntList.isEmpty()) {
                FacesContext context = FacesContext.getCurrentInstance();
                HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
                try {
                    response.sendRedirect("selectMyEnterprise.xhtml");
                } catch (IOException ex) {
                    Logger.getLogger(PracticeNoteBB.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return stuEnRelList;
    }
    /*
     *处理来自teacherCheck.xhmtl的请求，并跳转
     */

    public String teachSeePracticeNote(Practicenotes practice) {
        this.practiceNote = practice;
        return "teachSeeReport.xhtml";
    }

    public Practicenotes getTemPraNote() {
        if (temPraNote == null) {
            temPraNote = new Practicenotes();
        }
        return temPraNote;
    }

    public void setTemPraNote(Practicenotes temPraNote) {
        this.temPraNote = temPraNote;
    }

    public void setPositionId(int positionId) {
        this.positionId = positionId;
    }

    public int getPositionId() {
        return this.positionId;
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

    public boolean isReadflag() {
        return readflag;
    }

    public void setReadflag(boolean readflag) {
        this.readflag = readflag;
    }

    public void setStuEnRelList(List<Stuentrel> stuEnRelList) {
        this.stuEnRelList = stuEnRelList;
    }

    public Practicenotes getPracticeNote() {
        return practiceNote;
    }

    public void setPracticeNote(Practicenotes practiceNote) {
        this.practiceNote = practiceNote;
    }

    public Part getParts() {
        return parts;
    }

    public void setParts(Part parts) {
        this.parts = parts;
    }

    public MyUser getUser() {
        return user;
    }
}
