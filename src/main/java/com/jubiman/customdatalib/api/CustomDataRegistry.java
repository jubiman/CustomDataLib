package com.jubiman.customdatalib.api;

import java.util.HashMap;

/**
 * Registry for CustomDataHandlers
 * @param <I> The identifier type of the CustomDataHandler (i.e. Long aka auth for players)
 */
public abstract class CustomDataRegistry<I> {
	protected final HashMap<String, CustomDataHandler<I, ? extends CustomData>> registry = new HashMap<>();

	/**
	 * Get CustomPlayers instance in registry
	 * @param identifier the identifier of the CustomDataHandler
	 * @return CustomPlayers instance registered with the identifier
	 */
	public CustomDataHandler<I, ? extends CustomData> get(String identifier) {
		// Should be fine as long as developers don't try to randomly access wrong registries
		// or add wrong types to the registry
		return registry.get(identifier);
	}

	/**
	 * Register a new CustomPlayers instance
	 * @param identifier the name of the class (used to return the instance)
	 * @param customPlayersHandlerInstance a new instance of the class
	 */
	public void register(String identifier, CustomDataHandler<I, ? extends CustomData> customPlayersHandlerInstance) {
		registry.put(identifier, customPlayersHandlerInstance);
	}
}
