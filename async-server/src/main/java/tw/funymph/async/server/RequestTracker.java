package tw.funymph.async.server;

import static java.lang.String.format;
import static java.lang.System.out;
import static java.lang.Thread.currentThread;

public class RequestTracker {

	public static void track(final String requestId, final String action) {
		out.println(format("[%s] do %s at %s", requestId, action, currentThread().getName()));
	}
}
