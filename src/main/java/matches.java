import java.util.concurrent.*;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

class TimeLimitedCodeBlock {

    public static void runWithTimeout(final Runnable runnable, long timeout, TimeUnit timeUnit) throws Exception {
        runWithTimeout(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                runnable.run();
                return null;
            }
        }, timeout, timeUnit);
    }

    public static <T> T runWithTimeout(Callable<T> callable, long timeout, TimeUnit timeUnit) throws Exception {
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        final Future<T> future = executor.submit(callable);
        executor.shutdown();
        try {
            return future.get(timeout, timeUnit);
        }
        catch (TimeoutException e) {
            future.cancel(true);
            throw e;
        }
        catch (ExecutionException e) {
            Throwable t = e.getCause();
            if (t instanceof Error) {
                throw (Error) t;
            } else if (t instanceof Exception) {
                throw (Exception) t;
            } else {
                throw new IllegalStateException(t);
            }
        }
    }

}

public class matches {
    public static boolean matches(String text, String regex) {
        return Pattern.compile(regex).matcher(text).matches();
    }

    public static boolean matchesNoEx(String text, String regex) {
        final boolean[] ret = {false};
        try {
            TimeLimitedCodeBlock.runWithTimeout(new Runnable() {
                @Override
                public void run() {
                    try {
                        ret[0] = Pattern.compile(regex).matcher(text).matches();
                    }
                    catch (PatternSyntaxException e) {
                        ret[0] = false;
                    }
                }
            }, 5, TimeUnit.SECONDS);
        } catch (Exception e) { // Probably TimeoutException
            return false;
        }
        return ret[0];
    }

    public static void main(String[] args) {
        System.out.println(matches("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaac",
                "(a+a+a+a+a+a+)+b"));
        System.out.println(matchesNoEx("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab",
                "(a+a+a+a+a+a+)+b"));
    }
}
