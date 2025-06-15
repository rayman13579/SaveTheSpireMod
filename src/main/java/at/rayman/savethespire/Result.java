package at.rayman.savethespire;

import java.util.function.Consumer;

public class Result {

    private final String value;

    private final boolean success;

    private Result(String value, boolean success) {
        this.value = value;
        this.success = success;
    }

    public static Result error(String value) {
        return new Result(value, false);
    }

    public static Result success(String value) {
        return new Result(value, true);
    }

    public Result success(Consumer<Result> consumer) {
        if (success) consumer.accept(this);
        return this;
    }

    public Result success(Runnable runnable) {
        if (success) runnable.run();
        return this;
    }

    public Result error(Consumer<Result> consumer) {
        if (!success) consumer.accept(this);
        return this;
    }

    public Result then(Consumer<Result> consumer) {
        consumer.accept(this);
        return this;
    }

    public Result then(Runnable runnable) {
        runnable.run();
        return this;
    }

    public boolean isSuccess() {
        return success;
    }

    @Override
    public String toString() {
        return value;
    }

}
