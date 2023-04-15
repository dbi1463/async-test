package tw.funymph.async.client;

import static java.lang.Integer.parseInt;
import static java.lang.String.format;
import static java.lang.String.valueOf;
import static java.lang.System.currentTimeMillis;
import static java.lang.System.out;
import static java.net.http.HttpRequest.newBuilder;
import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static java.time.Duration.ofSeconds;
import static java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.rangeClosed;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.util.List;
import java.util.concurrent.Callable;

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
		
		@SuppressWarnings("preview")
		var executor = newVirtualThreadPerTaskExecutor();
		List<Callable<String>> tasks = rangeClosed(1, requests).mapToObj((index) -> {
			Callable<String> task = () -> {
				return sendRequest(method, index, timestamper);
			};
			return task;
		}).collect(toList());
		
		try {
			executor.invokeAll(tasks);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		executor.shutdown();
		return timestamper;
	}

	private static String sendRequest(final String method, final int index, final Timestamper timestamper) {
		var requestId = valueOf(index);
		timestamper.start(requestId);
		var requst = newBuilder(URI.create("http://localhost:8080/%s/%s".formatted(method, requestId))).build();
		try {
			HttpClient.newBuilder()
				.connectTimeout(ofSeconds(10))
				.build()
				.send(requst, ofString());
			timestamper.stop(requestId, true);
		} catch (Exception e) {
			timestamper.stop(requestId, false);
			e.printStackTrace();
		}
		return requestId;
	}
}
