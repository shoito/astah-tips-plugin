package com.github.astah.tips;


import javax.swing.JDialog;
import javax.swing.JOptionPane;

import com.change_vision.jude.api.inf.ui.IPluginActionDelegate;
import com.change_vision.jude.api.inf.ui.IWindow;

public class TipsAction implements IPluginActionDelegate {

	public Object run(IWindow window) throws UnExpectedException {
	    try {
	    	JDialog dialog = new JDialog(window.getParent());
	    	dialog.setTitle("Tips");
	    	dialog.setModal(true);
	    	dialog.getContentPane().add(new TipsPanel());
//	    	dialog.pack();
	    	dialog.setSize(640, 480);
	    	dialog.setLocationRelativeTo(window.getParent());
	    	dialog.setVisible(true);
	    } catch (Exception e) {
	    	JOptionPane.showMessageDialog(window.getParent(), "Unexpected error has occurred.", "Alert", JOptionPane.ERROR_MESSAGE); 
	        throw new UnExpectedException();
	    }
	    return null;
	}


}
