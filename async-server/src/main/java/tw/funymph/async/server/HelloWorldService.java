package tw.funymph.async.server;

import java.util.concurrent.CompletableFuture;

public class HelloWorldService {

	SleepyRepository repository = new SleepyRepository();

	public String hello() {
		int count = this.repository.count();
		this.doSomething(count);
		this.repository.save();
		return "Hello World";
	}

	public CompletableFuture<String> helloAsync() {
		return this.repository
			.countAsync()
			.thenApply((count) -> {
				this.doSomething(count);
				return null;
			})
			.thenCompose((o) -> this.repository.saveAsync())
			.thenCompose((o) -> CompletableFuture.completedFuture("Hello World"));
	}

	private void doSomething(int count) {
		
	}
}
