package flex.webtier.server.j2ee.jsp;
public class FlashVarTagImpl extends flex.bootstrap.BootstrapTag
   implements flex.webtier.server.j2ee.jsp.FlashVarTag
{
flex.webtier.server.j2ee.jsp.FlashVarTag flashVarTag = (flex.webtier.server.j2ee.jsp.FlashVarTag) tag;
public void setName(java.lang.String p0)
{
  Thread thread = Thread.currentThread();
  ClassLoader oldLoader = thread.getContextClassLoader();
  thread.setContextClassLoader(bootstrap);
  try {
    flashVarTag.setName(p0);
  } finally {
    thread.setContextClassLoader(oldLoader);
  }
}
public void setValue(java.lang.Object p0)
{
  Thread thread = Thread.currentThread();
  ClassLoader oldLoader = thread.getContextClassLoader();
  thread.setContextClassLoader(bootstrap);
  try {
    flashVarTag.setValue(p0);
  } finally {
    thread.setContextClassLoader(oldLoader);
  }
}
}
