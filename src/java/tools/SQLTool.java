/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.Dependent;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.sql.DataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

/**
 *
 * @author Administrator
 * @param <T>
 */
@Dependent
public class SQLTool<T> implements java.io.Serializable {

    List<T> paperClassificationList;

    public List<T> getBeanListHandlerRunner(String sql, T t) {
        ResultSetHandler<List<T>> rp = new BeanListHandler(t.getClass());
        DataSource mycon = ConnectionManager.getDataSource();
        QueryRunner run = new QueryRunner(mycon);
        try {
            paperClassificationList = run.query(run.getDataSource().getConnection(), sql, rp);
        } catch (SQLException ex) {
            FacesContext.getCurrentInstance().addMessage("OK", new FacesMessage("查找失败！"));
        } finally {
            if (null != mycon) {
                try {
                    mycon.getConnection().close();
                } catch (SQLException ex) {
                    Logger.getLogger(SQLTool.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return paperClassificationList;
    }

    public int executUpdate(String sql) {
        DataSource mycon = ConnectionManager.getDataSource();
        QueryRunner run = new QueryRunner(mycon);
        int result = 0;
        try {
            result = run.update(sql);
        } catch (SQLException ex) {
            FacesContext.getCurrentInstance().addMessage("OK", new FacesMessage("失败！"));
        } finally {
            if (null != mycon) {
                try {
                    mycon.getConnection().close();
                } catch (SQLException ex) {
                    Logger.getLogger(SQLTool.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        if (result <= 0) {
            FacesContext.getCurrentInstance().addMessage("globalMessages", new FacesMessage("失败！"));
        } else {
            FacesContext.getCurrentInstance().addMessage("globalMessages", new FacesMessage("成功！"));
        }
        return result;
    }

    public List<String> getIdListHandlerRunner(String sql) {
        DataSource mycon = ConnectionManager.getDataSource();
        QueryRunner run = new QueryRunner(mycon);
        List<String> ls = new LinkedList<>();
        ResultSetHandler<Object[]> h = new ResultSetHandler<Object[]>() {
            @Override
            public Object[] handle(ResultSet rs) throws SQLException {
                Object[] result;
                List<String> tem = new ArrayList<>();
                while (rs.next()) {
                    tem.add(rs.getString(1));
                }
                result = tem.toArray();
                return result;
            }
        };
        try {
            Object[] myTem = run.query(sql, h);
            for (Object myTem1 : myTem) {
                ls.add(myTem1.toString());
            }
        } catch (SQLException ex) {
            FacesContext.getCurrentInstance().addMessage("OK", new FacesMessage("查找失败！"));
        } finally {
            if (null != mycon) {
                try {
                    mycon.getConnection().close();
                } catch (SQLException ex) {
                    Logger.getLogger(SQLTool.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return ls;
    }
}
