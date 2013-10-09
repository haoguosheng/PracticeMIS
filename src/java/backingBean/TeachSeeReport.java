/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package backingBean;

import entities.Practicenote;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import tools.SQLTool;
import tools.StaticFields;
import tools.UserAnalysis;

/**
 *
 * @author myPC
 */
@Named
@SessionScoped
public class TeachSeeReport implements Serializable {

    private SQLTool<Practicenote> practDao ;
    private Practicenote practiceNote ;

    @PostConstruct
    public void init(){
        practDao = new SQLTool<Practicenote>();
        practiceNote = new Practicenote();
    }
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
