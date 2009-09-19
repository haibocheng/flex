package flex.webtier.server.j2ee.jsp;
public class MxmlTagImpl extends flex.bootstrap.BootstrapBodyTag
   implements flex.webtier.server.j2ee.jsp.MxmlTag
{
flex.webtier.server.j2ee.jsp.MxmlTag mxmlTag = (flex.webtier.server.j2ee.jsp.MxmlTag) tag;
public void setFlashVar(java.lang.Object p0, java.lang.Object p1)
 throws javax.servlet.jsp.JspTagException{
  Thread thread = Thread.currentThread();
  ClassLoader oldLoader = thread.getContextClassLoader();
  thread.setContextClassLoader(bootstrap);
  try {
    mxmlTag.setFlashVar(p0, p1);
  } finally {
    thread.setContextClassLoader(oldLoader);
  }
}
public void setId(java.lang.String p0)
{
  Thread thread = Thread.currentThread();
  ClassLoader oldLoader = thread.getContextClassLoader();
  thread.setContextClassLoader(bootstrap);
  try {
    mxmlTag.setId(p0);
  } finally {
    thread.setContextClassLoader(oldLoader);
  }
}
public void setUseHistoryManagement(java.lang.String p0)
 throws javax.servlet.jsp.JspTagException{
  Thread thread = Thread.currentThread();
  ClassLoader oldLoader = thread.getContextClassLoader();
  thread.setContextClassLoader(bootstrap);
  try {
    mxmlTag.setUseHistoryManagement(p0);
  } finally {
    thread.setContextClassLoader(oldLoader);
  }
}
public void setUsePlayerDetection(java.lang.String p0)
 throws javax.servlet.jsp.JspTagException{
  Thread thread = Thread.currentThread();
  ClassLoader oldLoader = thread.getContextClassLoader();
  thread.setContextClassLoader(bootstrap);
  try {
    mxmlTag.setUsePlayerDetection(p0);
  } finally {
    thread.setContextClassLoader(oldLoader);
  }
}
public void setUseExpressInstall(java.lang.String p0)
 throws javax.servlet.jsp.JspTagException{
  Thread thread = Thread.currentThread();
  ClassLoader oldLoader = thread.getContextClassLoader();
  thread.setContextClassLoader(bootstrap);
  try {
    mxmlTag.setUseExpressInstall(p0);
  } finally {
    thread.setContextClassLoader(oldLoader);
  }
}
public void setAlternateContentPage(java.lang.String p0)
 throws javax.servlet.jsp.JspTagException{
  Thread thread = Thread.currentThread();
  ClassLoader oldLoader = thread.getContextClassLoader();
  thread.setContextClassLoader(bootstrap);
  try {
    mxmlTag.setAlternateContentPage(p0);
  } finally {
    thread.setContextClassLoader(oldLoader);
  }
}
public void setSource(java.lang.String p0)
{
  Thread thread = Thread.currentThread();
  ClassLoader oldLoader = thread.getContextClassLoader();
  thread.setContextClassLoader(bootstrap);
  try {
    mxmlTag.setSource(p0);
  } finally {
    thread.setContextClassLoader(oldLoader);
  }
}
public void setWidth(java.lang.String p0)
 throws javax.servlet.jsp.JspTagException{
  Thread thread = Thread.currentThread();
  ClassLoader oldLoader = thread.getContextClassLoader();
  thread.setContextClassLoader(bootstrap);
  try {
    mxmlTag.setWidth(p0);
  } finally {
    thread.setContextClassLoader(oldLoader);
  }
}
public void setHeight(java.lang.String p0)
 throws javax.servlet.jsp.JspTagException{
  Thread thread = Thread.currentThread();
  ClassLoader oldLoader = thread.getContextClassLoader();
  thread.setContextClassLoader(bootstrap);
  try {
    mxmlTag.setHeight(p0);
  } finally {
    thread.setContextClassLoader(oldLoader);
  }
}
}
