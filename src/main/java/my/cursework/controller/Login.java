package my.cursework.controller;

import my.cursework.model.Checker;
import my.cursework.model.FilesystemNode;
import my.cursework.model.MysqlManager;
import my.cursework.model.Utils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

public class Login extends HttpServlet {

    private String fileUploadPath;

    @Override
    public void init(ServletConfig config) throws ServletException {
        fileUploadPath = config.getServletContext().getInitParameter("FILE_UPLOAD_PATH");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession sess;
        if((sess= request.getSession(false)) != null)
            sess.invalidate();

        request.getRequestDispatcher("views/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession sess;
        if((sess= request.getSession(false)) != null)
            sess.invalidate();
        PrintWriter out = response.getWriter();
        if (request.getSession(false) != null){
            response.sendRedirect("./listing");
            System.out.println("LOGIN::ALREADY LOGGED IN");
        }
        else {
            String username = request.getParameter("username");
            if (username == null || username.isEmpty()) {
                out.print("USERNAME IS NULL");
                System.out.println("LOGIN:Username is null");
                response.setStatus(404);
                return;
            }

            if (!Checker.isClean(username)) {
                out.print("BAD USERNAME");
                System.out.println("LOGIN:BADUSERNAME");
                response.setStatus(418);
                return;
            }

            String password = request.getParameter("password");
            if (password == null || password.isEmpty()) {
                out.print("PASSWORD IS NULL");
                System.out.println("LOGIN:password is null");
                response.setStatus(418);
                return;
            }
            if (!Checker.isCleanPassword(password)) {
                out.print("BAD PASSWORD");
                System.out.println("LOGIN:BAD PASWORD");
                return;
            }

            MysqlManager mysqlManager = new MysqlManager();

            boolean isRegistered = mysqlManager.isRegistered(username, password);
            if (isRegistered) {
                //out.println("Success");
                System.out.format("Succesfully logon with %s :: %s\n", username, password);

                sess = request.getSession();

                sess.setAttribute("username", username);

                int uid = mysqlManager.getUID(username);
                if (uid == 0) {
                    System.out.println("LOGIN:NULL UID");
                    return;
                }
                sess.setAttribute("uid", uid);
                FilesystemNode root;
                try {
                    root = Utils.load(fileUploadPath + "/" + username + "/tree.serialized");
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    root = new FilesystemNode("root", 0);
                    FilesystemNode user = new FilesystemNode(username, uid);
                    root.addChild(user);
                }


                sess.setAttribute("tree", root);
                response.setStatus(200);
                response.sendRedirect("./listing");
            }
            mysqlManager.close();
        }
    }
}
