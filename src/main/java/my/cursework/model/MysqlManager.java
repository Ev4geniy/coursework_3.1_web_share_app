package my.cursework.model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.*;

public class MysqlManager {

    private ResultSet rs;
    private Statement s;
    private Connection conn;
    private PreparedStatement preparedStmt = null;

    public MysqlManager(){
        rs = null;
        s = null;
        conn = null;
        try {
            try{
                Class.forName("com.mysql.jdbc.Driver");
            }
            catch(ClassNotFoundException ex){
                ex.printStackTrace();
                System.out.println("FUCK");
            }
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/courseworkDB", "tomcat", "tacmot");
            s= conn.createStatement();

//
        }
        catch (SQLException ex){
            ex.printStackTrace();
        }
    }


    public void addUser(String username, String password){
        try{

            String query = "INSERT INTO users (username, password) VALUES (?, ?)";
            preparedStmt = conn.prepareStatement(query);
            preparedStmt.setString (1, username);
            preparedStmt.setString (2, password);
            preparedStmt.executeUpdate();

        }catch(SQLException e){
            e.printStackTrace();
        }

    }

    public boolean isRegistered(String name, String password){
        boolean res = false;
        try {
            s= conn.createStatement();
            rs = s.executeQuery("SELECT * from users WHERE username='"+name+"' and password='"+password+"'");

            int rowCount = 0;
            while (rs.next()) {
                System.out.println("MYSQLM:isRegitered::UIDUID: "+rs.getString("id"));
                rowCount++;
            }
            System.out.println("MYSQLM:isRegitered::rowcount: "+ rowCount);
            if (rowCount == 1){
                res= true;
            }else{
                if(rowCount > 1){
                    System.out.format("MORE THAN 1 USER with %s :: %s\n", name, password);
                }
                else{
                    System.out.format("logon failure with %s :: %s\n", name, password);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    public String addPublicFile(String username, String path, FilesystemNode node){
        String hash = Utils.hashing(username, path);
        String test = getPublicPathByHash(hash);
        if (test != null){
            System.out.format("MYSQLM:addPublicFiles node for %s : %s already exist\n", path, hash);
            return hash;
        }
        try{
            String query = "INSERT INTO public_files (path, hash, node) VALUES (?, ?, ?)";
            preparedStmt = conn.prepareStatement(query);

            System.out.format("MYSQLM:addPublicFiles Creating line in public_files: %s : %s \n", path, hash);
            preparedStmt.setString (1, path);
            preparedStmt.setString (2, hash);
            preparedStmt.setObject(3, node);
            preparedStmt.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
        return hash;
    }

    public FilesystemNode getPublicNodeByHash(String hash){
        FilesystemNode node = null;
        try{
            s= conn.createStatement();
            rs = s.executeQuery("SELECT node from public_files WHERE hash='" + hash + "'");
            int rowCount = 0;
            while (rs.next()) {
                try{
                    byte[] buf = rs.getBytes(1);
                    ObjectInputStream objectIn;
                    if (buf != null){
                        objectIn = new ObjectInputStream(new ByteArrayInputStream(buf));
                        node = (FilesystemNode) objectIn.readObject();
                    }else{
                        System.out.print("Mysqlm:getNode:BUF IS NULL");
                    }


                }catch (ClassNotFoundException | IOException ex){
                    ex.printStackTrace();
                }

                rowCount++;
            }
            if (rowCount == 1){
                System.out.println("MYSQLM:getNode: "+node.getName());
            }else{
                if (rowCount >1){
                    System.out.println("MYSQLM:getNode: multiple nodes for: "+hash);
                }
                else{
                    System.out.println("MYSQLM:getNode: no such node: "+hash);
                }

            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return node;
    }

    public String getPublicPathByHash(String hash){
        String path = null;
        try{
            s = conn.createStatement();
            rs = s.executeQuery("SELECT path from public_files WHERE hash='" + hash + "'");
            int rowCount = 0;


            while (rs.next()) {
                path = rs.getString("path");
                System.out.println("SQLM::path: "+rs.getString("path"));
                rowCount++;
            }
            if (rowCount == 1){
                System.out.println("MYSQLM:Path successfull: "+path);
            }
            else{
                if (rowCount >1){
                    System.out.println("MYSQLM:Path Multiple path for hash: "+hash);
                }else{
                    System.out.println("MYSQLM:getNode: no such path: "+hash);
                }
            }

        }catch(SQLException e){
            e.printStackTrace();
        }
        return path;
    }

    public int getUID(String username){
        int id = 0;
        try{
            s = conn.createStatement();
            rs = s.executeQuery("SELECT id from users WHERE username='" + username + "'");
            int rowCount = 0;
            String uid = null;

            while (rs.next()) {
                uid = rs.getString("id");
                System.out.println("SQLM::UIDUID: "+rs.getString("id"));
                rowCount++;
            }
            if (rowCount == 1){
                try{
                    id = Integer.parseInt(uid);
                }
                catch (NumberFormatException ex){
                    ex.printStackTrace();
                }

            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        // finally{
        //   close();
        // }
        return id;
    }


    public void close(){
        if (rs != null) {
            try { rs.close(); } catch (SQLException e) { ; }
            rs = null;
        }
        if (s != null) {
            try { s.close(); } catch (SQLException e) { ; }
            s = null;
        }
        if (preparedStmt != null) {
            try { preparedStmt.close(); } catch (SQLException e) { ; }
            preparedStmt = null;
        }
        if (conn != null) {
            try { conn.close(); } catch (SQLException e) { ; }
            conn = null;
        }
    }
}
