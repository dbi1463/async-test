package tw.funymph.async.server;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static tw.funymph.async.server.RequestTracker.track;

import java.util.concurrent.CompletableFuture;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldWebService {

	HelloWorldService service = new HelloWorldService();

	@GetMapping("/sync/{requestId}")
	public String hello(@PathVariable("requestId") final String requestId) {
		track(requestId, "HelloWorldWebService::hello");
		return this.service.hello(requestId);
	}

	@GetMapping("/wrapped/{requestId}")
	public CompletableFuture<String> helloWrapped(@PathVariable("requestId") final String requestId) {
		track(requestId, "HelloWorldWebService::wrapped");
		return supplyAsync(() -> {
			track(requestId, "HelloWorldWebService::supplyAsync::hello");
			return this.service.hello(requestId);
		});
	}

	@GetMapping("/async/{requestId}")
	public CompletableFuture<String> helloAsync(@PathVariable("requestId") final String requestId) {
		track(requestId, "HelloWorldWebService::helloAsync");
		return this.service.helloAsync(requestId);
	}
}
