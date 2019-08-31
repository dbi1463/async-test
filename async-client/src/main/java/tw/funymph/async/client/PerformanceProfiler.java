package tw.funymph.async.client;

import static java.lang.String.format;
import static java.lang.String.valueOf;
import static java.lang.System.currentTimeMillis;
import static java.lang.System.out;
import static java.util.concurrent.CompletableFuture.allOf;
import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.stream.IntStream.rangeClosed;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PerformanceProfiler {

	public static void main(String[] args) {
		if (args.length != 2) {
			out.println("please provide method and times");
			return;
		}
		int times = Integer.parseInt(args[1]);
		profile(args[0], times);
	}

	private static void profile(String method, int times) {
		out.println(format("profile %s", method));
		OkHttpClient client = new OkHttpClient();
		Timestamper timestamper = new Timestamper();
		long totalStarted = currentTimeMillis();
		ExecutorService executor = new ScheduledThreadPoolExecutor(times);
		allOf(rangeClosed(1, times).mapToObj((index) -> {
			final String requestId = valueOf(index);
			return runAsync(() -> {
				timestamper.start(requestId);
				Request request = new Request.Builder().url(format("http://localhost:8080/%s/%s", method, requestId)).build();
				try (Response response = client.newCall(request).execute()) {
					timestamper.stop(requestId, true);
				} catch(Exception e) {
					timestamper.stop(requestId, false);
				}
			}, executor);
		}).toArray(CompletableFuture[]::new)).join();
		long totalElapsed = currentTimeMillis() - totalStarted;
		out.println(format("use total %d ms to send %d requests for %s", totalElapsed, times, method));
		out.println(format("%d succeeded, %d failed", timestamper.succeeded(), timestamper.failed()));
		out.println(format("min request time: %d", timestamper.min()));
		out.println(format("max request time: %d", timestamper.max()));
		out.println(format("medium request time: %d", timestamper.median()));
		out.println(format("average request time: %f", timestamper.mean()));
		out.println(format("90 average request time: %f", timestamper.ninetiethMean()));
		out.println(format("95 average request time: %f", timestamper.ninetyFifthMean()));
		executor.shutdown();
		File file = new File(String.format("target/%s %d.csv", method, System.currentTimeMillis()));
		timestamper.save(file);
	}
}
