/**
 * 
 */
package uk.ac.shef.dcs.smdStudio.gui.view;

import javax.swing.JPanel;

import uk.ac.shef.dcs.smdStudio.domain.AbstractModule;

/**
 * @author Aria
 * 
 */
@SuppressWarnings("serial")
public class ModulePanel extends JPanel {

	private AbstractModule module;

	/**
	 * @return the module
	 */
	public AbstractModule getModule() {
		return module;
	}

	/**
	 * @param module
	 *            the module to set
	 */
	public void setModule(AbstractModule module) {
		this.module = module;
	}

	/**
	 * 
	 */
	public void delete() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 
	 */
	public void reBuild() {
		// TODO Auto-generated method stub
		
	}
}
