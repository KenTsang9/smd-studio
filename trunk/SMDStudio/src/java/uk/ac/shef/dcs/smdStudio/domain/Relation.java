package uk.ac.shef.dcs.smdStudio.domain;

/**
 * A representation of the model object '<em><b>Smd Relation</b></em>'.
 * 
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link uk.ac.sheffield.dcs.smdStudio.domain.Relation#getFirstItem <em>firstItem</em>}</li>
 * <li>{@link uk.ac.sheffield.dcs.smdStudio.domain.Relation#getSecondItem <em>secondItem</em>}</li>
 * <li>{@link uk.ac.sheffield.dcs.smdStudio.domain.Relation#getCost <em>cost</em>}</li>
 * </ul>
 * </p>
 * 
 * @author <a href=mailto:acp07ad@sheffield.ac.uk>Aria Dorri</a>
 */
public class Relation {
	
	private RelationItem firstItem;
	
	private RelationItem secondItem;
	
	private double cost;

	/**
	 * @return the firstItem
	 */
	public RelationItem getFirstItem() {
		return firstItem;
	}

	/**
	 * @param firstItem the firstItem to set
	 */
	public void setFirstItem(RelationItem firstItem) {
		this.firstItem = firstItem;
	}

	/**
	 * @return the secondItem
	 */
	public RelationItem getSecondItem() {
		return secondItem;
	}

	/**
	 * @param secondItem the secondItem to set
	 */
	public void setSecondItem(RelationItem secondItem) {
		this.secondItem = secondItem;
	}

	/**
	 * @return the cost
	 */
	public double getCost() {
		return cost;
	}

	/**
	 * @param cost the cost to set
	 */
	public void setCost(double cost) {
		this.cost = cost;
	}
}
