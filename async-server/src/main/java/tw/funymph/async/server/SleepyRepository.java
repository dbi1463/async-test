package tw.funymph.async.server;

import static java.lang.Thread.sleep;
import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.concurrent.CompletableFuture.supplyAsync;

import java.util.concurrent.CompletableFuture;

public class SleepyRepository {

	int count() {
		asleep(100);
		return 1;
	}

	void save() {
		asleep(100);
	}

	CompletableFuture<Integer> countAsync() {
		return supplyAsync(this::count);		
	}

	CompletableFuture<Void> saveAsync() {
		return runAsync(this::save);
	}

	private void asleep(long milliseconds) {
		try {
			sleep(milliseconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
