package tw.funymph.async.server;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor;
import static tw.funymph.async.server.Config.wrapWithVirtualThread;
import static tw.funymph.async.server.RequestTracker.track;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SuppressWarnings("preview")
public class HelloWorldWebService {

	private ExecutorService executor = newVirtualThreadPerTaskExecutor();
	private HelloWorldService service = new HelloWorldService();

	@GetMapping("/sync/{requestId}")
	public String hello(@PathVariable("requestId") final String requestId) {
		track(requestId, "HelloWorldWebService::hello");
		return this.service.hello(requestId);
	}

	@GetMapping("/wrapped/{requestId}")
	public CompletableFuture<String> helloWrapped(@PathVariable("requestId") final String requestId) {
		track(requestId, "HelloWorldWebService::wrapped");
		Supplier<String> supplier = () -> {
			track(requestId, "HelloWorldWebService::supplyAsync::hello");
			return this.service.hello(requestId);
		};
		return wrapWithVirtualThread ? supplyAsync(supplier, executor) : supplyAsync(supplier);
	}

	@GetMapping("/async/{requestId}")
	public CompletableFuture<String> helloAsync(@PathVariable("requestId") final String requestId) {
		track(requestId, "HelloWorldWebService::helloAsync");
		return this.service.helloAsync(requestId);
	}
}
