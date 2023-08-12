package com.jubiman.customdatalib.mob;

import com.jubiman.customdatalib.api.CustomData;
import com.jubiman.customdatalib.api.CustomDataHandler;
import com.jubiman.customdatalib.api.CustomDataRegistry;
import necesse.engine.GameEventListener;
import necesse.engine.GameEvents;
import necesse.engine.GameLog;
import necesse.engine.events.ServerStartEvent;
import necesse.engine.events.ServerStopEvent;
import necesse.engine.network.server.Server;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Where mods can register their custom mobs
 */
public class CustomMobRegistry extends CustomDataRegistry<Integer> {
	/**
	 * The main instance of the CustomMob registry, which resides on the server environment
	 */
	public static final CustomMobRegistry INSTANCE = new CustomMobRegistry();
	private static final HashMap<String, Constructor<? extends CustomMobsHandler<?>>> classHashMap = new HashMap<>();

	static {
		GameEvents.addListener(ServerStopEvent.class, new GameEventListener<ServerStopEvent>() {
			@Override
			public void onEvent(ServerStopEvent e) {
				INSTANCE.stopAll();
				INSTANCE.registry.clear();
			}
		});
		GameEvents.addListener(ServerStartEvent.class, new GameEventListener<ServerStartEvent>() {
			@Override
			public void onEvent(ServerStartEvent e) {
				System.out.println("Registering all CustomMobsHandler classes: " + Arrays.toString(classHashMap.keySet().toArray()));
				INSTANCE.registerAll();
				GameLog.debug.println("Registered all CustomMobsHandler classes");
			}
		});
	}

	/**
	 * Register a new CustomMobsHandler class. Used by individual mods
	 *
	 * @param identifier the name of the class
	 * @param clazz      a reference to the class
	 */
	public static void registerClass(String identifier, Class<? extends CustomMobsHandler<?>> clazz) {
		try {
			GameLog.debug.println("Registering CustomMobsHandler class: " + identifier);
			Constructor<? extends CustomMobsHandler<?>> ctor = clazz.getDeclaredConstructor();
			classHashMap.put(identifier, ctor);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Server ticks all registered CustomMobs. Please do not call this function as it's called every tick when Necesse's server ticks.
	 *
	 * @param server the server to tick from
	 */
	public static void serverTickAll(Server server) {
		for (CustomDataHandler<Integer, ? extends CustomData> cms : INSTANCE.registry.values()) {
			((CustomMobsHandler<? extends CustomMob>) cms).serverTick(server);
		}
	}

	/**
	 * Saves mob data from all registered CustomMob classes
	 *
	 * @param save the SaveData to save to
	 * @param id   the id of the mob to save
	 */
	public void saveAll(SaveData save, Object id) {
		for (CustomDataHandler<Integer, ? extends CustomData> cms : registry.values()) {
			SaveData customMobSave = new SaveData("CustomMobData");
			cms.save(customMobSave, (Integer) id);
			save.addSaveData(customMobSave);
		}
	}

	/**
	 * Loads all registered CustomMobs, is called before the rest of the mob is loaded
	 *
	 * @param data the LoadData to load from
	 */
	public void loadAllEnter(LoadData data, int id) {
		LoadData mobData = data.getFirstLoadDataByName("CustomMobData");
		for (Map.Entry<String, CustomDataHandler<Integer, ? extends CustomData>> entry : registry.entrySet())
			((CustomMobsHandler<? extends CustomMob>) entry.getValue()).loadEnter(mobData.getFirstLoadDataByName(entry.getKey()), id);
	}

	/**
	 * Loads all registered CustomMobs, is called after the rest of the mob is loaded
	 *
	 * @param data the LoadData to load from
	 */
	public void loadAllExit(LoadData data, int id) {
		LoadData mobData = data.getFirstLoadDataByName("CustomMobData");
		for (Map.Entry<String, CustomDataHandler<Integer, ? extends CustomData>> entry : registry.entrySet())
			((CustomMobsHandler<? extends CustomMob>) entry.getValue()).loadExit(mobData.getFirstLoadDataByName(entry.getKey()), id);
	}

	/**
	 * Creates a new Handler instance for every declared class
	 */
	private void registerAll() {
		for (Map.Entry<String, Constructor<? extends CustomMobsHandler<?>>> entry : classHashMap.entrySet()) {
			try {
				GameLog.debug.println("Instantiating a new object for " + entry.getValue().getDeclaringClass().getName());
				CustomMobsHandler<?> instance = entry.getValue().newInstance();
				register(entry.getKey(), instance);
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
				System.err.println("Failed to instantiate a new object for " + entry.getValue().getDeclaringClass().getName());
				e.printStackTrace();
			}
		}
	}

	public void stopAll() {
		for (CustomDataHandler<Integer, ? extends CustomData> cms : registry.values())
			((CustomMobsHandler<? extends CustomMob>) cms).stop();
	}

	public void removeUser(int id) {
		for (CustomDataHandler<Integer, ? extends CustomData> cms : registry.values())
			((CustomMobsHandler<? extends CustomMob>) cms).remove(id);
	}
}
