package uk.ac.sheffield.dcs.smdStudio.framework.resources;

import java.util.Locale;
import java.util.ResourceBundle;

import uk.ac.sheffield.dcs.smdStudio.framework.diagram.ExportableAsXML;

public class XMLResourceBoundle {

	private static final ResourceBundle RS = ResourceBundle.getBundle(
			ResourceBundleConstant.XML_STRINGS, Locale.getDefault());

	private String element;

	public XMLResourceBoundle(Class<? extends ExportableAsXML> clazz) {
		this.element = clazz.getSimpleName().toLowerCase() + ".";
	}

	public String getElementName(String name) {
		return RS.getString(element + name);
	}

	public String getName(String name) {
		return RS.getString(name);
	}

}
