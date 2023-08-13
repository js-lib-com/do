package com.jslib.doupdate;

import java.util.List;

/**
 * Commands parser and executor. A command has a single word name and a list of arguments. Arguments parsing and validation
 * should be performed by command implementation.
 * 
 * @author Iulian Rotaru
 */
public interface ICommand {

	void exec(List<String> args) throws Exception;

}
