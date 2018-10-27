<%@ page language="java"  contentType="text/html; charset=UTF-8" %>

<html>
<body style="text-align: center">
    <h2>Tomcat 1</h2>
    <h2>Hello World!</h2>
    <h5>springMVC上传文件</h5>
    <form name="form1" action="/manage/product/upload.do" method="post" enctype="multipart/form-data">
        <input type="file" name="upload_file" />
        <input type="submit" value="springMVC上传文件" />
    </form>

    <h5>富文本图片上传文件</h5>
    <form name="form2" action="/manage/product/richtext_img_upload.do" method="post" enctype="multipart/form-data">
        <input type="file" name="upload_file" />
        <input type="submit" value="富文本图片上传文件" />
    </form>

</body>
</html>
