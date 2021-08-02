package com.jslib.docli;

import com.jslib.docore.IProgress;

import js.util.Params;

public class Progress implements IProgress<Integer> {
	private final Console console;
	private final int size;

	private int total;

	public Progress(Console console, int size) {
		Params.notZero(size, "Size");
		this.console = console;
		this.size = size;
		this.total = 0;
	}

	@Override
	public void onProgress(Integer value) {
		if (value == -1) {
			console.println("\u001B[100D100.00%%");
			return;
		}
		total += value;
		double percent = Math.min(100.0, 100.0 * total / size);
		console.print("\u001B[100D%03.2f%%", percent);
	}
}
