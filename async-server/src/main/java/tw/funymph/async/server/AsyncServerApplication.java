package tw.funymph.async.server;

import static java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor;
import static java.util.concurrent.ForkJoinPool.commonPool;
import static org.springframework.boot.SpringApplication.run;
import static tw.funymph.async.server.Config.virtualThreadedAsyncTask;
import static tw.funymph.async.server.Config.virtualThreadedTomcatHandler;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatProtocolHandlerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;

@SpringBootApplication
@SuppressWarnings("preview")
public class AsyncServerApplication {

	public static void main(String[] args) {
		run(AsyncServerApplication.class, args);
	}

	@Bean(TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME)
	AsyncTaskExecutor asyncTaskExecutor() {
		return new TaskExecutorAdapter(virtualThreadedAsyncTask ? newVirtualThreadPerTaskExecutor() : commonPool());
	}

	@Bean
	TomcatProtocolHandlerCustomizer<?> protocolHandlerVirtualThreadExecutorCustomizer() {
		return virtualThreadedTomcatHandler ? protocolHandler -> {
			protocolHandler.setExecutor(newVirtualThreadPerTaskExecutor());
		} : protocolHandler -> {};
	}
}
