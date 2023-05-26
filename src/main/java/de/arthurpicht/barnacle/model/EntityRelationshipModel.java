package de.arthurpicht.barnacle.model;

import java.util.ArrayList;
import java.util.List;

public class EntityRelationshipModel {

	private final List<Entity> entityList;

	public EntityRelationshipModel() {
		this.entityList = new ArrayList<>();
	}

	public void addEntity(Entity entity) {
		this.entityList.add(entity);
	}
	
	public List<Entity> getEntities() {
		return this.entityList;
	}
	
	public Entity getEntityByTableName(String tableName) {
		for (Entity entity : this.entityList) {
			if (entity.getTableName().equals(tableName)) {
				return entity;
			}
		}
		return null;
	}
	
	public String debugOut() {
		StringBuilder string = new StringBuilder("EntityCollection");
		for (Entity entity : this.entityList) {
			string.append("\n").append(entity.toString());
		}
		return string.toString();
	}

}
