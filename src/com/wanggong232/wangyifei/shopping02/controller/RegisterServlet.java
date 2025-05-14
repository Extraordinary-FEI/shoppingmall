package com.wanggong232.wangyifei.shopping02.controller;

import com.wanggong232.wangyifei.shopping02.dao.UserDao;
import com.wanggong232.wangyifei.shopping02.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.logging.Logger;

@WebServlet("/register")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
                 maxFileSize = 1024 * 1024 * 10,      // 10MB
                 maxRequestSize = 1024 * 1024 * 50)   // 50MB
public class RegisterServlet extends HttpServlet {
    private final UserDao userDao = new UserDao();
    private static final String UPLOAD_DIR = "uploads" + File.separator + "avatars";

    private static final Logger LOGGER = Logger.getLogger(RegisterServlet.class.getName());


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/user/register.jsp");
    }

    private String saveUploadedFile(Part filePart, String applicationPath) throws IOException {
        if (filePart == null || filePart.getSize() == 0) {
            return null; 
        }
        String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
        if (fileName == null || fileName.trim().isEmpty()){
            return null; 
        }
        String fileExtension = "";
        int i = fileName.lastIndexOf(".");
        if (i > 0) {
            fileExtension = fileName.substring(i);
        }
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

        String uploadFilePath = applicationPath + File.separator + UPLOAD_DIR;
        File uploadDir = new File(uploadFilePath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        try (InputStream fileContent = filePart.getInputStream()) {
            Files.copy(fileContent, Paths.get(uploadFilePath + File.separator + uniqueFileName), StandardCopyOption.REPLACE_EXISTING);
        }
        return UPLOAD_DIR + File.separator + uniqueFileName; 
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        String fullName = request.getParameter("fullName");
        String address = request.getParameter("address");
        String phone = request.getParameter("phone");
        String avatarPath = null;

        User tempUserForForm = new User();
        tempUserForForm.setUsername(username);
        tempUserForForm.setEmail(email);
        tempUserForForm.setFullName(fullName);
        tempUserForForm.setAddress(address);
        tempUserForForm.setPhone(phone);

        try {
            Part filePart = request.getPart("avatar");
            if (filePart != null && filePart.getSize() > 0 && filePart.getSubmittedFileName() != null && !filePart.getSubmittedFileName().isEmpty()) {
                String applicationPath = request.getServletContext().getRealPath("");
                avatarPath = saveUploadedFile(filePart, applicationPath);
                if (avatarPath != null) {
                    tempUserForForm.setAvatarPath(avatarPath.replace(File.separator, "/"));
                }
            }
        } catch (IOException | ServletException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "头像上传失败: " + e.getMessage());
            request.setAttribute("user", tempUserForForm); 
            request.getRequestDispatcher("/user/register.jsp").forward(request, response);
            return;
        }

        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty() ||
                email == null || email.trim().isEmpty()) {
            request.setAttribute("errorMessage", "用户名、密码和邮箱不能为空！");
            request.setAttribute("user", tempUserForForm);
            request.getRequestDispatcher("/user/register.jsp").forward(request, response);
            return;
        }

        if (userDao.getUserByUsername(username) != null) {
            request.setAttribute("errorMessage", "用户名已存在！");
            request.setAttribute("user", tempUserForForm);
            request.getRequestDispatcher("/user/register.jsp").forward(request, response);
            return;
        }
        
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password); 
        newUser.setEmail(email);
        newUser.setFullName(fullName);
        newUser.setAddress(address);
        newUser.setPhone(phone);
        newUser.setRole("USER"); 
        if (avatarPath != null) {
            newUser.setAvatarPath(avatarPath.replace(File.separator, "/"));
        }

        if (userDao.addUser(newUser)) {
            response.sendRedirect(request.getContextPath() + "/user/login.jsp?registration=success");
        } else {
            request.setAttribute("errorMessage", "注册失败，数据库错误或用户已存在，请稍后重试！");
            request.setAttribute("user", newUser); // Send the complete user object back
            request.getRequestDispatcher("/user/register.jsp").forward(request, response);
        }
    }
}

