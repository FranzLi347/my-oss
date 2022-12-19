package io.github.franzli347.foss.utils.asyncUtils;

import io.github.franzli347.foss.utils.BeanUtils;

import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 异步任务管理器
 *
 * @author FranzLi
 */
public class AsyncTaskManager {
    /**
     * 单例模式
     */

    private static AsyncTaskManager me = new AsyncTaskManager();
    /**
     * 操作延迟10毫秒
     */
    private final int OPERATE_DELAY_TIME = 10;
    /**
     * 异步操作任务调度线程池
     */
    private final ScheduledExecutorService executor = BeanUtils.getBean("scheduledExecutorService");

    public static AsyncTaskManager me() {
        return me;
    }

    /**
     * 执行任务
     *
     * @param task 任务
     */
    public void execute(TimerTask task) {
        executor.schedule(task, OPERATE_DELAY_TIME, TimeUnit.MILLISECONDS);
    }

    /**
     * 停止任务线程池
     */
    public void shutdown() {
        Threads.shutdownAndAwaitTermination(executor);
    }
}
