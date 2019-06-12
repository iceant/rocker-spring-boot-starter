# rocker-spring-boot-starter
spring boot starter for rocker template

## Usage

### Configuration
```properties
spring.rocker.enabled=true
spring.rocker.reloading=true
spring.rocker.templateResolverOrder=214748213637
spring.rocker.prefix="classpath:/templates/"
spring.rocker.suffix=".rocker.html"
spring.rocker.contentType="text/html;charset=utf-8"
spring.rocker.expose-request-attribute=false
spring.rocker.allow-request-override=false
spring.rocker.expose-session-attributes=false
spring.rocker.allow-session-override=false
spring.rocker.expose-spring-macro-helpers=true
```

### layout.rocker.html
> - path: `<maven_project>/src/main/resources/templates/layout/layout.rocker.html`

```html
@import java.util.*
@import rocker.*
@args(RockerContent header, RockerContent script, RockerBody body)

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <link rel="stylesheet" href='@$r.path("/static/deps/bootstrap-4.1.3-dist/css/bootstrap.min.css")'/>
        <link rel="stylesheet" href='@$r.path("/static/deps/font-awesome/css/font-awesome.css")'>
        <link rel="stylesheet" href='@$r.path("/static/css/style.css")'>
        @header
    </head>
    <body class="white-bg">
        @body
        <script src='@$r.path("/static/deps/jquery/jquery-3.4.1.min.js")'></script>
        <script src='@$r.path("/static/deps/popper.js/popper-1.14.3.min.js")'></script>
        <script src='@$r.path("/static/deps/bootstrap-4.1.3-dist/js/bootstrap.min.js")'></script>
        <script src='@$r.path("/static/js/app.js")'></script>
        @script
    </body>
</html>
```

### login.rocker.html
> - path: `<maven_project>/src/main/resources/templates/login.rocker.html`

``` html
@import java.util.*
@import rocker.*

@args()

@header => {
<title>Login</title>
}

@script => {
<script>
    alert("I'm login page!");
</script>
}

@$r.template("layout/layout", header, script)->{
    body!
}
```

### Result
```html

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<link rel="stylesheet" href='/static/deps/bootstrap-4.1.3-dist/css/bootstrap.min.css'/>
<link rel="stylesheet" href='/static/deps/font-awesome/css/font-awesome.css'>
<link rel="stylesheet" href='/static/css/style.css'>
<title>Login</title>

</head>
<body class="white-bg">
 body!

<script src='/static/deps/jquery/jquery-3.4.1.min.js'></script>
<script src='/static/deps/popper.js/popper-1.14.3.min.js'></script>
<script src='/static/deps/bootstrap-4.1.3-dist/js/bootstrap.min.js'></script>
<script src='/static/js/app.js'></script>
<script>
alert("I'm login page!");
</script>

</body>
</html>
```

### rocker.$r.java util
#### Access Spring `ApplicationContext`
```java
$r.getApplicationContext();
```

#### Access Spring Beans
```java
$r.bean("beanName");
$r.bean(/*bean class*/);
```

#### Access Spring boot `application.properties`
```java
$r.prop("name");
$r.prop("name", "defaultValue");
```

#### Access Spring `messages.properties`
```java
$r.msg("code");
$r.msg("code", param1, param2, ...);
```

#### Access `HttpServletRequest`
```java
$r.request();
```

#### Access `HttpServletRequest` parameters
```java
$r.paramString("name", "defaultValue");
$r.paramString("name");

$r.paramInt("name", 0);
$r.paramInt("name"); /* return null Integer if not exist*/

$r.paramFloat("name", 0.0f);
$r.paramFloat("name"); /*return null Float if not exist*/

$r.paramLong("name", 0);
$r.paramLong("name");/*return null Long if not exist*/

$r.paramDouble("name", 0.0d);
$r.paramDouble("name"); /*return null Double if not exist*/

$r.paramDate("name", "yyyy-MM-dd HH:mm:ss");
$r.paramDate("name", "MM/dd/yyyy");
$r.paramDate("name", "yyyy-MM-dd", new Date());
```

#### Access object in `HttpSevletRequest` attribute
````java
$r.attr("name");
$r.attr("name", defaultValue);
````

#### Access object field use 'getXXX' method
````java
$r.field("user.username"); /*user stored in request.attribute, this will invoke user.getUsername()*/
$r.field("user", "username"); /*user stored in request.attribute, this will invoke user.getUsername()*/
````


#### Get context path
```html
<img src='$r.path("/static/images/logo.png")'/>
```

#### Access CSRF token
```html
<head>
    <meta name="_csrf" content="$r.csrf()"/>
    <meta name="_csrf_header" content="$r.csrfTokenHeaderName()"/>
</head>
<body>
    <form method="post">
        <input type="hidden" name="$r.csrfTokenParameterName()" value="$r.csrf()">
    </form>
    
    <script>
    $(function () {
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        $(document).ajaxSend(function(e, xhr, options) {
            xhr.setRequestHeader(header, token);
        });
    });
</script>
</body>
```

#### Invoke Rocker template
```html
@import java.util.*
@import rocker.*

@args()

@header => {
}

@script => {
}

// dynamic invoke rocker template
@$r.template("layout/layout", header, script)->{
    body!
}
```