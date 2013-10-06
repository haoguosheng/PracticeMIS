/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.Practicenote;
import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import tools.SQLTool;
import tools.StaticFields;
import tools.UserAnalysis;

/**
 *
 * @author myPC
 */
@ManagedBean
@SessionScoped
public class TeachSeeReport implements Serializable {

    private SQLTool<Practicenote> practDao = new SQLTool<Practicenote>();
    private Practicenote practiceNote = new Practicenote();

    public String directToNote() {
        FacesContext context = FacesContext.getCurrentInstance();
        String date = context.getExternalContext().getRequestParameterMap().get("submitDate");
        String stuno = context.getExternalContext().getRequestParameterMap().get("studentNo");
        String schoolId = UserAnalysis.getSchoolId(stuno);
        practiceNote = practDao.getBeanListHandlerRunner("select * from practicenote" +StaticFields.currentGradeNum+ schoolId + " where stuno='" + stuno + "' and submitdate='" + date + "'", practiceNote).get(0);
        practiceNote.setSchoolId(schoolId);
        return "teachSeeReport.xhtml";
    }

    /**
     * @return the practiceNote
     */
    public Practicenote getPracticeNote() {
        return practiceNote;
    }

    /**
     * @param practiceNote the practiceNote to set
     */
    public void setPracticeNote(Practicenote practiceNote) {
        this.practiceNote = practiceNote;
    }
}
