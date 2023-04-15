package tw.funymph.async.server;

import static java.lang.Thread.sleep;
import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor;
import static tw.funymph.async.server.Config.virtualThreadedRepository;
import static tw.funymph.async.server.RequestTracker.track;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

@SuppressWarnings("preview")
public class SleepyRepository {

	private ExecutorService executor = newVirtualThreadPerTaskExecutor();

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
		Supplier<Integer> supplier = () -> {
			track(requestId, "SleepyRepository::supplyAsync::count");
			return this.count(requestId);
		}; 
		return virtualThreadedRepository ? supplyAsync(supplier, executor) : supplyAsync(supplier);
	}

	CompletableFuture<Void> saveAsync(final String requestId) {
		Runnable runnable = () -> {
			track(requestId, "SleepyRepository::supplyAsync::save");
			this.save(requestId);
		};
		return virtualThreadedRepository ? runAsync(runnable, executor) : runAsync(runnable);
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
