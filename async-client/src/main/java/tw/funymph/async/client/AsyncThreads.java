package tw.funymph.async.client;

import static java.lang.String.format;
import static java.lang.System.out;
import static java.lang.Thread.currentThread;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.supplyAsync;

import java.util.concurrent.CompletableFuture;

public class AsyncThreads {

	private static void whichThead(final String action) {
		out.println(format("%s run at %s", action, currentThread().getName()));
	}

	private static String world() {
		whichThead("world");
		return "world";
	}

	private static CompletableFuture<String> hello(final String who) {
		whichThead("hello");
		return completedFuture(format("hello %s", who));
	}

	private static void say(final String something) {
		whichThead("say");
		out.println(something);
	}

	public static void main(String[] args) {
		whichThead("main");
		supplyAsync(AsyncThreads::world)
			.thenComposeAsync(AsyncThreads::hello)
			.thenAccept(AsyncThreads::say);
	}
}
