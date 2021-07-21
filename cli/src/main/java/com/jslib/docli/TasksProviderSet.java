package com.jslib.docli;

import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.SortedSet;
import java.util.TreeSet;

import com.jslib.dospi.ITasksProvider;

import js.log.Log;
import js.log.LogFactory;

public class TasksProviderSet implements Iterable<ITasksProvider> {
	private static final Log log = LogFactory.getLog(TasksProviderSet.class);

	private final SortedSet<ITasksProvider> set;

	public TasksProviderSet() {
		log.trace("TasksProviderSet()");

		this.set = new TreeSet<>((pleft, pright) -> pleft.getContextName() == null ? pright.getContextName() == null ? 0 : 1 : -1);
		ServiceLoader.load(ITasksProvider.class).forEach(provider -> {
			set.add(provider);
		});
	}

	@Override
	public Iterator<ITasksProvider> iterator() {
		return set.iterator();
	}
}
