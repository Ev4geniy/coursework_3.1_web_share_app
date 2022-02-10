package my.cursework.controller;

import my.cursework.model.FilesystemNode;
import my.cursework.model.Utils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class ListShared extends HttpServlet {

    private String fileUploadPath;

    @Override
    public void init(ServletConfig config) throws ServletException {
        fileUploadPath = config.getServletContext().getInitParameter("FILE_UPLOAD_PATH");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.getRequestDispatcher("views/listShared.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String username = (String) request.getSession().getAttribute("username");

        response.setContentType("text/html");
        PrintWriter pw = response.getWriter();

        String dir = request.getParameter("dir");
        if (dir == null || dir.isEmpty()){
            dir = "/shared";
        }
        FilesystemNode shared = null;
        try {
            shared = Utils.load(fileUploadPath + "/" + username + "/shared.serialized");
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        FilesystemNode sharedRoot = new FilesystemNode("root", 0);
        sharedRoot.addChild(shared);

        String [] splitPath = dir.split("/");
        FilesystemNode target = sharedRoot;

        for (String it: splitPath){
            if(!it.equals("/") && !it.isEmpty()){
                target = target.getChild(it);
                System.out.println("TARGET =" + it);
            }

        }
        if(!target.isFile()){

            pw.println("<div class=\"current-directory\">");
            pw.println(target.getSharedFullName().replace("/", " > "));
            pw.println("</div>");

            pw.print("<div id=\"currentDir\"  value=\"");
            pw.print(target.getSharedFullName());
            pw.println("\" /></div>");

            pw.println("<div class=\"file-listing\">");

            pw.print("<a class=\"go-back\" onclick=\"getPage('");
            if(target.getSharedParent() != null){
                pw.print(target.getSharedParent().getSharedFullName());
            }
            else{
                pw.print(target.getParent().getSharedFullName());
            }
            pw.println("')\">..</a><br>");

            for(FilesystemNode child : target.getChildrenMap().values()){
                if (child.isFile()) {
                    pw.print("<div class=\"file\" onclick=\"downloadFile('");
                    pw.print(child.getSharedFullName());
                    pw.println("', false, true)\">");
                }
                else {
                    pw.print("<div class=\"folder\" onclick=\"getPage('");
                    pw.print(child.getSharedFullName());
                    pw.println("')\">");
                }
                pw.println(child.getName());
                pw.println("</div><br>");
            }
//            pw.println(target.printSharedContent());
        }
        pw.close();
        System.out.println("SHARED TREE: " + shared.toStringTerminal());

//        pw.println(shared.printSharedContent());
//        pw.println("<body></html>");
    }
}
