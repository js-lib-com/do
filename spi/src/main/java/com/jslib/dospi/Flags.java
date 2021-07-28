package com.jslib.dospi;

/**
 * Flags for parameters definition. See {@link IParameters#define(String, String, Flags, Class, Object...)} and overloads.
 * 
 * @author Iulian Rotaru
 */
public enum Flags {
	/** Mandatory parameter value. Empty user input is re-prompted. */
	MANDATORY,
	/** Optional parameter value can be null. */
	OPTIONAL,
	/** Parameter is command line argument only. Do not prompt user if value is not provided on command line. */
	ARGUMENT
}
