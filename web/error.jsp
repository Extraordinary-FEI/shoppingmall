<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>应用程序错误</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; background-color: #f4f4f4; color: #333; }
        .container { background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1); }
        h1 { color: #d9534f; }
        p { font-size: 1.1em; }
        a { color: #007bff; text-decoration: none; }
        a:hover { text-decoration: underline; }
    </style>
</head>
<body>
<div class="container">
    <h1>应用程序错误</h1>
    <p>抱歉，处理您的请求时遇到了一个意外错误。</p>
    <p>错误详情：${requestScope["jakarta.servlet.error.message"]}</p>
    <p>请尝试返回<a href="javascript:history.back()">上一页</a>或联系管理员。</p>
    <p><small>如果您是管理员，请检查服务器日志以获取更多详细信息。</small></p>
</div>
</body>
</html>
