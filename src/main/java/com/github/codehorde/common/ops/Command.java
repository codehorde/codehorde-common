package com.github.codehorde.common.ops;

/**
 * A command that does not throw any checked exceptions.
 * <p>
 * Created by baomingfeng at 2017-06-15 15:40:20
 */
public interface Command extends ExceptionalCommand<RuntimeException> {
    // convenience typedef
}