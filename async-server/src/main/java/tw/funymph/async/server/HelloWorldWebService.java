package tw.funymph.async.server;

import static java.lang.String.format;
import static java.lang.Thread.currentThread;

import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldWebService {

	HelloWorldService service = new HelloWorldService();

	@GetMapping("/sync")
	public String hello() {
		System.out.println(format("sync handled by thread %s", currentThread().getName()));
		return this.service.hello();
	}

//	@Async
	@GetMapping("/wrapped")
	public CompletableFuture<String> helloWrapped() {
		System.out.println(format("wrapped handled by thread %s", currentThread().getName()));
		return CompletableFuture.supplyAsync(() -> {
			System.out.println(format("wrapped respond by thread %s", currentThread().getName()));
			return this.service.hello();
		});
	}

//	@Async
	@GetMapping("/async")
	public CompletableFuture<String> helloAsync() {
		System.out.println(format("async handled by thread %s", currentThread().getName()));
		return this.service.helloAsync();
	}
}
