package tw.funymph.async.server;

import static java.lang.Thread.sleep;
import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static tw.funymph.async.server.RequestTracker.track;

import java.util.concurrent.CompletableFuture;

public class SleepyRepository {

	int count(final String requestId) {
		track(requestId, "SleepyRepository::count");
		asleep(1000, requestId);
		return 1;
	}

	void save(final String requestId) {
		track(requestId, "SleepyRepository::save");
		asleep(1000, requestId);
	}

	CompletableFuture<Integer> countAsync(final String requestId) {
		return supplyAsync(() -> {
			track(requestId, "SleepyRepository::supplyAsync::count");
			return this.count(requestId);
		});
	}

	CompletableFuture<Void> saveAsync(final String requestId) {
		return runAsync(() -> {
			track(requestId, "SleepyRepository::supplyAsync::save");
			this.save(requestId);
		});
	}

	private void asleep(final long milliseconds, final String requestId) {
		try {
			track(requestId, "SleepyRepository::sleep");
			sleep(milliseconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
