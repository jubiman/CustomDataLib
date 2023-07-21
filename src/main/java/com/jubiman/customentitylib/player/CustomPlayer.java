package com.jubiman.customentitylib.player;

import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

/**
 * The instance of each individual entity containing custom data
 */
public abstract class CustomPlayer {
		/**
		 * The auth of the player, used to access this object in the CustomPlayers registry
		 */
		protected final long auth;

		/**
		 * Creates new CustomEntity
		 * @param auth the auth of the player
		 */
		public CustomPlayer(long auth) {
			this.auth = auth;
		}

		/**
		 * Creates new save component
		 * @return a SaveData component with name of the player's auth
		 */
		// TODO: fix this now (probs let the mod only create a GND map and make the library add the save data)
		// TODO: this way we get a following structure: Entity -> CustomEntityData -> ModName -> save data
		// TODO: we might have to add the uniqueID to the save data as well, or we use the new ID that might be created already
		public SaveData generateEntitySave() {
			return new SaveData(String.valueOf(auth));
		}

		/**
		 * Save the player's data
		 * @param save save parent object to add to
		 */
		public abstract void addSaveData(SaveData save);

		/**
		 * Load player's data from saved data. Gets called before the rest of the player is loaded.
		 * @param data the data to load
		 */
		public abstract void loadEnter(LoadData data);

		/**
		 * Load player's data from saved data. Gets called before the rest of the player is loaded.
		 * @param data the data to load
		 */
		public abstract void loadExit(LoadData data);
}
