package kr.hhplus.be.server.interfaces.scheduler;

public class SchedulerContext {
    private static final ThreadLocal<String> uuidHolder = new ThreadLocal<>();

    public static void setUuid(String uuid) {
        uuidHolder.set(uuid);
    }

    public static String getUuid() {
        return uuidHolder.get();
    }

    public static void clear() {
        uuidHolder.remove();
    }
}
