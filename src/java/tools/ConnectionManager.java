package tools;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;


import javax.naming.NameNotFoundException;
import javax.sql.DataSource;

/**
 * 用于管理数据库链接，采用连接池技术
 */
public class ConnectionManager {

    // 定义一个ThreadLocal对象
    private static  Connection conn = null;
    private static DataSource dataSource = null;
    /**
     * 获取数据库链接
     *
     * @return
     */
    public static Connection getConnection() {
        if (conn == null) {
            try {
                Context ctx = new InitialContext();
                dataSource = (DataSource) ctx.lookup("jdbc/Enterprise");//这里要注意JNDI名称的大小写问题
                conn = dataSource.getConnection();
            } catch (NameNotFoundException nfe) {
                nfe.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (NamingException e) {
                e.printStackTrace();
            }
        }
        return conn;
    }
    
    public static DataSource getDataSource(){
        if(dataSource == null){
            getConnection();
        }
        return dataSource;
    }
    
    /**
     * 释放相关的资源
     *
     * @param rs
     * @param st
     */
    public static void close(ResultSet rs, Statement st) {
        if (rs != null) {
            try {
                rs.close();
            } catch (Exception e) {
            }
        }
        if (st != null) {
            try {
                st.close();
            } catch (Exception e) {
            }
        }
    }
}
