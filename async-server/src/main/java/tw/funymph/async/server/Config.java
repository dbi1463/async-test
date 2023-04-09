package tw.funymph.async.server;

public interface Config {

	static final boolean virtualThreadedTomcatHandler = true;
	static final boolean virtualThreadedAsyncTask = false;
	static final boolean wrapWithVirtualThread = false;
	static final boolean virtualThreadedRepository = false;
}
