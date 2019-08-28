package tw.funymph.async.client;

import static java.lang.System.currentTimeMillis;

public class SingleRequestResult {

	private String requestId;
	private long startedTime;
	private long finishedTime;
	private boolean succeeded;

	public static SingleRequestResult start(final String requestId) {
		SingleRequestResult result = new SingleRequestResult();
		result.startedTime = currentTimeMillis();
		result.requestId = requestId;
		return result;
	}

	public SingleRequestResult finish(boolean succeeded) {
		this.finishedTime = currentTimeMillis();
		this.succeeded = succeeded;
		return this;
	}

	public String getRequestId() {
		return this.requestId;
	}

	public long getStartedTime() {
		return this.startedTime;
	}

	public long getFinishedTime() {
		return this.finishedTime;
	}

	public long getElapsedTime() {
		return this.finishedTime - this.startedTime;
	}

	public boolean isSucceeded() {
		return this.succeeded;
	}

	public String toString() {
		return String.format("%s, %d, %d, %d, %b", this.requestId, this.startedTime, this.finishedTime, this.getElapsedTime(), this.succeeded);
	}
}
