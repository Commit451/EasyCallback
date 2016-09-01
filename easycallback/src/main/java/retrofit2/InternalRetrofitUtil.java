package retrofit2;

import java.util.concurrent.Executor;

/**
 * Package access to Retrofit things.
 */
public class InternalRetrofitUtil {

    private static Platform platform;

    static Platform getPlatform() {
        if (platform == null) {
            platform = Platform.get();
        }
        return platform;
    }

    public static Executor defaultCallbackExecutor() {
        return getPlatform().defaultCallbackExecutor();
    }
}
