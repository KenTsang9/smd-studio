package uk.ac.shef.dcs.smdStudio.domain;

/**
 * A representation of the model object '<em><b>Leaf</b></em>'.
 * 
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link uk.ac.shef.dcs.smdStudio.Leaf#getCost <em>Cost</em>}</li>
 * </ul>
 * </p>
 * 
 * @author <a href=mailto:acp07ad@sheffield.ac.uk>Aria Dorri</a>
 */
public class Leaf  extends AbstractModule{
	
	private double cost;

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
