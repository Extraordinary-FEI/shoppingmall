package com.wanggong232.wangyifei.shopping02.controller.admin;

import com.wanggong232.wangyifei.shopping02.dao.UserDao;
import com.wanggong232.wangyifei.shopping02.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@WebServlet("/admin/users")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
                 maxFileSize = 1024 * 1024 * 10,      // 10MB
                 maxRequestSize = 1024 * 1024 * 50)   // 50MB
public class AdminUserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserDao userDao;
    // Ensure UPLOAD_DIR uses File.separator for construction but relative paths for DB are forward slashes
    private static final String RELATIVE_UPLOAD_DIR = "uploads/avatars"; 

    public void init() {
        userDao = new UserDao();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null || !"ADMIN".equals(((User)session.getAttribute("currentUser")).getRole())) {
            response.sendRedirect(request.getContextPath() + "/user/login.jsp?error=AdminAccessRequired");
            return;
        }

        String action = request.getParameter("action");
        if (action == null) {
            action = "list"; // Default action
        }

        switch (action) {
            case "edit":
                showEditUserForm(request, response);
                break;
            case "delete":
                deleteUser(request, response);
                break;
            case "search":
                searchUsers(request, response);
                break;
            case "list":
            default:
                listUsers(request, response);
                break;
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null || !"ADMIN".equals(((User)session.getAttribute("currentUser")).getRole())) {
            response.sendRedirect(request.getContextPath() + "/user/login.jsp?error=AdminAccessRequired");
            return;
        }
        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        if (action == null) {
            listUsers(request, response); 
            return;
        }

        switch (action) {
            case "add":
                insertUser(request, response);
                break;
            case "edit":
                updateUser(request, response);
                break;
            case "bulkDelete":
                bulkDeleteUsers(request, response);
                break;
            default:
                listUsers(request, response);
                break;
        }
    }

    private void listUsers(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<User> userList = userDao.getAllUsers();
        request.setAttribute("userList", userList);
        request.getRequestDispatcher("/admin/user_list.jsp").forward(request, response);
        // Forward messages from session if they exist (e.g., after add/edit/delete)
        HttpSession session = request.getSession();
        if (session.getAttribute("message") != null) {
            // 在调用本接口之前，如果塞了消息属性值的话，就不需要重复塞了
            // request.setAttribute("message", session.getAttribute("message"));
            session.removeAttribute("message");
        }
        if (session.getAttribute("errorMessage") != null) {
            // 在调用本接口之前，如果塞了消息属性值的话，就不需要重复塞了
            // request.setAttribute("errorMessage", session.getAttribute("errorMessage"));
            session.removeAttribute("errorMessage");
        }
    }

    private void searchUsers(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String searchTerm = request.getParameter("searchTerm");
        List<User> userList;
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            userList = userDao.searchUsers(searchTerm.trim());
            request.setAttribute("message", "搜索结果: " + searchTerm);
        } else {
            userList = userDao.getAllUsers(); 
        }
        request.setAttribute("userList", userList);
        request.getRequestDispatcher("/admin/user_list.jsp").forward(request, response);
    }

    private void showEditUserForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int userId = Integer.parseInt(request.getParameter("userId"));
            User existingUser = userDao.getUserById(userId);
            if (existingUser != null) {
                request.setAttribute("user", existingUser);
                request.setAttribute("formAction", "edit");
                request.getRequestDispatcher("/admin/user_form.jsp").forward(request, response);
            } else {
                request.getSession().setAttribute("errorMessage", "未找到ID为 " + userId + " 的用户");
                response.sendRedirect(request.getContextPath() + "/admin/users");
            }
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMessage", "无效的用户ID格式");
            response.sendRedirect(request.getContextPath() + "/admin/users");
        }
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

        // Construct the absolute path for saving the file
        String absoluteUploadDirPath = applicationPath + File.separator + RELATIVE_UPLOAD_DIR.replace("/", File.separator);
        File uploadDir = new File(absoluteUploadDirPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        try (InputStream fileContent = filePart.getInputStream()) {
            Files.copy(fileContent, Paths.get(absoluteUploadDirPath + File.separator + uniqueFileName), StandardCopyOption.REPLACE_EXISTING);
        }
        // Return the relative path with forward slashes for DB and web access
        return RELATIVE_UPLOAD_DIR + "/" + uniqueFileName;
    }

    private void insertUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password"); 
        String fullName = request.getParameter("fullName");
        String address = request.getParameter("address");
        String phone = request.getParameter("phone");
        String role = request.getParameter("role");
        String avatarPath = null;

        User tempUser = new User(username, "", email, fullName, address, phone, role);

        try {
            Part filePart = request.getPart("avatar");
            if (filePart != null && filePart.getSize() > 0 && filePart.getSubmittedFileName() != null && !filePart.getSubmittedFileName().isEmpty()) {
                String applicationPath = request.getServletContext().getRealPath("");
                avatarPath = saveUploadedFile(filePart, applicationPath);
            }
        } catch (IOException | ServletException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "头像上传失败: " + e.getMessage());
            request.setAttribute("user", tempUser);
            request.setAttribute("formAction", "add");
            request.getRequestDispatcher("/admin/user_form.jsp").forward(request, response);
            return;
        }
        
        if (username == null || username.trim().isEmpty() || email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty() || role == null || role.trim().isEmpty()) {
            request.setAttribute("errorMessage", "用户名、邮箱、密码和角色不能为空。");
            tempUser.setAvatarPath(avatarPath); 
            request.setAttribute("user", tempUser);
            request.setAttribute("formAction", "add");
            request.getRequestDispatcher("/admin/user_form.jsp").forward(request, response);
            return;
        }

        if (userDao.getUserByUsername(username) != null) {
            request.setAttribute("errorMessage", "用户名 '" + username + "' 已存在。");
            tempUser.setAvatarPath(avatarPath);
            request.setAttribute("user", tempUser);
            request.setAttribute("formAction", "add");
            request.getRequestDispatcher("/admin/user_form.jsp").forward(request, response);
            return;
        }
        
        User newUser = new User(username, password, email, fullName, address, phone, role);
        if (avatarPath != null) {
            newUser.setAvatarPath(avatarPath); // Already uses forward slashes from saveUploadedFile
        }

        if (userDao.addUser(newUser)) {
            request.getSession().setAttribute("message", "用户 " + username + " 添加成功！");
            response.sendRedirect(request.getContextPath() + "/admin/users");
        } else {
            request.setAttribute("errorMessage", "添加用户失败，请检查数据或联系管理员。");
            newUser.setAvatarPath(avatarPath); 
            request.setAttribute("user", newUser);
            request.setAttribute("formAction", "add");
            request.getRequestDispatcher("/admin/user_form.jsp").forward(request, response);
        }
    }

    private void updateUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int userId = Integer.parseInt(request.getParameter("userId"));
            String username = request.getParameter("username");
            String email = request.getParameter("email");
            String fullName = request.getParameter("fullName");
            String address = request.getParameter("address");
            String phone = request.getParameter("phone");
            String role = request.getParameter("role");
            String existingAvatarPath = request.getParameter("existingAvatarPath"); 
            String newAvatarPath = null; // To store path of newly uploaded avatar

            User userForFormRepopulation = userDao.getUserById(userId);
            if(userForFormRepopulation == null){
                 request.getSession().setAttribute("errorMessage", "尝试更新的用户不存在。");
                 response.sendRedirect(request.getContextPath() + "/admin/users");
                 return;
            }
            userForFormRepopulation.setUsername(username); userForFormRepopulation.setEmail(email); 
            userForFormRepopulation.setFullName(fullName); userForFormRepopulation.setAddress(address);
            userForFormRepopulation.setPhone(phone); userForFormRepopulation.setRole(role);
            userForFormRepopulation.setAvatarPath(existingAvatarPath); // Initialize with existing for repopulation

            try {
                Part filePart = request.getPart("avatar");
                if (filePart != null && filePart.getSize() > 0 && filePart.getSubmittedFileName() != null && !filePart.getSubmittedFileName().isEmpty()) {
                    String applicationPath = request.getServletContext().getRealPath("");
                    newAvatarPath = saveUploadedFile(filePart, applicationPath);
                    if (newAvatarPath != null) {
                        userForFormRepopulation.setAvatarPath(newAvatarPath); // Update for form repopulation if new one is saved
                    } else {
                        // File save failed, keep existing for repopulation, newAvatarPath remains null
                    }
                }
            } catch (IOException | ServletException e) {
                e.printStackTrace();
                request.setAttribute("errorMessage", "头像上传失败: " + e.getMessage());
                request.setAttribute("user", userForFormRepopulation);
                request.setAttribute("formAction", "edit");
                request.getRequestDispatcher("/admin/user_form.jsp").forward(request, response);
                return;
            }

            if (username == null || username.trim().isEmpty() || email == null || email.trim().isEmpty() || role == null || role.trim().isEmpty()) {
                request.setAttribute("errorMessage", "用户名、邮箱和角色不能为空。");
                request.setAttribute("user", userForFormRepopulation);
                request.setAttribute("formAction", "edit");
                request.getRequestDispatcher("/admin/user_form.jsp").forward(request, response);
                return;
            }

            User userToUpdate = userDao.getUserById(userId); 
            if (userToUpdate == null) { 
                request.getSession().setAttribute("errorMessage", "尝试更新的用户不存在。");
                response.sendRedirect(request.getContextPath() + "/admin/users");
                return;
            }

            User existingUserWithNewUsername = userDao.getUserByUsername(username);
            if (existingUserWithNewUsername != null && existingUserWithNewUsername.getUserId() != userId) {
                request.setAttribute("errorMessage", "用户名 '" + username + "' 已被其他用户占用。");
                request.setAttribute("user", userForFormRepopulation);
                request.setAttribute("formAction", "edit");
                request.getRequestDispatcher("/admin/user_form.jsp").forward(request, response);
                return;
            }

            userToUpdate.setUsername(username);
            userToUpdate.setEmail(email);
            userToUpdate.setFullName(fullName);
            userToUpdate.setAddress(address);
            userToUpdate.setPhone(phone);
            userToUpdate.setRole(role);
            
            if (newAvatarPath != null) { // If a new avatar was successfully uploaded and saved
                userToUpdate.setAvatarPath(newAvatarPath);
            } else {
                // No new avatar uploaded or save failed, retain the existing one from DB (or what was in the form)
                userToUpdate.setAvatarPath(existingAvatarPath); 
            }

            if (userDao.updateUser(userToUpdate)) {
                request.getSession().setAttribute("message", "用户 " + username + " 更新成功！");
                response.sendRedirect(request.getContextPath() + "/admin/users");
            } else {
                request.setAttribute("errorMessage", "更新用户失败。");
                request.setAttribute("user", userForFormRepopulation); 
                request.setAttribute("formAction", "edit");
                request.getRequestDispatcher("/admin/user_form.jsp").forward(request, response);
            }
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMessage", "无效的用户ID格式。");
            response.sendRedirect(request.getContextPath() + "/admin/users");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "更新用户时发生意外错误: " + e.getMessage());
            // Try to repopulate form with as much data as possible
            try {
                int userId = Integer.parseInt(request.getParameter("userId"));
                User user = userDao.getUserById(userId) != null ? userDao.getUserById(userId) : new User();
                user.setUsername(request.getParameter("username"));
                user.setEmail(request.getParameter("email"));
                user.setFullName(request.getParameter("fullName"));
                user.setAddress(request.getParameter("address"));
                user.setPhone(request.getParameter("phone"));
                user.setRole(request.getParameter("role"));
                user.setAvatarPath(request.getParameter("existingAvatarPath"));
                request.setAttribute("user", user);
            } catch (Exception ex) {/* ignore repopulation error */}
            request.setAttribute("formAction", "edit");
            request.getRequestDispatcher("/admin/user_form.jsp").forward(request, response);
        }
    }

    private void deleteUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int userId = Integer.parseInt(request.getParameter("userId"));
            User userToDelete = userDao.getUserById(userId);
            if (userToDelete != null && "ADMIN".equals(userToDelete.getRole()) && userId == ((User)request.getSession().getAttribute("currentUser")).getUserId()){
                request.getSession().setAttribute("errorMessage", "不能删除当前登录的管理员账户。");
            } else if (userDao.deleteUser(userId)) {
                // Optionally, delete avatar file from server here
                // String avatarToDeletePath = userToDelete.getAvatarPath();
                // if (avatarToDeletePath != null && !avatarToDeletePath.isEmpty()) {
                //    String fullPath = request.getServletContext().getRealPath("") + File.separator + avatarToDeletePath.replace("/", File.separator);
                //    File avatarFile = new File(fullPath);
                //    if (avatarFile.exists()) avatarFile.delete();
                // }
                request.getSession().setAttribute("message", "用户ID " + userId + " 删除成功！");
            } else {
                request.getSession().setAttribute("errorMessage", "删除用户ID " + userId + " 失败。");
            }
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMessage", "无效的用户ID格式。");
        }
        response.sendRedirect(request.getContextPath() + "/admin/users");
    }

    private void bulkDeleteUsers(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String[] userIdsParam = request.getParameterValues("userIds");
        if (userIdsParam == null || userIdsParam.length == 0) {
            request.getSession().setAttribute("errorMessage", "没有选择任何用户进行删除。");
            response.sendRedirect(request.getContextPath() + "/admin/users");
            return;
        }

        int deletedCount = 0;
        int currentAdminId = ((User)request.getSession().getAttribute("currentUser")).getUserId();
        boolean selfDeleteAttempt = false;

        for (String userIdStr : userIdsParam) {
            try {
                int userId = Integer.parseInt(userIdStr);
                if (userId == currentAdminId) {
                    selfDeleteAttempt = true;
                    continue; 
                }
                // User userToDelete = userDao.getUserById(userId); // Fetch before delete for avatar path
                if (userDao.deleteUser(userId)) {
                    // Optionally, delete avatar file from server here
                    // if (userToDelete != null && userToDelete.getAvatarPath() != null && !userToDelete.getAvatarPath().isEmpty()) { ... delete logic ... }
                    deletedCount++;
                }
            } catch (NumberFormatException e) {
                System.err.println("Invalid user ID in bulk delete: " + userIdStr);
            }
        }

        StringBuilder message = new StringBuilder();
        if (deletedCount > 0) {
            message.append("成功批量删除 ").append(deletedCount).append(" 个用户。");
        }
        if (selfDeleteAttempt) {
            if (message.length() > 0) message.append(" ");
            message.append("无法删除当前登录的管理员账户。");
        }
        if (deletedCount == 0 && !selfDeleteAttempt && userIdsParam.length > 0) {
            request.getSession().setAttribute("errorMessage", "批量删除用户失败或未选中有效用户（除当前管理员外）。");
        } else if (message.length() > 0){
            request.getSession().setAttribute("message", message.toString());
        }
        response.sendRedirect(request.getContextPath() + "/admin/users");
    }
}

