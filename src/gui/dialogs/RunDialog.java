package gui.dialogs;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import model.TextualDefinition;
import util.Environment;
import core.Session;
import io.file.FileType;
import io.file.FileTypeAssociation;
import io.net.SocketIO;

public class RunDialog extends Dialog implements WindowListener{
	
	private RunDialog runDialog;
	private JSpinner portNumber,
					 testNumber;
	private JTextField programField;
	private JComboBox<String> connectDefinitions,
							  modelDefinitions;
	private JComboBox<TorXakisType> torxakisType;
	private SocketIO socketIO;
	private Process process;
	
	public static enum TorXakisType{
		TESTER("TESTER", "TEST"),
		SIMULATOR("SIMULATOR", "SIM"),
		STEPPER("STEPPER", "STEP");
		
		private String cmd, runCMD;
		
		private TorXakisType(String cmd, String runCMD){
			this.cmd = cmd;
			this.runCMD = runCMD;
		}
		
		public String getInitCommand(String model, String connection){
			switch(this){
			case TESTER:
			case SIMULATOR:
				return cmd + " " + model + " " + connection;
			default:
				return cmd + " " + model;
			}
		}
		
		public String getRunCommand(int iterations){
			return runCMD + " " + String.valueOf(iterations);
		}
	}
	
	public RunDialog(){
		runDialog = this;
		init();
	}

	public void init(){
		setTitle("Run project with TorXakis");
		setSizeByScreenSize(0.3, 0.5);
		centerOnScreen();
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		this.setModal(true);
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0.4;
		gbc.weighty = 0.05;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.insets = new Insets(5,5,5,5);
		
		add(this.getConConfigPanel(), gbc);
		gbc.gridy++;
		
		add(this.getProjectConfigPanel(), gbc);
		gbc.gridy++;
		
		add(this.getRunConfigPanel(), gbc);
		gbc.gridy++;

		add(this.getButtonPanel(), gbc);
		gbc.gridy++;
		
		gbc.gridy++;
		gbc.gridx = 0;
		this.pack();
		this.setMinimumSize(new Dimension(300,200));
		this.setPreferredSize(new Dimension(300,200));
		this.setResizable(false);
		this.centerOnScreen();
	}
	
	public JPanel getConConfigPanel(){
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		panel.setBorder(BorderFactory.createTitledBorder("Connection configurations"));
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0.4;
		gbc.weighty = 0.05;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.insets = new Insets(5,5,5,5);
				
		JLabel programLabel = new JLabel("Host");
		panel.add(programLabel, gbc);
		gbc.gridx++;
		gbc.weightx = 0.6;
		
		programField = new JTextField("localhost");
		programField.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(programField, gbc);
		gbc.gridy++;
		gbc.gridx = 0;
		gbc.weightx = 0.4;
		
		JLabel portLabel = new JLabel("Port");
		panel.add(portLabel, gbc);
		gbc.gridx++;
		gbc.weightx = 0.6;
		
		
		portNumber = new JSpinner(new SpinnerNumberModel(7220, 0, 250000, 1));
		JSpinner.NumberEditor editor = new JSpinner.NumberEditor(portNumber, "#"); 
		portNumber.setEditor(editor);
		panel.add(portNumber, gbc);
		gbc.gridy++;
		gbc.gridx = 0;
		gbc.weightx = 0.4;
		return panel;
	}
	
	public JPanel getProjectConfigPanel(){
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		panel.setBorder(BorderFactory.createTitledBorder("Project configurations"));
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0.4;
		gbc.weighty = 0.05;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.insets = new Insets(5,5,5,5);
		
		JLabel specLabel = new JLabel("Model");
		panel.add(specLabel, gbc);
		gbc.gridx++;
		gbc.weightx = 0.6;
		
		modelDefinitions = new JComboBox<String>();
		System.out.println(Session.getSession().getProject().getName());
		for(String s : Session.getSession().getProject().getDefinitionsByTypeDef(TextualDefinition.DefType.MODEL)){
			modelDefinitions.addItem(s);
		}
		panel.add(modelDefinitions, gbc);
		gbc.gridy++;
		gbc.gridx = 0;
		gbc.weightx = 0.4;

		JLabel connectLabel = new JLabel("Connect definition");
		panel.add(connectLabel, gbc);
		gbc.gridx++;
		gbc.weightx = 0.6;
		
		connectDefinitions = new JComboBox<String>();
		for(String s : Session.getSession().getProject().getDefinitionsByTypeDef(TextualDefinition.DefType.CNECTDEF)){
			connectDefinitions.addItem(s);
		}
		panel.add(connectDefinitions, gbc);
		
		return panel;
	}
	
	public JPanel getRunConfigPanel(){
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		panel.setBorder(BorderFactory.createTitledBorder("Run configurations"));
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0.4;
		gbc.weighty = 0.05;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.insets = new Insets(5,5,5,5);
		
		JLabel typeLabel = new JLabel("Run mode");
		panel.add(typeLabel, gbc);
		gbc.gridx++;
		gbc.weightx = 0.6;
		
		
		torxakisType = new JComboBox<TorXakisType>();
		torxakisType.addItem(TorXakisType.TESTER);
		torxakisType.addItem(TorXakisType.SIMULATOR);
		torxakisType.addItem(TorXakisType.STEPPER);
		panel.add(torxakisType, gbc);
		gbc.gridy++;
		gbc.gridx = 0;
		gbc.weightx = 0.4;
		
		JLabel specLabel = new JLabel("Number of steps");
		panel.add(specLabel, gbc);
		gbc.gridx++;
		gbc.weightx = 0.6;
		
		
		testNumber = new JSpinner(new SpinnerNumberModel(1000, 0, null, 1));
		JSpinner.NumberEditor editor = new JSpinner.NumberEditor(testNumber, "#"); 
		testNumber.setEditor(editor);
		panel.add(testNumber, gbc);
		gbc.gridy++;
		gbc.gridx = 0;
		gbc.weightx = 0.4;
		return panel;
	}
	
	private void startTorxakisServer(String pathToTorXakis, int port){
		 try {
		        String ss = null;
		        Runtime obj = null;
		        process = Runtime.getRuntime().exec("cmd.exe /c start java -jar " + pathToTorXakis + "\\Server.jar");
		        BufferedWriter writeer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
//		        writeer.write("txsserver" + port);
		        writeer.write("dir");
		        writeer.flush();
		    } catch (IOException e) {
		        System.out.println("FROM CATCH" + e.toString());
		    }
	}
	
	public JPanel getButtonPanel(){
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton save = new JButton("Run");
		save.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				int port = (int) portNumber.getValue();
				String host = programField.getText();
				String model = String.valueOf(modelDefinitions.getSelectedItem());
				String connection = String.valueOf(connectDefinitions.getSelectedItem());
				int iterations = (int) testNumber.getValue();
				TorXakisType type = (TorXakisType) torxakisType.getSelectedItem();
				startTorxakisServer("C:\\Users\\Tobias\\Desktop", port);
				Session.getSession().getProject().saveAs(Session.TEMP_TXS, FileTypeAssociation.TorXakisExport.getDefaultFileType());
				socketIO = new SocketIO(runDialog, port, host);
				socketIO.startTorXakis(Session.TEMP_TXS, model, connection, iterations, type);
				TorXakisDialog td = new TorXakisDialog(socketIO.getReader());
				Runnable r = new Runnable(){
					@Override
					public void run() {
						td.readLines();
					}
					
				};
				(new Thread(r)).start();
				td.setVisible(true);
			}
		});
		JButton cancel = new JButton("Close");
		cancel.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				destroyCMD();
				runDialog.dispose();
			}
		});
		panel.add(cancel);
		panel.add(save);
		return panel;
	}

	public void destroyCMD(){
		if(process != null && process.isAlive()) process.destroy();
	}
	
	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		destroyCMD();
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
