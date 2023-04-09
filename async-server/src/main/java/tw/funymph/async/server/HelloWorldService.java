package tw.funymph.async.server;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static tw.funymph.async.server.RequestTracker.track;

import java.util.concurrent.CompletableFuture;

public class HelloWorldService {

	private SleepyRepository repository = new SleepyRepository();

	public String hello(final String requestId) {
		track(requestId, "HelloWorldService::hello");
		int count = this.repository.count(requestId);
		this.doSomething(count, requestId);
		this.repository.save(requestId);
		return "Hello World";
	}

	public CompletableFuture<String> helloAsync(final String requestId) {
		track(requestId, "HelloWorldService::helloAsync");
		return this.repository
			.countAsync(requestId)
			.thenApply((count) -> {
				this.doSomething(count, requestId);
				return null;
			})
			.thenCompose((o) -> this.repository.saveAsync(requestId))
			.thenCompose((o) -> completedFuture("Hello World"));
	}

	private void doSomething(final int count, final String requestId) {
		track(requestId, "HelloWorldService::doSomething");
	}
}
