package gui.dialogs;

import io.file.FileType;

import java.io.FilenameFilter;

import io.file.FileTypeAssociation;

import java.awt.Frame;
import java.awt.Component;
import java.awt.Dialog;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import core.Session;

import java.awt.FileDialog;
import java.awt.FlowLayout;

public abstract class AdvFileChooser extends FileDialog {
	private PropertyChangeListener autochangeExtensionListener;
	
	protected FileTypeAssociation association;
	
	public AdvFileChooser(Frame parent, final FileTypeAssociation association, String title, int mode) {
		super(parent, title, mode);
		init(association);
	}

	public AdvFileChooser(Dialog parent, final FileTypeAssociation association, String title, int mode) {
		super(parent, title, mode);
		init(association);
	}

	private void init(FileTypeAssociation association) {
		this.association = association;
		FileType[] types = association.fileTypes;
		ArrayList<String> fileExtensions = new ArrayList<String>();

		for(FileType ft : types){
			for(String fileExtension : ft.getFileExtensions()){
				fileExtensions.add(fileExtension);
			}
		}
		
		this.setFile(this.getFileExtensionPattern(fileExtensions));
		this.setFilenameFilter(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				return AdvFileChooser.this.association.matches(filename);
			}
		});
	}
	
	private String getFileExtensionPattern(ArrayList<String> fileExtensions){
		StringBuilder sb = new StringBuilder();
		for(String fe : fileExtensions){
			sb.append("*."+fe+";");
		}
		return sb.toString();
	}

	public String getFullFile() {
		if(super.getFile() != null) {
			return super.getDirectory() + super.getFile();
		}
		return null;
	}

protected class WaitingDialog extends gui.dialogs.Dialog{
		
		private final WaitingDialog dialog;
		private String title, message;
		private JProgressBar progressBar;
		
		
		public WaitingDialog(String title, String message){
			super(Session.getSession().getWindow());
			dialog = this;
			this.title = title;
			this.message = message;
			start();
		}
		
		public void start(){
			setTitle(title);
			this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			init();
			pack();
			centerOnScreen();
			setResizable(false);
			setVisible(true);
		}
		
		private void init(){
			JPanel dummy = new JPanel();
			BoxLayout bl = new BoxLayout(dummy, BoxLayout.Y_AXIS);
			
			dummy.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
			dummy.setLayout(bl);
			
			JLabel m = new JLabel(message);
			m.setAlignmentX(Component.CENTER_ALIGNMENT);
			dummy.add(m);
			
			progressBar = new JProgressBar();
			progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);
			progressBar.setIndeterminate(true);
			
			dummy.add(progressBar);
			this.add(dummy);
		}
	}
	
}
