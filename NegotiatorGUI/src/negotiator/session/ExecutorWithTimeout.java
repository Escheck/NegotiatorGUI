package negotiator.session;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * execute commands within the set timout limits. Compute remaining time for
 * further calls
 * 
 * @author W.Pasman, David Festen
 *
 */
public class ExecutorWithTimeout {

	private long remainingTimeMs;

	public ExecutorWithTimeout(long timeoutms) {
		remainingTimeMs = timeoutms;
	}

	/**
	 * Execute command, within remaining time.
	 * 
	 * @param command
	 * @return result of execute of command
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws TimeoutException
	 */
	public synchronized <V> V execute(Callable<V> command)
			throws InterruptedException, ExecutionException, TimeoutException {

		long start = System.nanoTime();
		V result;

		ScheduledExecutorService executorService = Executors
				.newSingleThreadScheduledExecutor();
		try {
			result = executorService.submit(command).get(remainingTimeMs,
					TimeUnit.MILLISECONDS);
		} finally {
			executorService.shutdownNow();
		}
		long end = System.nanoTime();
		long usedMs = (end - start) / 1000000; // used time in millis

		remainingTimeMs = Math.max(remainingTimeMs - usedMs, 0);

		return result;
	}

	/**
	 * @return remaining time for this executor
	 */
	public long getRemainingTimeMs() {
		return remainingTimeMs;
	}

}