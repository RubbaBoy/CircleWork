package com.circlework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class SQLUtility {

    private static final Logger LOGGER = LoggerFactory.getLogger(SQLUtility.class);

//    public static void main(String[] args) {
//        var row = new Row();
//        int i = row.get(0);
//        String i2 = row.get(1);
//        var i3 = row.<String>get(1);
//
//        executeQuerySingle("");
//        executeQuerySingle("", 0);
//        executeQuerySingle("", 0, 0, 0, 0, 0);
//        executeQuerySingle("", new int[] {0, 0, 0, 0, 0});
//    }

    public static void executeUpdate(String update, Object... args) throws SQLException {
        try(var conn = DataSource.getConnection();
            var stmt = conn.prepareStatement(update)) {
            int index = 1;

            for (Object cur : args) {
                stmt.setObject(index++, cur);
            }

            stmt.executeUpdate();
        }
    }

    public static Row executeQuerySingle(String query, Object... args) throws SQLException{
        // thwo SQLException if row.length < 1
        if(args.length < 1){
            throw new SQLException();
        }

        var single = executeQuery(query, args);
        return single.get(0);
    }

    public static List<Row> executeQuery(String query, Object... args) throws SQLException {
        List<Row> rows = new LinkedList<>();//list for storing the created rows

        try(var conn = DataSource.getConnection();
            var stmt = conn.prepareStatement(query)){
            int index = 1;

            for(Object cur : args){
                stmt.setObject(index++, cur);
            }

            var resultSet = stmt.executeQuery();

            while(resultSet.next()){
                var collectRow = new LinkedList<>();//list for collecting all data in columns for a row
                int colNum = resultSet.getMetaData().getColumnCount();
                for(int i = 1; i <= colNum; i++){
                   collectRow.add(resultSet.getObject(i));
                }
                Row curRow = new Row(collectRow);
                rows.add(curRow);
            }

        }
        return rows;
    }

}
