package my.cursework.controller;

import my.cursework.model.FilesystemNode;
import my.cursework.model.MysqlManager;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

public class DownloadPublic extends HttpServlet {

    private String fileUploadPath;

    @Override
    public void init(ServletConfig config) throws ServletException {
        fileUploadPath = config.getServletContext().getInitParameter("FILE_UPLOAD_PATH");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        FilesystemNode root = new FilesystemNode("root", 0);
        FilesystemNode tree = (FilesystemNode)request.getSession().getAttribute("publicTree");
        root.addChild(tree);

        String hash = (String) request.getSession().getAttribute("hash");

        String file = request.getParameter("file");
        if (file == null || file.isEmpty()){
            response.setStatus(418);
            response.getWriter().println("No file specified");
            return;
        }
        MysqlManager mysqlm = new MysqlManager();
        String basePath = mysqlm.getPublicPathByHash(hash);
        mysqlm.close();
        if (basePath == null || basePath.isEmpty()){
            System.out.println("publicDownload: ERROR: No path for hash"+hash);
            return;
        }
        String userstr= basePath.split("/")[1];
        String userpath = basePath.substring(userstr.length()+1);
        // String [] test = file.split("/public/[^/]+/");
        // for (String it:test){
        //     System.out.println(it);
        // }
        String[] splitPath = file.split("/public/[^/]+/");
        String fileWOpublic;
        if(splitPath.length < 2){
            fileWOpublic = "";
        }
        else {
            fileWOpublic = splitPath[1];
        }
        System.out.println(fileWOpublic);
        System.out.println("DOwnlaodPublic:  reconstruct check user: "+userstr);
        System.out.println("DOwnlaodPublic: reconstruct check userpath: "+userpath);
        String fullpath = fileUploadPath + "/" + userstr + "/files"+userpath + "/" + fileWOpublic;
        String [] parts = file.split("/");
        String filename = parts[parts.length-1];

        System.out.println("Taret downloadpath = "+fullpath);


        File f = new File(fullpath);
        if (f == null) System.out.println("DOWNLOADPublic:DOGET:file not found:"+fullpath);
        // if (f == null) System.out.println("DOWNLOAD:DOGET:f not found:"+fullpath);
        FileInputStream inStream = new FileInputStream(fullpath);
        String mimeType = "application/octet-stream";
        response.setContentType(mimeType);
        response.setContentLength((int) f.length());

        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"", filename);
        response.setHeader(headerKey, headerValue);

        // obtains response's output stream
        OutputStream outStream = response.getOutputStream();

        byte[] buffer = new byte[4096];
        int bytesRead = -1;

        while ((bytesRead = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, bytesRead);
        }

        inStream.close();
        outStream.close();


    }

}
