package uk.ac.shef.dcs.smdStudio.domain;

import java.util.List;

/**
 * A representation of the model object '<em><b>SMD</b></em>'.
 * 
 * 
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link uk.ac.shef.dcs.smdStudio.Module#getRelations <em>Relations</em>}</li>
 * <li>{@link uk.ac.shef.dcs.smdStudio.Module#getModules <em>Modules</em>}</li>
 * </ul>
 * </p>
 * 
 * @author <a href=mailto:acp07ad@sheffield.ac.uk>Aria Dorri</a>
 */
public class Module extends AbstractModule{

	private List<Relation> relations;

	private List<AbstractModule> modules;

	/**
	 * @return the relations
	 */
	public List<Relation> getRelations() {
		return relations;
	}

	/**
	 * @param relations
	 *            the relations to set
	 */
	public void setRelations(List<Relation> relations) {
		this.relations = relations;
	}

	/**
	 * @return the smdTypes
	 */
	public List<AbstractModule> getModules() {
		return modules;
	}

	/**
	 * @param smdTypes
	 *            the smdTypes to set
	 */
	public void setModules(List<AbstractModule> smdTypes) {
		this.modules = smdTypes;
	}
}
