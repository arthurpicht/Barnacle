package de.arthurpicht.barnacle.model;

import java.util.HashSet;
import java.util.Set;

public class EntityRelationshipModel {

	private final Set<Entity> entitySet;

	public EntityRelationshipModel() {
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
