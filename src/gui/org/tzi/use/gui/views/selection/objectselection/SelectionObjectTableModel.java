package org.tzi.use.gui.views.selection.objectselection;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.tzi.use.gui.views.selection.SelectionComparator;
import org.tzi.use.gui.views.selection.TableModel;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.sys.MObject;
import org.tzi.use.uml.sys.MSystem;
import org.tzi.use.uml.sys.MSystemState;
import org.tzi.use.util.Log;

/** 
 * SelectionObjectTableModel is derived from TableModel. Data of table are 
 * stored in a specific TableModel, i.e. in the SelectionObjectTableModel.
 * @author   Jun Zhang 
 * @author   Jie Xu
 */
public class SelectionObjectTableModel extends TableModel {
	public MClass fClass;
	MSystem fSystem;
	
	/**
	 * Constructor for SelectionObjectTableModel.
	 */
	public SelectionObjectTableModel( List fAttributes, List fValues, MClass fClass, MSystem system) {
		super(fAttributes, fValues);
		this.fClass = fClass;
		this.fSystem = system;
		this.setColumnName("object name", "select");
		update();
	}

	/**
	 * Method getColumnClass determine the default renderer/ editor for
	 * each cell. 
	 */
	public Class getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}

	/**
	 * Method setValueAt takes the user input.
	 * 
	 * @see javax.swing.table.TableModel#setValueAt(Object, int, int)
	 */
	public void setValueAt(Object value, int row, int col) {
		Log.trace(this, "row = " + row + ", col = " + col + ", value = "
				+ value);
		if(value.toString().equals("true")){
			fValues.set(row, Boolean.valueOf(true));
		}
		else{
			fValues.set(row, Boolean.valueOf(false));
		}
		fireTableCellUpdated(row, col);
	}

	public void setSelected(MClass mc){
		fClass = mc;
		update();
	}
	
	/**
	 * Method update updates the data of Table. 
	 */
	public void update() {

		if (fClass != null) {
			fAttributes.clear();
			fValues.clear();
			
			MClass cls = fClass;
			MSystemState state = fSystem.state();
			Set allObjects = state.allObjects();
			Iterator objectIterator = allObjects.iterator();
			int i = 0;

			SelectionComparator sort = new SelectionComparator();
			TreeSet sortedNodes = new TreeSet(sort);
			 while (objectIterator.hasNext()) {
				MObject obj = (MObject) objectIterator.next();
				if (cls.name().equals(obj.cls().name())) {
					 sortedNodes.add(obj);
					i++;
				}
			}
			Iterator it = sortedNodes.iterator();
			while(it.hasNext()){
				MObject obj = (MObject) it.next();
				 fAttributes.add(obj.name());
				 fValues.add(Boolean.valueOf(false));
			}
		}
		fireTableDataChanged();
	}

}
