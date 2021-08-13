package com.jslib.docore;

public interface IFactory {
	<T> T getInstance(Class<T> type, Object... args);
}
