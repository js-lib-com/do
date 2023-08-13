package com.jslib.docore;

@FunctionalInterface
public interface IProgress<T> {

	void onProgress(T value);

}
