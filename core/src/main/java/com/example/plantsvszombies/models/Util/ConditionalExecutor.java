package com.example.plantsvszombies.models.Util;

import com.badlogic.gdx.utils.Array;

import java.util.function.BooleanSupplier;


public class ConditionalExecutor {
    private final Array<ConditionalTask> tasks = new Array<>();
    private final float CLEAR_INTERVAL = 15f; // 每隔15秒清理一次
    private float timeSinceLastClear = 0f;

    public void addTask(BooleanSupplier condition, Runnable task) {
        tasks.add(new ConditionalTask(task, condition));
    }

    public void update(float delta) {
        timeSinceLastClear += delta;
        if (timeSinceLastClear >= CLEAR_INTERVAL) {
            clear();
            timeSinceLastClear = 0f;
        }

        for (ConditionalTask task : tasks) {
            if (task.shouldExecute()) {
                task.execute();
            }
        }
    }

    private void clear() {
        Array.ArrayIterator<ConditionalTask> it = new Array.ArrayIterator<>(tasks);
        while (it.hasNext()) {
            if (!it.next().wasConditionMet) {
                it.remove();
            }
        }
    }

    private static class ConditionalTask {
        private final Runnable task;
        private final BooleanSupplier condition;
        private boolean wasConditionMet = false;

        public ConditionalTask(Runnable task, BooleanSupplier condition) {
            this.task = task;
            this.condition = condition;
        }

        public boolean shouldExecute() {
            boolean isConditionMet = condition.getAsBoolean();
            // 只在条件从false变为true时执行
            boolean shouldExecute = isConditionMet && !wasConditionMet;
            wasConditionMet = isConditionMet;
            return shouldExecute;
        }

        public void execute() {
            task.run();
        }
    }
}
