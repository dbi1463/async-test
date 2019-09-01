package tw.funymph.async.client;

import static java.lang.Integer.parseInt;
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
		int requests = parseInt(args[1]);
		long totalStarted = currentTimeMillis();
		String method = args[0];
		Timestamper timestamper = profile(method, requests);
		dumpResult(requests, totalStarted, method, timestamper);
	}

	private static void dumpResult(int requests, long totalStarted, String method, Timestamper timestamper) {
		long totalElapsed = currentTimeMillis() - totalStarted;
		out.println(format("use total %d ms to send %d requests for %s", totalElapsed, requests, method));
		out.println(format("%d succeeded, %d failed", timestamper.succeeded(), timestamper.failed()));
		out.println(format("min request time: %d", timestamper.min()));
		out.println(format("max request time: %d", timestamper.max()));
		out.println(format("medium request time: %d", timestamper.median()));
		out.println(format("average request time: %f", timestamper.mean()));
		out.println(format("90 average request time: %f", timestamper.ninetiethMean()));
		out.println(format("95 average request time: %f", timestamper.ninetyFifthMean()));
		File file = new File(String.format("target/%s-%d-%d.csv", method, requests, currentTimeMillis()));
		timestamper.save(file);
	}

	private static Timestamper profile(final String method, final int requests) {
		out.println(format("profile %s", method));
		Timestamper timestamper = new Timestamper();
		OkHttpClient client = new OkHttpClient();
		ExecutorService executor = new ScheduledThreadPoolExecutor(requests);
		allOf(rangeClosed(1, requests).mapToObj((index) -> {
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
		executor.shutdown();
		return timestamper;
	}
}
