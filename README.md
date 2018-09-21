Hello, welcome to my blog system practice.
--------------

Project Debrief:

#### Frontend:  
>This project was first done using raw html templates and JS some of which from [billvsme/Vmaig_blog](https://github.com/billvsme/vmaig_blog). The original blog system in the link is written in Python and Django.
>
>Currently, there is another version of this blog system ongoing in my [blog-project-fe](https://github.com/Leoluheng/blog-project-fe) repository, which takes the advantage of one of the edge-cutting frameworks _*Vue.js*_ in reuseable & flexible frontend templates.
#### Backend:  
>This project is constructed using JFinal - the opensource MVC *Java* Webdev framework, with its data stored using MySQL, the relational database, which is setup locally using Docker, the awesome containerization tool!!!  
_***P.S. JFinal is based on Spring MVC***_   
Since JFinal is lacking popularity compared to Spring (Sad...), i will look into Spring and migrate the backend at some time.
  
  

#### Miscellaneous Notes:

1. When updating user's avatar, depending on the server you use (in my case it's Jetty) one may highly possibly encouter error if the avatar exceeds server's default maxFormContentSize. It is very easy to exceed the default content size. To fix it, create an xml file under WEB-INF directory and name it as jetty-web.xml (this is for jettry only) and add the following content to the file:

```
// FOR JETTY 6
  <?xml version="1.0"?>    
	<!DOCTYPE Configure PUBLIC "-//Mort Bay Consulting//DTD Configure//EN" "http://jetty.mortbay.org/configure.dtd">
	<Configure id="WebAppContext" class="org.mortbay.jetty.webapp.WebAppContext">    
  	<Set name="maxFormContentSize" type="int">-1</Set>    
    </Configure>  
```  
```           
// FOR JETTY 7
<?xml version="1.0"?>    
  <!DOCTYPE Configure PUBLIC "-//Mort Bay Consulting//DTD Configure//EN" "http://jetty.mortbay.org/configure.dtd">    
  <Configure id="WebAppContext" class="org.eclipse.jetty.webapp.WebAppContext">    
    <Set name="maxFormContentSize" type="int">-1</Set>    
  </Configure> 
```
Alternatively, modify jetty server startup parameter using 
```
-Dorg.mortbay.jetty.Request.maxFormContentSize=9000
```
 or look up more detials googling "jetty/tomcat maxFormContentSize"

# WEB DEVELOPMENT is a whole LOT OF FUN!
