package de.arthurpicht.barnacle.mapping;

import java.util.HashSet;
import java.util.Set;

/**
 * Repräsentiert die Menge aller Entitäten.
 * Implementiert als Singleton.
 * 
 * @author Arthur Picht, (c) 2007 - 2023
 *
 */
public class EntityCollection {

	private final Set<Entity> entitySet;

	public EntityCollection() {
		this.entitySet = new HashSet<>();
	}

	public void addEntity(Entity entity) {
		this.entitySet.add(entity);
	}
	
	public Set<Entity> getEntities() {
		return this.entitySet;
	}
	
	public Entity getEntityByTableName(String tableName) {
		for (Entity entity : this.entitySet) {
			if (entity.getTableName().equals(tableName)) {
				return entity;
			}
		}
		return null;
	}
	
	public String debugOut() {
		StringBuilder string = new StringBuilder("EntityCollection");
		for (Entity entity : this.entitySet) {
			string.append("\n").append(entity.toString());
		}
		
		return string.toString();
	}
}
