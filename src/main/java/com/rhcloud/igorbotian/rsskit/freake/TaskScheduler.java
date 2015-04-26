package com.rhcloud.igorbotian.rsskit.freake;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
class TaskScheduler {

    public void executeEveryDayAt(int hourUTC, Runnable task) {
        if(hourUTC < 0 || hourUTC > 23) {
            throw new IllegalArgumentException("Hour value should be in range between 0 and 24");
        }

        Objects.requireNonNull(task);

        Calendar execTime = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

        if(execTime.get(Calendar.HOUR_OF_DAY) >= hourUTC) {
            execTime.roll(1, Calendar.DAY_OF_MONTH);
        }

        execTime.set(Calendar.MILLISECOND, 0);
        execTime.set(Calendar.SECOND, 0);
        execTime.set(Calendar.MINUTE, 0);
        execTime.set(Calendar.HOUR_OF_DAY, hourUTC);

        executeRepeatedTask(task, execTime.getTime(), 1, TimeUnit.DAYS);
    }

    public void executeEveryNMinutes(int n, Runnable task) {
        if(n <= 0) {
            throw new IllegalArgumentException("N minutes should have a positive value");
        }

        Calendar execTime = Calendar.getInstance();
        execTime.roll(Calendar.MINUTE, n);

        executeRepeatedTask(Objects.requireNonNull(task), execTime.getTime(), n, TimeUnit.MINUTES);
    }

    private void executeRepeatedTask(final Runnable task, final Date firstTime, final int period, final TimeUnit unit) {
        assert task != null;
        assert period > 0;
        assert unit != null;

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        task.run();
                    }
                }, firstTime, TimeUnit.MILLISECONDS.convert(period, unit));
            }
        });
        thread.run();
    }
}
