/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package converter;

import entities.Checkrecords;
import entities.City;
import entities.Entercityrequire;
import entities.Enterprise;
import entities.Enterstudent;
import entities.Nameofunit;
import entities.News;
import entities.Position;
import entities.Practicenotes;
import entities.Resourceinfo;
import entities.Roleinfo;
import entities.Student;
import entities.Stuentrel;
import entities.Teacherinfo;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import sessionBeans.CheckrecordsFacadeLocal;
import sessionBeans.CityFacadeLocal;
import sessionBeans.EntercityrequireFacadeLocal;
import sessionBeans.EnterpriseFacadeLocal;
import sessionBeans.EnterstudentFacadeLocal;
import sessionBeans.NameofunitFacadeLocal;
import sessionBeans.NewsFacadeLocal;
import sessionBeans.PositionFacadeLocal;
import sessionBeans.PracticenotesFacadeLocal;
import sessionBeans.ResourceinfoFacadeLocal;
import sessionBeans.RoleinfoFacadeLocal;
import sessionBeans.StudentFacadeLocal;
import sessionBeans.StuentrelFacadeLocal;
import sessionBeans.TeacherinfoFacadeLocal;

/**
 *
 * @author hgs
 */
public class ConvertersInAll {

    @FacesConverter(forClass = Checkrecords.class,value="checkrecordsConverter")
    public static class CheckrecordsConverter implements Converter {

        @EJB
        CheckrecordsFacadeLocal myejb;

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            return myejb.find(Integer.valueOf(value));
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null || object.equals("0")) {
                return null;
            }
            if (object instanceof Checkrecords) {
                Checkrecords o = (Checkrecords) object;
                return String.valueOf(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Checkrecords.class.getName());
            }
        }

    }

    @FacesConverter(forClass = City.class,value = "cityConverter")
    public static class CityConverter implements Converter {

        @EJB
        CityFacadeLocal myejb;

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0||value.equals("0")) {
                return null;
            }
            return myejb.find(Integer.valueOf(value));
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null || object.equals("0")) {
                return null;
            }
            if (object instanceof City) {
                City o = (City) object;
                return String.valueOf(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + " and the value is"+object+"; expected type: " + City.class.getName());
            }
        }

    }

    @FacesConverter(forClass = Entercityrequire.class,value="entercityrequireConverter")
    public static class EntercityrequireConverter implements Converter {

        @EJB
        EntercityrequireFacadeLocal myEjb;

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0||value.equals("0")) {
                return null;
            }
            return myEjb.find(Integer.parseInt(value));
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null || object.equals("0")) {
                return null;
            }
            if (object instanceof Entercityrequire) {
                Entercityrequire o = (Entercityrequire) object;
                return String.valueOf(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Entercityrequire.class.getName());
            }
        }

    }

    @FacesConverter(forClass = Enterprise.class,value="enterpriseConverter")
    public static class EnterpriseConverter implements Converter {

        @EJB
        EnterpriseFacadeLocal myEjb;

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0||value.equals("0")) {
                return null;
            }
            return myEjb.find(Integer.valueOf(value));
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null || object.equals("0")) {
                return null;
            }
            if (object instanceof Enterprise) {
                Enterprise o = (Enterprise) object;
                return String.valueOf(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Enterprise.class.getName());
            }
        }

    }

    @FacesConverter(forClass = Enterstudent.class,value="enterstudentConverter")
    public static class EnterstudentConverter implements Converter {

        @EJB
        EnterstudentFacadeLocal myEjb;

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0||value.equals("0")) {
                return null;
            }
            return myEjb.find(Integer.valueOf(value));
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null || object.equals("0")) {
                return null;
            }
            if (object instanceof Enterstudent) {
                Enterstudent o = (Enterstudent) object;
                return String.valueOf(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Enterstudent.class.getName());
            }
        }

    }

    @FacesConverter(forClass = Nameofunit.class,value="nameofunitConverter")
    public static class NameofunitConverter implements Converter {

        @EJB
        NameofunitFacadeLocal myEjb;

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0||value.equals("0")) {
                return null;
            }
            return myEjb.find(Integer.valueOf(value));
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null || object.equals("0")) {
                return null;
            }
            if (object instanceof Nameofunit) {
                Nameofunit o = (Nameofunit) object;
                return String.valueOf(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Nameofunit.class.getName());
            }
        }
    }

    @FacesConverter(forClass = News.class,value="newsConverter")
    public static class NewsConverter implements Converter {

        @EJB
        NewsFacadeLocal myEjb;

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0||value.equals("0")) {
                return null;
            }
            return myEjb.find(Integer.valueOf(value));
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null || object.equals("0")) {
                return null;
            }
            if (object instanceof News) {
                News o = (News) object;
                return String.valueOf(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + News.class.getName());
            }
        }

    }

    @FacesConverter(forClass = Position.class,value="positionConverter")
    public static class PositionConverter implements Converter {

        @EJB
        PositionFacadeLocal myEjb;

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0||value.equals("0")) {
                return null;
            }
            return myEjb.find(Integer.valueOf(value));
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null || object.equals("0")) {
                return null;
            }
            if (object instanceof Position) {
                Position o = (Position) object;
                return String.valueOf(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Position.class.getName());
            }
        }
    }

    @FacesConverter(forClass = Practicenotes.class,value="practicenotesConverter")
    public static class PracticenotesConverter implements Converter {

        @EJB
        PracticenotesFacadeLocal myEjb;

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0||value.equals("0")) {
                return null;
            }
            return myEjb.find(Integer.valueOf(value));
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null || object.equals("0")) {
                return null;
            }
            if (object instanceof Practicenotes) {
                Practicenotes o = (Practicenotes) object;
                return String.valueOf(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Practicenotes.class.getName());
            }
        }
    }

    @FacesConverter(forClass = Resourceinfo.class,value="resourceinfoConverter")
    public static class ResourceinfoConverter implements Converter {

        @EJB
        ResourceinfoFacadeLocal myEjb;

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0||value.equals("0")) {
                return null;
            }
            return myEjb.find(Integer.valueOf(value));
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null || object.equals("0")) {
                return null;
            }
            if (object instanceof Resourceinfo) {
                Resourceinfo o = (Resourceinfo) object;
                return String.valueOf(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Resourceinfo.class.getName());
            }
        }

    }

    @FacesConverter(forClass = Roleinfo.class,value = "roleinfoConverter")
    public static class RoleinfoConverter implements Converter {

        @EJB
        RoleinfoFacadeLocal myEjb;

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0||value.equals("0")) {
                return null;
            }
            return myEjb.find(Integer.valueOf(value));
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null || object.equals("0")) {
                return null;
            }
            if (object instanceof Roleinfo) {
                Roleinfo o = (Roleinfo) object;
                return String.valueOf(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Roleinfo.class.getName());
            }
        }
    }

    @FacesConverter(forClass = Student.class,value="studentConverter")
    public static class StudentConverter implements Converter {

        @EJB
        StudentFacadeLocal myEjb;

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0||value.equals("0")) {
                return null;
            }
            return myEjb.find(Integer.valueOf(value));
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null || object.equals("0")) {
                return null;
            }
            if (object instanceof Student) {
                Student o = (Student) object;
                return String.valueOf(o.getUno());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Student.class.getName());
            }
        }

    }

    @FacesConverter(forClass = Stuentrel.class,value="stuentrelConverter")
    public static class StuentrelConverter implements Converter {

        @EJB
        StuentrelFacadeLocal myEjb;

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0||value.equals("0")) {
                return null;
            }
            return myEjb.find(Integer.valueOf(value));
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null || object.equals("0")) {
                return null;
            }
            if (object instanceof Stuentrel) {
                Stuentrel o = (Stuentrel) object;
                return String.valueOf(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Stuentrel.class.getName());
            }
        }
    }

    @FacesConverter(forClass = Teacherinfo.class,value = "teacherinfoConverter")
    public static class TeacherinfoConverter implements Converter {

        @EJB
        TeacherinfoFacadeLocal myEjb;

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0||value.equals("0")) {
                return null;
            }
            return myEjb.find(Integer.valueOf(value));
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null || object.equals("0")) {
                return null;
            }
            if (object instanceof Teacherinfo) {
                Teacherinfo o = (Teacherinfo) object;
                return String.valueOf(o.getUno());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Teacherinfo.class.getName());
            }
        }

    }
}
