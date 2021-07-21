package com.jslib.dospi;

@FunctionalInterface
public interface IParameterCallback<T> {
	T onParameter(String name, T value);
}
