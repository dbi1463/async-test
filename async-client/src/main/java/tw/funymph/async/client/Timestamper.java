package tw.funymph.async.client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.LongStream;

public class Timestamper {

	private Map<Integer, SingleRequestResult> results = new ConcurrentHashMap<>();

	public SingleRequestResult start(int requestId) {
		SingleRequestResult result = SingleRequestResult.start(requestId);
		this.results.put(requestId, result);
		return result;
	}

	public SingleRequestResult stop(int requestId, boolean succeeded) {
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
		return this.elapsedTimes().sorted().skip(fiftieth).findFirst().orElse(0);
	}

	public double ninetiethMean() {
		int ninetieth = results.size() - (results.size() / 10);
		return this.elapsedTimes().sorted().limit(ninetieth).average().orElse(0);
	}

	public double ninetyFifthMean() {
		int ninetyFifth = results.size() - (results.size() / 20);
		return this.elapsedTimes().sorted().limit(ninetyFifth).average().orElse(0);
	}

	public void save(File file) {
		StringBuilder builder = new StringBuilder();
		long base = this.first();
		builder.append("ID, start, start-shifted, finish, finish-shifted, elapsed, succeeded\n");
		this.results.forEach((key, value) -> {
			builder.append(String.format("%d, %d, %d, %d, %d, %d, %b\n",
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

	private long first() {
		return this.startedTimes().min().orElse(0);
	}

	private LongStream startedTimes() {
		return this.results.values().stream().mapToLong(result -> result.getStartedTime());
	}

	private LongStream elapsedTimes() {
		return this.results.values().stream().mapToLong(result -> result.getElapsedTime());
	}
}
