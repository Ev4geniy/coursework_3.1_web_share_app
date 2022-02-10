<%--
  Created by IntelliJ IDEA.
  User: evgeny
  Date: 02.02.2022
  Time: 22:58
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" session="false" %>
<html>
<head>
    <title>Login</title>
</head>
<body>
    <div>
        <h1>Login</h1>
        <form action="./login" method="post">
            <div class="container">
                <label for="username">Username : </label>
                <input type="text" placeholder="Enter Username" name="username" id ="username" required>
                <br>
                <label for="password">Password : </label>
                <input type="text" placeholder="Enter Password" name="password" id="password" required>
                <button type="submit">Login</button>
            </div>
        </form>
        <h2>Not Registered?  <a href="./register">Register</a> </h2>
    </div>
</body>
</html>
