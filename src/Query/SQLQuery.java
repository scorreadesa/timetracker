package Query;

import java.sql.*;
import java.util.TreeMap;

public class SQLQuery {

    private Connection conn_;
    private TreeMap<String, ResultSet> cache_;

    public SQLQuery(String driver, String url, String user, String pwd) throws SQLException, ClassNotFoundException
    {
        Class.forName(driver);
        conn_ = DriverManager.getConnection(url, user, pwd);
        cache_ = new TreeMap<>();
    }

    public ResultSet query(String query) throws SQLException
    {

        PreparedStatement statement = conn_.prepareStatement(query);
        ResultSet query_results = statement.executeQuery();
        cache_.put(query, query_results);
        return query_results;
    }

    public void insert(String query) throws SQLException{

        PreparedStatement statement = conn_.prepareStatement(query);
        statement.executeUpdate();
    }
}
