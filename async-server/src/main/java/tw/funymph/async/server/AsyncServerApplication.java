package tw.funymph.async.server;

import static org.springframework.boot.SpringApplication.run;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
//@EnableAsync
public class AsyncServerApplication {

	public static void main(String[] args) {
		run(AsyncServerApplication.class, args);
	}
}
