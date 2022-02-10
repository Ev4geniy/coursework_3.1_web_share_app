package my.cursework.controller;

import my.cursework.model.FilesystemNode;
import my.cursework.model.Utils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

public class Create extends HttpServlet {

    private String basePath;
    private FilesystemNode user;

    @Override
    public void init(ServletConfig config) throws ServletException {
        basePath = config.getServletContext().getInitParameter("FILE_UPLOAD_PATH");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String creating = request.getParameter("creating");
        if(creating != null && creating.equals("file")){
            createFile(request, response);
        }
        else if(creating != null && creating.equals("directory")){
            createDirectory(request, response);
        }
        else {
            System.out.println("Wrong request: parameter $creating$ == " + creating);
        }

    }

    private void createFile(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        String pathToUpload = request.getParameter("path");
        Enumeration<String> params = request.getParameterNames();
        boolean isShared = false;

        while (params.hasMoreElements()){
            if (params.nextElement().equals("shared")){
                isShared = true;
                pathToUpload = pathToUpload.substring(7);
                System.out.println("upload:shared == TRUE");
                break;
            }
        }

        System.out.println("NEW FILE PATH:" + pathToUpload);

        Part filePart = request.getPart("file");
        if (filePart == null){
            System.out.println("NO PARTS!!!!!1");
        }
        assert filePart != null;
        String fileName = filePart.getSubmittedFileName();

        String username = (String)request.getSession().getAttribute("username");
        int uid = (int)request.getSession().getAttribute("uid");
        String userstr= pathToUpload.split("/")[1];
        String userpath = pathToUpload.substring(userstr.length() + 1);
        System.out.println("Path reconstruct check user: " + userstr);
        System.out.println("Path reconstruct check userpath: " + userpath);

        String fullpath = basePath + "/" + userstr + "/files" + userpath + "/" + fileName;  //files - /; add / bef filename
        System.out.println("TEST2");
        System.out.println("FULLPATH: " + fullpath);
        String [] splitPath= pathToUpload.split("/");
        FilesystemNode currentTarget = null;

        FilesystemNode root = null;
        if (isShared){
            System.out.println("USERNAME WHO SHARE: "+userstr);
            try {
//                FilesystemNode currentUserSharedTree = Utils.load(basePath + "/" + username + "/shared.serialized");
//                root = Utils.load(basePath + "/" + userstr + "/tree.serialized");
//
//                FilesystemNode sharedTarget = currentUserSharedTree;
//                for (String pth : splitPath){
//                    if(!pth.equals("/") && !pth.isEmpty()){
//                        System.out.println(pth);
//                        sharedTarget = sharedTarget.getChild(pth);
//                    }
//                }
//                pathToUpload =
//                fullpath = basePath + "/" + userstr + "/files/" + sharedTarget.getFullName().split("/", 3)[2] + "/" + fileName;
//                System.out.println("Update full path: " + fullpath);
                root = Utils.load(basePath + "/" + username + "/shared.serialized");
                if(root != null){

                    for (String shared : splitPath){
                        if(!shared.equals("/") && !shared.isEmpty()){
                            root = root.getChild(shared);
                        }
                    }
                    currentTarget = root;

                    fullpath = basePath+"/"+userstr+"/files" + root.getFullName().substring(userstr.length() + 1);
                    pathToUpload = root.getFullName();
                    System.out.println("DOWNLOAD::DO_GET::correct full path is: " + fullpath);
                    root = Utils.load(basePath + "/" + userstr + "/tree.serialized");
                }
//                root = Utils.load(basePath + "/"+userstr+"/tree.serialized");
            }
            catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        else{
            root = (FilesystemNode)request.getSession().getAttribute("tree");
        }


        FilesystemNode target = null;


        target = root;
        for (String it: pathToUpload.split("/")){
            if(!it.equals("/") && !it.isEmpty()){
                System.out.println(it);
                target = target.getChild(it);
            }
        }

        if (!isShared){


            FilesystemNode child = new FilesystemNode(fileName, uid);
            System.out.format("CREATE::DOPOST:Creating file with uid %s\n", uid);
            child.setFile();
            target.addChild(child);
            Utils.save(root, basePath + "/" + username + "/tree.serialized");
            filePart.write(fullpath);
        }
        else {
            FilesystemNode f = currentTarget.getChild(fileName);
            System.out.println();
            if(f != null){
                while(f.getSharedParent() == null){
                    System.out.println("Checking f " + f.getName());
                    f = f.getParent();
                }
                System.out.println(f);
                boolean isWritable = f.getPermissions().isGroupWritable(uid);
                if(isWritable){
                    System.out.println("CREATE::writing to " + fullpath + "/" + fileName);
                    filePart.write(fullpath + "/" + fileName);
                }
                else {
                    System.out.format("CREATE ERROR: user with uid: %s can't write\n", uid);
                    response.setStatus(418);
                }
            }
//            System.out.println("Node: "+f.getFullName());
//            System.out.println("Can overvrite: "+f.getPermissions().isGroupWritable(uid));
//            if (f.getPermissions().isGroupWritable(uid)) filePart.write(fullpath);
//            else {
//                System.out.format("CREATE ERROR: user with uid: %s can't write\n", uid);
//                response.setStatus(418);
//            }
        }
    }

    private void createDirectory(HttpServletRequest request, HttpServletResponse response) {

        String username = (String) request.getSession().getAttribute("username");
        if (username == null) {
            System.out.println("CREATE:DOGET:ERRORR null username");
        } else {
            System.out.println("CREATE:DOGET:request for DIR Creation from: " + username);
        }
        user = (FilesystemNode) request.getSession().getAttribute("tree");
        if (user == null) {
            System.out.println("CREATE:DOGET:ERRORR user: null username");
        } else {
            System.out.println("CREATE:DOGET:treeCheck:" + user.getName());
        }
        int uid = (int) request.getSession().getAttribute("uid");
        if (uid == 0) {
            System.out.println("CREATE:DOGET:ERRORR null uid");
        } else {
            System.out.println("CREATE:DOGET:UID FOR DIR: " + String.valueOf(uid));
        }

        response.setContentType("text/html");//setting the content type

        String path = request.getParameter("path");
        if (path == null) {
            System.out.println("ERROR:CREATE PATH IS NULL");
        }
        else{

            if (path.isEmpty()) {
                System.out.println("Empty path was fixed");
                path = "/";
            }

            String dirname = request.getParameter("dirname");
            if (dirname == null || dirname.isEmpty()) {
                dirname = "/";
                //TODO: return error status code and abort
            }

//             Node root = new Node("root", 123);
//             root.addChildren(user);

            FilesystemNode target = user.findNode(path);
            FilesystemNode child = new FilesystemNode(dirname, uid);
            target.addChild(child);

            String userStr = path.split("/")[1];
            String userPath = path.substring(userStr.length() + 1);

            System.out.println("DIRPath reconstruct check user: " + userStr);
            System.out.println("DIRPath reconstruct check userPath: " + userPath);

            String fullPath = basePath + "/" + userStr + "/files" + userPath + "/" + dirname;

            System.out.println("CREATING DIR: " + fullPath);
            System.out.format("CREATE::DOPOST:CReating dir with uid %s\n", uid);

            new File(fullPath).mkdirs();

            System.out.println("New folder created:" + child.getFullName());
            System.out.println(target.toStringTerminal());

            Utils.save(user, basePath + "/" + username + "/tree.serialized");
        }
    }
}
