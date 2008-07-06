package uk.ac.shef.dcs.smdStudio.domain;

import java.util.List;

/**
 * A representation of the model object '<em><b>SMD Type</b></em>'.
 * 
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link uk.ac.shef.dcs.smdStudio.Leaf#getName <em>Name</em>}</li>
 * <li>{@link uk.ac.shef.dcs.smdStudio.Leaf#getDescription <em>Description</em>}</li>
 * </ul>
 * </p>
 * 
 * @author <a href=mailto:acp07ad@sheffield.ac.uk>Aria Dorri</a>
 */
public class AbstractModule {

	private String name;

	private String description;

	private List<String> tests;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the tests
	 */
	public List<String> getTests() {
		return tests;
	}

	/**
	 * @param tests
	 *            the tests to set
	 */
	public void setTests(List<String> tests) {
		this.tests = tests;
	}
}
