package uk.ac.shef.dcs.smdStudio.domain;

/**
 * A representation of the model object '<em><b>SMD Relation Item</b></em>'.
 * 
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link uk.ac.shef.dcs.smdStudio.RelationItem#getModule <em>module</em>}</li>
 * <li>{@link uk.ac.shef.dcs.smdStudio.RelationItem#isIsEnd <em>end</em>}</li>
 * </ul>
 * </p>
 * 
 * @see uk.ac.sheffield.dcs.smdStudio.domain.uk.ac.shef.dcs.smdStudio.domainPackage#getSmdRelationItem()
 * 
 * @author <a href=mailto:acp07ad@sheffield.ac.uk>Aria Dorri</a>
 * 
 */
public class RelationItem {
	
	private AbstractModule module;

	private boolean end;

	/**
	 * @return the smdType
	 */
	public AbstractModule getModule() {
		return module;
	}

	/**
	 * @param smdType the smdType to set
	 */
	public void setModule(AbstractModule smdType) {
		this.module = smdType;
	}

	/**
	 * @return the end
	 */
	public boolean isEnd() {
		return end;
	}

	/**
	 * @param end the end to set
	 */
	public void setEnd(boolean end) {
		this.end = end;
	}

}
