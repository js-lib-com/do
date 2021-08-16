package com.jslib.doupdate;

/**
 * Write ahead log with commands to be executed by updater. Current implemented commands are listed below. All commands
 * involving file system uses absolute paths. For details see command classes description - all classes implementing
 * {@link ICommand}.
 * 
 * <pre>
 * delete ${file}
 * clean ${directory}
 * clean ${directory} exclude ${file-patterns}
 * clean ${directory} exclude ${files-list}
 * move ${source-file} to ${target-dir}
 * unzip ${zip-file} to ${target-dir}
 * unzip ${zip-file} to ${target-dir} with properties merge
 * </pre>
 * 
 * @author Iulian Rotaru
 */
public interface ICommandsLog {

	void add(String command, Object... args);

}
