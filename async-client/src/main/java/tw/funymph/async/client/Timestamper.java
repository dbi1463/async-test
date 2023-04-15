package tw.funymph.async.client;

import static java.lang.String.format;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class Timestamper {

	private Map<String, SingleRequestResult> results = new ConcurrentHashMap<>();

	public SingleRequestResult start(final String requestId) {
		SingleRequestResult result = SingleRequestResult.start(requestId);
		this.results.put(requestId, result);
		return result;
	}

	public SingleRequestResult stop(final String requestId, final boolean succeeded) {
		return this.results.computeIfPresent(requestId, (key, value) -> value.finish(succeeded));
	}

	public long min() {
		return this.elapsedTimes().min().orElse(0);
	}

	public long max() {
		return this.elapsedTimes().max().orElse(0);
	}

	public double mean() {
		return this.elapsedTimes().average().orElse(0);
	}

	public long median() {
		int fiftieth = results.size() / 2;
		return this.elapsedTimes()
			.sorted()
			.skip(fiftieth)
			.findFirst().orElse(0);
	}

	public double ninetiethMean() {
		int ninetieth = results.size() - (results.size() / 10);
		return this.elapsedTimes()
			.sorted()
			.limit(ninetieth)
			.average()
			.orElse(0);
	}

	public double ninetyFifthMean() {
		int ninetyFifth = results.size() - (results.size() / 20);
		return this.elapsedTimes()
			.sorted()
			.limit(ninetyFifth)
			.average()
			.orElse(0);
	}

	public int succeeded() {
		return this.results.values()
			.stream()
			.mapToInt((result) -> (result.isSucceeded() ? 1 : 0))
			.sum();
	}

	public int failed() {
		return this.results.size() - this.succeeded();
	}

	public void save(File file) {
		StringBuilder builder = new StringBuilder();
		long base = this.first();
		builder.append("ID, start, start-shifted, finish, finish-shifted, elapsed, succeeded\n");
		this.sortedResults().forEach((value) -> {
			builder.append(format("%s, %d, %d, %d, %d, %d, %b\n",
				value.getRequestId(),
				value.getStartedTime(),
				value.getStartedTime() - base,
				value.getFinishedTime(),
				value.getFinishedTime() - base,
				value.getElapsedTime(),
				value.isSucceeded()
			));
		});
		try (Writer writer = new OutputStreamWriter(new FileOutputStream(file))) {
			writer.write(builder.toString());
		} catch(Exception e) {
			
		}
	}

	private Stream<SingleRequestResult> sortedResults() {
		return this.results.values()
			.stream()
			.sorted((a, b) -> (int) (a.getStartedTime() - b.getStartedTime()));
	}

	private long first() {
		return this.startedTimes().min().orElse(0);
	}

	private LongStream startedTimes() {
		return this.results.values()
			.stream()
			.filter(result -> result.isSucceeded())
			.mapToLong(result -> result.getStartedTime());
	}

	private LongStream elapsedTimes() {
		return this.results.values()
			.stream()
			.filter(result -> result.isSucceeded())
			.mapToLong(result -> result.getElapsedTime());
	}
}
