/*
 Violet - A program for editing UML diagrams.

 Copyright (C) 2007 Cay S. Horstmann (http://horstmann.com)
 Alexandre de Pellegrin (http://alexdp.free.fr);

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package uk.ac.sheffield.dcs.smdStudio.framework.gui;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.beans.PropertyEditorSupport;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import uk.ac.sheffield.dcs.smdStudio.framework.diagram.ChoiceList;
import uk.ac.sheffield.dcs.smdStudio.framework.diagram.ChoiceListEditor;
import uk.ac.sheffield.dcs.smdStudio.framework.resources.ResourceBundleConstant;
import uk.ac.sheffield.dcs.smdStudio.framework.util.SerializableEnumeration;

/**
 * A component filled with editors for all editable properties of an object.
 */
public class PropertySheet {
	private static Map<Class<?>, Class<? extends PropertyEditor>> editors;

	private static Set<Class<?>> knownImmutables = new HashSet<Class<?>>();

	/**
	 * Flag indicating that the bean has a minimum of one editable property
	 */
	private boolean isEditable = false;

	private ArrayList<PropertyChangeListener> listeners = new ArrayList<PropertyChangeListener>();

	private JPanel panel;

	static {
		knownImmutables.add(String.class);
		knownImmutables.add(Integer.class);
		knownImmutables.add(Boolean.class);
		knownImmutables.add(Double.class);
	}

	static {
		editors = new HashMap<Class<?>, Class<? extends PropertyEditor>>();
		editors.put(String.class, StringEditor.class);
		editors.put(java.awt.Color.class, ColorEditor.class);
		editors.put(ChoiceList.class, ChoiceListEditor.class);
	}

	// private static final int MAX_TEXT_LENGTH = 15;

	/*
	 * Formats text for the button that pops up a custom editor.
	 * 
	 * @param text the property value as text @return the text to put on the
	 * button private static String buttonText(String text) { if (text == null
	 * || text.equals("")) return " "; if (text.length() > MAX_TEXT_LENGTH)
	 * return text.substring(0, MAX_TEXT_LENGTH) + "..."; return text; }
	 */

	private static boolean isKnownImmutable(Class<?> type) {
		if (type.isPrimitive())
			return true;
		if (knownImmutables.contains(type))
			return true;
		if (SerializableEnumeration.class.isAssignableFrom(type))
			return true;
		return false;
	}

	/**
	 * Constructs a property sheet that shows the editable properties of a given
	 * object.
	 * 
	 * @param object
	 *            the object whose properties are being edited
	 * @param parent
	 *            the parent component
	 * 
	 * 
	 *            EDITED changed from formlayout to gridbag layout
	 */
	public PropertySheet(Object bean) {
		panel = new JPanel();
		try {
			BeanInfo info = Introspector.getBeanInfo(bean.getClass());
			PropertyDescriptor[] descriptors = (PropertyDescriptor[]) info
					.getPropertyDescriptors().clone();
			Arrays.sort(descriptors, new Comparator<PropertyDescriptor>() {
				public int compare(PropertyDescriptor d1, PropertyDescriptor d2) {
					Integer p1 = (Integer) d1.getValue("priority");
					Integer p2 = (Integer) d2.getValue("priority");
					if (p1 == null && p2 == null)
						return 0;
					if (p1 == null)
						return 1;
					if (p2 == null)
						return -1;
					return p1.intValue() - p2.intValue();
				}
			});

			GridBagLayout gridbag = new GridBagLayout();
			GridBagConstraints c = new GridBagConstraints();

			panel.setLayout(gridbag);

			c.fill = GridBagConstraints.BOTH;
			c.weightx = 1.0;

			// panel.setLayout(new FormLayout());

			ResourceBundle rs = ResourceBundle.getBundle(
					ResourceBundleConstant.NODE_AND_EDGE_STRINGS, Locale
							.getDefault());
			for (int i = 0; i < descriptors.length; i++) {
				if (descriptors[i].isHidden())
					continue;
				PropertyEditor editor = getEditor(bean, descriptors[i]);
				if (editor != null) {
					// Try to extract title from resource bundle
					String title = descriptors[i].getName();
					try {
						String translatedTitle = rs.getString(title
								.toLowerCase());
						if (translatedTitle != null)
							title = translatedTitle;
					} catch (MissingResourceException e) {
						// Nothing to do
					}

					// Upper case the first character
					title = title.substring(0, Math.min(1, title.length()))
							.toUpperCase()
							+ title.substring(Math.min(1, title.length()),
									title.length());

					JLabel label = new JLabel(title + ":");
					label.setFont(label.getFont().deriveFont(Font.BOLD));
					c.insets = new Insets(10, 0, 0, 0);
					c.gridwidth = GridBagConstraints.REMAINDER;
					gridbag.setConstraints(label, c);
					panel.add(label);
					c.insets = new Insets(0, 0, 0, 0);
					c.gridwidth = GridBagConstraints.REMAINDER;
					Component editorComponent = getEditorComponent(editor);
					gridbag.setConstraints(editorComponent, c);
					panel.add(editorComponent);
					this.isEditable = true;
				}
			}
		} catch (IntrospectionException exception) {
			exception.printStackTrace();
		}
	}

	/**
	 * Adds a property change listener to the list of listeners.
	 * 
	 * @param listener
	 *            the listener to add
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
	}

	/**
	 * Notifies all listeners of a state change.
	 * 
	 * @param event
	 *            the event to propagate
	 */
	private void firePropertyStateChanged(PropertyChangeEvent event) {
		synchronized (listeners) {
			for (PropertyChangeListener listener : listeners)
				listener.propertyChange(event);
		}
	}

	public JComponent getComponent() {
		return panel;
	}

	/**
	 * Gets the property editor for a given property, and wires it so that it
	 * updates the given object.
	 * 
	 * @param bean
	 *            the object whose properties are being edited
	 * @param descriptor
	 *            the descriptor of the property to be edited
	 * @return a property editor that edits the property with the given
	 *         descriptor and updates the given object
	 */
	public PropertyEditor getEditor(final Object bean,
			final PropertyDescriptor descriptor) {
		try {
			Method getter = descriptor.getReadMethod();
			if (getter == null)
				return null;
			final Method setter = descriptor.getWriteMethod();
			if (setter == null)
				return null;
			Class<?> type = descriptor.getPropertyType();
			final PropertyEditor editor;
			Class<?> editorClass = descriptor.getPropertyEditorClass();
			if (editorClass == null && editors.containsKey(type))
				editorClass = (Class<?>) editors.get(type);
			if (editorClass != null)
				editor = (PropertyEditor) editorClass.newInstance();
			else
				editor = PropertyEditorManager.findEditor(type);
			if (editor == null)
				return null;

			Object value = getter.invoke(bean);
			editor.setValue(value);

			if (!isKnownImmutable(type)) {
				try {
					value = value.getClass().getMethod("clone").invoke(value);
				} catch (Throwable t) {
					// we tried
				}
			}
			final Object oldValue = value;
			editor.addPropertyChangeListener(new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent event) {
					try {
						Object newValue = editor.getValue();
						setter.invoke(bean, newValue);
						firePropertyStateChanged(new PropertyChangeEvent(bean,
								descriptor.getName(), oldValue, newValue));
					} catch (IllegalAccessException exception) {
						exception.printStackTrace();
					} catch (InvocationTargetException exception) {
						exception.printStackTrace();
					}
				}
			});
			return editor;
		} catch (InstantiationException exception) {
			exception.printStackTrace();
			return null;
		} catch (IllegalAccessException exception) {
			exception.printStackTrace();
			return null;
		} catch (InvocationTargetException exception) {
			exception.printStackTrace();
			return null;
		}
	}

	/**
	 * Wraps a property editor into a component.
	 * 
	 * @param editor
	 *            the editor to wrap
	 * @return a button (if there is a custom editor), combo box (if the editor
	 *         has tags), or text field (otherwise)
	 */
	private Component getEditorComponent(final PropertyEditor editor) {
		String[] tags = editor.getTags();
		String text = editor.getAsText();
		if (editor.supportsCustomEditor()) {
			return editor.getCustomEditor();
			/*
			 * // Make a button that pops up the custom editor final JButton
			 * button = new JButton(); // if the editor is paintable, have it
			 * paint an icon if (editor.isPaintable()) { button.setIcon(new
			 * Icon() { public int getIconWidth() { return WIDTH - 8; } public
			 * int getIconHeight() { return HEIGHT - 8; }
			 * 
			 * public void paintIcon(Component c, Graphics g, int x, int y) {
			 * g.translate(x, y); Rectangle r = new Rectangle(0, 0,
			 * getIconWidth(), getIconHeight()); Color oldColor = g.getColor();
			 * g.setColor(Color.BLACK); editor.paintValue(g, r);
			 * g.setColor(oldColor); g.translate(-x, -y); } }); } else
			 * button.setText(buttonText(text)); // pop up custom editor when
			 * button is clicked button.addActionListener(new ActionListener() {
			 * public void actionPerformed(ActionEvent event) { final Component
			 * customEditor = editor.getCustomEditor();
			 * 
			 * JOptionPane.showMessageDialog(parent, customEditor); // This
			 * should really be showInternalMessageDialog, // but then you get
			 * awful focus behavior with JDK 5.0 // (i.e. the property sheet
			 * retains focus). In // particular, the color dialog never works.
			 * 
			 * if (editor.isPaintable()) button.repaint(); else
			 * button.setText(buttonText(editor.getAsText())); } }); return
			 * button;
			 */
		} else if (tags != null) {
			// make a combo box that shows all tags
			final JComboBox comboBox = new JComboBox(tags);
			comboBox.setSelectedItem(text);
			comboBox.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent event) {
					if (event.getStateChange() == ItemEvent.SELECTED)
						editor.setAsText((String) comboBox.getSelectedItem());
				}
			});
			return comboBox;
		} else {
			final JTextField textField = new JTextField(text, 10);
			textField.getDocument().addDocumentListener(new DocumentListener() {
				public void changedUpdate(DocumentEvent e) {
				}

				public void insertUpdate(DocumentEvent e) {
					try {
						editor.setAsText(textField.getText());
					} catch (IllegalArgumentException exception) {
					}
				}

				public void removeUpdate(DocumentEvent e) {
					try {
						editor.setAsText(textField.getText());
					} catch (IllegalArgumentException exception) {
					}
				}
			});
			return textField;
		}
	}

	/**
	 * @return true if the bean has a minimum of one editable property
	 */
	public boolean isEditable() {
		return this.isEditable;
	}

	/**
	 * Adds a property change listener to the list of listeners.
	 * 
	 * @param listener
	 *            the listener to add
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}

	// workaround for Web Start bug
	public static class StringEditor extends PropertyEditorSupport {
		public String getAsText() {
			return (String) getValue();
		}

		public void setAsText(String s) {
			setValue(s);
		}
	}
}
