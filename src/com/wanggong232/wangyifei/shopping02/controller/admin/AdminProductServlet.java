package com.wanggong232.wangyifei.shopping02.controller.admin;


import com.wanggong232.wangyifei.shopping02.dao.ProductDao;
import com.wanggong232.wangyifei.shopping02.model.Product;
import com.wanggong232.wangyifei.shopping02.model.SubCategory;
import com.wanggong232.wangyifei.shopping02.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@WebServlet("/admin/products")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 10,      // 10MB
        maxRequestSize = 1024 * 1024 * 50)   // 50MB
public class AdminProductServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ProductDao productDao;
    private static final String UPLOAD_DIR = "uploads/products"; // Relative to webapp directory

    public void init() {
        productDao = new ProductDao();
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
            case "add":
                showNewProductForm(request, response);
                break;
            case "edit":
                showEditProductForm(request, response);
                break;
            case "delete":
                deleteProduct(request, response);
                break;
            case "search":
                searchProducts(request, response);
                break;
            case "list":
            default:
                listProducts(request, response);
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
            listProducts(request, response);
            return;
        }

        switch (action) {
            case "add":
                insertProduct(request, response);
                break;
            case "edit":
                updateProduct(request, response);
                break;
            case "bulkDelete":
                bulkDeleteProducts(request, response);
                break;
            default:
                listProducts(request, response);
                break;
        }
    }


    private void listProducts(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Product> productList = productDao.getAllProducts();
        request.setAttribute("productList", productList);
        // Forward messages from session if they exist (e.g., after add/edit/delete)
        HttpSession session = request.getSession();
        if (session.getAttribute("message") != null) {
            request.setAttribute("message", session.getAttribute("message"));
            session.removeAttribute("message");
        }
        if (session.getAttribute("errorMessage") != null) {
            request.setAttribute("errorMessage", session.getAttribute("errorMessage"));
            session.removeAttribute("errorMessage");
        }
        request.getRequestDispatcher("/admin/product_list.jsp").forward(request, response);
    }

    private void searchProducts(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String searchTerm = request.getParameter("searchTerm");
        List<Product> productList;
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            productList = productDao.searchProducts(searchTerm.trim());
            request.setAttribute("message", "搜索结果: " + searchTerm);
        } else {
            productList = productDao.getAllProducts();
        }
        request.setAttribute("productList", productList);
        request.getRequestDispatcher("/admin/product_list.jsp").forward(request, response);
    }

    private void showNewProductForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("product", new Product());
        request.setAttribute("formAction", "add");
        // 获取所有小类别
        List<SubCategory> subCategories = productDao.getAllSubCategories();
        request.setAttribute("subCategories", subCategories);
        request.getRequestDispatcher("/admin/product_form.jsp").forward(request, response);
    }

    private void showEditProductForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int productId = Integer.parseInt(request.getParameter("productId"));
            Product existingProduct = productDao.getProductById(productId);
            if (existingProduct != null) {
                request.setAttribute("product", existingProduct);
                request.setAttribute("formAction", "edit");
                request.getRequestDispatcher("/admin/product_form.jsp").forward(request, response);
            } else {
                request.getSession().setAttribute("errorMessage", "未找到ID为 " + productId + " 的商品");
                response.sendRedirect(request.getContextPath() + "/admin/products");
            }
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMessage", "无效的商品ID格式");
            response.sendRedirect(request.getContextPath() + "/admin/products");
        }
    }

    private String uploadFile(HttpServletRequest request, Part filePart) throws IOException {
        String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString(); // MSIE fix.
        if (fileName == null || fileName.isEmpty()) {
            return null;
        }
        String fileExtension = "";
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            fileExtension = fileName.substring(i);
        }
        String newFileName = UUID.randomUUID().toString() + fileExtension;

        String applicationPath = request.getServletContext().getRealPath("");
        String uploadFilePath = applicationPath + File.separator + UPLOAD_DIR;

        File uploadDir = new File(uploadFilePath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        File file = new File(uploadDir, newFileName);
        try (var inputStream = filePart.getInputStream()) {
            Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        return UPLOAD_DIR + "/" + newFileName; // Return relative path for web access
    }

    private void insertProduct(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String description = request.getParameter("description");
        String priceStr = request.getParameter("price");
        String stockQuantityStr = request.getParameter("stockQuantity");
        String category = request.getParameter("category");
        String categoryIdStr = request.getParameter("categoryId");
        String subCategoryIdStr = request.getParameter("subCategoryId"); // 新增小类别 ID 获取
        String existingImageUrl = request.getParameter("existingImageUrl");


        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setCategory(category);
        product.setImageUrl(existingImageUrl); // Set existing first, override if new file uploaded

        // Basic Validation
        if (name == null || name.trim().isEmpty() || priceStr == null || priceStr.trim().isEmpty() || stockQuantityStr == null || stockQuantityStr.trim().isEmpty()) {
            request.setAttribute("errorMessage", "商品名称、价格和库存不能为空。");
            request.setAttribute("product", product); // Repopulate form with entered data
            request.setAttribute("formAction", "add");
            request.getRequestDispatcher("/admin/product_form.jsp").forward(request, response);
            return;
        }

        try {
            product.setPrice(new BigDecimal(priceStr));
            product.setStockQuantity(Integer.parseInt(stockQuantityStr));
            product.setCategoryId(Integer.parseInt(categoryIdStr));
            product.setSubCategoryId(Integer.parseInt(subCategoryIdStr)); // 新增小类别 ID 设置
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "价格、库存数量、类别 ID 或小类别 ID 格式无效。");
            request.setAttribute("product", product);
            request.setAttribute("formAction", "add");
            request.getRequestDispatcher("/admin/product_form.jsp").forward(request, response);
            return;
        }

        Part filePart = request.getPart("imageFile");
        String uploadedFilePath = null;
        if (filePart != null && filePart.getSize() > 0) {
            try {
                uploadedFilePath = uploadFile(request, filePart);
                if (uploadedFilePath != null) {
                    product.setImageUrl(uploadedFilePath);
                }
            } catch (IOException e) {
                request.setAttribute("errorMessage", "图片上传失败: " + e.getMessage());
                request.setAttribute("product", product);
                request.setAttribute("formAction", "add");
                request.getRequestDispatcher("/admin/product_form.jsp").forward(request, response);
                return;
            }
        }

        if (productDao.addProduct(product)) {
            request.getSession().setAttribute("message", "商品 \"" + name + "\" 添加成功！");
            response.sendRedirect(request.getContextPath() + "/admin/products");
        } else {
            request.setAttribute("errorMessage", "添加商品失败，请检查数据。");
            request.setAttribute("product", product);
            request.setAttribute("formAction", "add");
            request.getRequestDispatcher("/admin/product_form.jsp").forward(request, response);
        }
    }

    private void updateProduct(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int productId = Integer.parseInt(request.getParameter("productId"));
            String name = request.getParameter("name");
            String description = request.getParameter("description");
            String priceStr = request.getParameter("price");
            String stockQuantityStr = request.getParameter("stockQuantity");
            String category = request.getParameter("category");
            String existingImageUrl = request.getParameter("existingImageUrl"); // Current image path

            Product product = productDao.getProductById(productId);
            if (product == null) {
                request.getSession().setAttribute("errorMessage", "尝试更新的商品不存在。");
                response.sendRedirect(request.getContextPath() + "/admin/products");
                return;
            }

            // Set product fields for repopulation in case of error
            product.setName(name);
            product.setDescription(description);
            product.setCategory(category);
            // Don't clear existing image URL unless a new one is successfully uploaded
            if(existingImageUrl != null && !existingImageUrl.isEmpty()) product.setImageUrl(existingImageUrl);

            if (name == null || name.trim().isEmpty() || priceStr == null || priceStr.trim().isEmpty() || stockQuantityStr == null || stockQuantityStr.trim().isEmpty()) {
                request.setAttribute("errorMessage", "商品名称、价格和库存不能为空。");
                request.setAttribute("product", product);
                request.setAttribute("formAction", "edit");
                request.getRequestDispatcher("/admin/product_form.jsp").forward(request, response);
                return;
            }

            try {
                product.setPrice(new BigDecimal(priceStr));
                product.setStockQuantity(Integer.parseInt(stockQuantityStr));
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "价格或库存数量格式无效。");
                request.setAttribute("product", product);
                request.setAttribute("formAction", "edit");
                request.getRequestDispatcher("/admin/product_form.jsp").forward(request, response);
                return;
            }

            Part filePart = request.getPart("imageFile");
            String uploadedFilePath = null;
            if (filePart != null && filePart.getSize() > 0) {
                try {
                    // Optionally: delete old image if a new one is uploaded and names are different
                    // String oldImagePath = product.getImageUrl();
                    uploadedFilePath = uploadFile(request, filePart);
                    if (uploadedFilePath != null) {
                        product.setImageUrl(uploadedFilePath);
                        // if (oldImagePath != null && !oldImagePath.equals(uploadedFilePath)) { /* delete old file logic */ }
                    }
                } catch (IOException e) {
                    request.setAttribute("errorMessage", "图片上传失败: " + e.getMessage());
                    request.setAttribute("product", product);
                    request.setAttribute("formAction", "edit");
                    request.getRequestDispatcher("/admin/product_form.jsp").forward(request, response);
                    return;
                }
            } else if (request.getParameter("removeImage") != null && request.getParameter("removeImage").equals("true")) {
                // Logic to remove image if a checkbox or similar is used
                // String oldImagePath = product.getImageUrl();
                product.setImageUrl(null); // Set to null or default image path
                // if (oldImagePath != null) { /* delete old file logic */ }
            }

            if (productDao.updateProduct(product)) {
                request.getSession().setAttribute("message", "商品 \"" + name + "\" 更新成功！");
                response.sendRedirect(request.getContextPath() + "/admin/products");
            } else {
                request.setAttribute("errorMessage", "更新商品失败。");
                request.setAttribute("product", product);
                request.setAttribute("formAction", "edit");
                request.getRequestDispatcher("/admin/product_form.jsp").forward(request, response);
            }
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMessage", "无效的商品ID格式。");
            response.sendRedirect(request.getContextPath() + "/admin/products");
        }
    }

    private void deleteProduct(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int productId = Integer.parseInt(request.getParameter("productId"));
            // Optionally: delete product image from server before deleting product record
            // Product product = productDao.getProductById(productId);
            // if (product != null && product.getImageUrl() != null) { /* delete file logic */ }
            if (productDao.deleteProduct(productId)) {
                request.getSession().setAttribute("message", "商品ID " + productId + " 删除成功！");
            } else {
                request.getSession().setAttribute("errorMessage", "删除商品ID " + productId + " 失败 (可能因为订单关联等)。");
            }
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMessage", "无效的商品ID格式。");
        }
        response.sendRedirect(request.getContextPath() + "/admin/products");
    }

    private void bulkDeleteProducts(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String[] productIdsParam = request.getParameterValues("productIds");
        if (productIdsParam == null || productIdsParam.length == 0) {
            request.getSession().setAttribute("errorMessage", "没有选择任何商品进行删除。");
            response.sendRedirect(request.getContextPath() + "/admin/products");
            return;
        }

        int deletedCount = 0;
        for (String productIdStr : productIdsParam) {
            try {
                int productId = Integer.parseInt(productIdStr);
                // Optionally: delete product image from server
                if (productDao.deleteProduct(productId)) {
                    deletedCount++;
                }
            } catch (NumberFormatException e) {
                System.err.println("Invalid product ID in bulk delete: " + productIdStr);
            }
        }

        if (deletedCount > 0) {
            request.getSession().setAttribute("message", "成功批量删除 " + deletedCount + " 个商品。");
        } else {
            request.getSession().setAttribute("errorMessage", "批量删除商品失败或未选中有效商品。");
        }
        response.sendRedirect(request.getContextPath() + "/admin/products");
    }
}


