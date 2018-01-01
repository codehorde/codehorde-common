package com.github.codehorde.common.ops;

/**
 * An interface that captures a unit of work.
 * <p>
 * Created by baomingfeng at 2017-06-15 15:40:20
 *
 * @param <E> The type of exception that the command throws.
 */
public interface ExceptionalCommand<E extends Exception> {

    /**
     * Performs a unit of work, possibly throwing {@code E} in the process.
     *
     * @throws E if there was a problem performing the work
     */
    void execute() throws E;
}