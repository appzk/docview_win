<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
	<h1>Docview Server Started!</h1>
	<form name="upload" action="/doc/upload?sid=MTIzNDU2Nzg5MDEyMzQuVUEuMTM0MjA3NjE4NDU4Ni5hODRhY2RlNDg2ZDBkN2Q4NjQwYTBmZWYyMTc4OWM2MQ" method="post" enctype="multipart/form-data">
		Upload document:<br />
		<input type="file" name="file" /><br />
		<input type="submit" value="Submit" /> <input type="reset" value="Reset" />
	</form>
</body>
</html>