package com.jubiman.customdatalib.util;

import necesse.engine.GameLog;

public class Logger {
	/**
	 * Logs an info message to the game log.
	 * @param message The message to log.
	 * @param args The arguments to format the message with.
	 */
	public static void info(String message, Object... args) {
		GameLog.out.format("[CustomDataLib] %s\n", String.format(message, args));
	}

	/**
	 * Logs an error message to the game log.
	 * @param message The message to log.
	 * @param args The arguments to format the message with.
	 */
	public static void error(String message, Object... args) {
		GameLog.err.format("[CustomDataLib] %s\n", String.format(message, args));
	}

	/**
	 * Logs a warning message to the game log.
	 * @param message The message to log.
	 * @param args The arguments to format the message with.
	 */
	public static void warn(String message, Object... args) {
		GameLog.warn.format("[CustomDataLib] %s\n", String.format(message, args));
	}

	/**
	 * Logs a debug message to the game log.
	 * @param message The message to log.
	 * @param args The arguments to format the message with.
	 */
	public static void debug(String message, Object... args) {
		GameLog.debug.format("[CustomDataLib] %s\n", String.format(message, args));
	}

	/**
	 * Logs a message to the game log that is only saved to the log file.
	 * @param message The message to log.
	 * @param args The arguments to format the message with.
	 */
	public static void file(String message, Object... args) {
		GameLog.file.format("[CustomDataLib] %s\n", String.format(message, args));
	}
}

