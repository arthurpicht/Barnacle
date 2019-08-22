package de.arthurpicht.barnacle.mapping;

import java.util.HashSet;
import java.util.Set;

/**
 * Repräsentiert die Menge aller Entitäten.
 * Implementiert als Singleton.
 * 
 * @author Arthur Picht, Arthur Picht GmbH, (c) 2007
 *
 */
public class EntityCollection {

	private static Set<Entity> entitySet;
	
	static {
		entitySet = new HashSet<Entity>();
	}
	
	public static void addEntity(Entity entity) {
		entitySet.add(entity);
	}
	
	public static Set<Entity> getEntities() {
		return entitySet;
	}
	
	public static Entity getEntityByTableName(String tableName) {
		for (Entity entity : entitySet) {
			if (entity.getTableName().equals(tableName)) {
				return entity;
			}
		}
		return null;
	}
	
	public static String debugOut() {
		String string = "EntityCollection";
		for (Entity entity : entitySet) {
			string += "\n" + entity.toString();
		}
		
		return string;
	}
}
