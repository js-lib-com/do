package com.jslib.dospi;

@FunctionalInterface
public interface IProgress<T> {

	void onProgress(T value);

}
