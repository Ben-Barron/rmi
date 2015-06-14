package com.benbarron.rmi.common;

import java.io.Serializable;
import java.util.function.BiConsumer;

@FunctionalInterface
public interface Task<T, C> extends BiConsumer<T, C>, Serializable { }
