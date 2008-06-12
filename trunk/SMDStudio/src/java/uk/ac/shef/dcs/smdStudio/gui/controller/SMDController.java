package uk.ac.shef.dcs.smdStudio.gui.controller;

import uk.ac.shef.dcs.smdStudio.domain.AbstractModule;

/**
 * @author Aria
 * 
 */
public interface SMDController {

	/**
	 * @param module
	 */
	void createNewSMD(AbstractModule module);


	/**
	 * @param module
	 */
	void updateSMD(AbstractModule module);


	/**
	 * @return
	 */
	AbstractModule getSMD();

}
