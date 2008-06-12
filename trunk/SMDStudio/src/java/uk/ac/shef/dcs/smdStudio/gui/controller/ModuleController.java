/**
 * 
 */
package uk.ac.shef.dcs.smdStudio.gui.controller;

import uk.ac.shef.dcs.smdStudio.domain.AbstractModule;

/**
 * @author Aria
 * 
 */
public class ModuleController implements DeletableSMDController {

	private ProjectController projectController;

	private AbstractModule module;

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.shef.dcs.smdStudio.gui.controller.DeletableSMDController#deleteSMD()
	 */
	@Override
	public void deleteSMD() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.shef.dcs.smdStudio.gui.controller.SMDController#createNewSMD(uk.ac.shef.dcs.smdStudio.domain.AbstractModule)
	 */
	@Override
	public void createNewSMD(AbstractModule module) {
		this.module = module;
		projectController.addModule(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.shef.dcs.smdStudio.gui.controller.SMDController#getSMD()
	 */
	@Override
	public AbstractModule getSMD() {
		return module;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.shef.dcs.smdStudio.gui.controller.SMDController#updateSMD(uk.ac.shef.dcs.smdStudio.domain.AbstractModule)
	 */
	@Override
	public void updateSMD(AbstractModule module) {
		// TODO Auto-generated method stub

	}

}
