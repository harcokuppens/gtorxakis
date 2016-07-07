package gui.dialogs;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComboBox;
import javax.swing.JLabel;

public class RunDialog extends Dialog{
	
	public RunDialog(){
		init();
	}

	public void init(){
		setTitle("Run project");
		setSizeByScreenSize(0.33,0.5);
		centerOnScreen();
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0.5;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		
		JLabel specLabel = new JLabel("Specification");
		this.add(specLabel, gbc);
		gbc.gridx++;
		
		JComboBox specDefinitions = new JComboBox();
		specDefinitions.addItem("Spec1");
		specDefinitions.addItem("Spec2");
		this.add(specDefinitions, gbc);
		gbc.gridy++;
		gbc.gridx = 0;
		
		JLabel adapLabel = new JLabel("Adapter");
		this.add(adapLabel, gbc);
		gbc.gridx++;
		
		JComboBox adapDefinitions = new JComboBox();
		adapDefinitions.addItem("Adap1");
		adapDefinitions.addItem("Adap2");
		this.add(adapDefinitions, gbc);
		gbc.gridy++;
		gbc.gridx = 0;
	}
}
