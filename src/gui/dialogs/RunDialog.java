package gui.dialogs;

import io.file.FileTypeAssociation;
import io.net.SocketIO;

import java.awt.BorderLayout;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import model.TextualDefinition;
import util.Environment;
import core.Session;
import core.SessionSettings;

public class RunDialog extends Dialog implements WindowListener{
	
	private RunDialog runDialog;
	private JSpinner portNumber,
					 testNumber;
	private JTextField programField,
					   pathField;
	private JComboBox<String> connectDefinitions,
							  modelDefinitions;
	private JComboBox<TorXakisType> torxakisType;
	private JPanel westPanel;
	
	private TorXakisPanel torxakisPanel;
	private SocketIO socketIO;
	private Process process;
	private SessionSettings sessionSettings;
	
	private boolean stopped = false;
	
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
	
	public RunDialog(SessionSettings settings){
		this.sessionSettings = settings;
		runDialog = this;
		init();
	}

	public void init(){
		setTitle("Run project with TorXakis");
		setLayout(new BorderLayout());
		westPanel = new JPanel();
		westPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0.4;
		gbc.weighty = 0.05;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.insets = new Insets(5,5,5,5);
		
		westPanel.add(this.getConConfigPanel(), gbc);
		gbc.gridy++;
		
		westPanel.add(this.getProjectConfigPanel(), gbc);
		gbc.gridy++;
		
		westPanel.add(this.getRunConfigPanel(), gbc);
		gbc.gridy++;

		westPanel.add(this.getButtonPanel(), gbc);
		gbc.gridy++;
		
		gbc.gridy++;
		gbc.gridx = 0;
		add(westPanel, BorderLayout.WEST);
		
		torxakisPanel = new TorXakisPanel(this);
		add(torxakisPanel, BorderLayout.CENTER);
		this.pack();
		this.setModal(true);
//		this.setResizable(false);
		this.centerOnScreen();
	}
	
	public JPanel getConConfigPanel(){
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		panel.setBorder(BorderFactory.createTitledBorder("Connection configurations"));
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0.4;
		gbc.weighty = 0.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.insets = new Insets(5,5,5,5);
				
		JLabel programLabel = new JLabel("Host");
		panel.add(programLabel, gbc);
		
		gbc.gridx++;
		gbc.weightx = 0.6;
		gbc.gridwidth = 2;
		programField = new JTextField(sessionSettings.getAttribute(SessionSettings.HOST));
		programField.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(programField, gbc);
		
		gbc.gridy++;
		gbc.gridx = 0;
		gbc.weightx = 0.4;
		gbc.gridwidth = 1;
		JLabel portLabel = new JLabel("Port");
		panel.add(portLabel, gbc);
		
		gbc.gridx++;
		gbc.weightx = 1;
		gbc.gridwidth = 2;
		int port = Integer.valueOf(sessionSettings.getAttribute(SessionSettings.PORT));
		portNumber = new JSpinner(new SpinnerNumberModel(port, 0, 250000, 1));
		JSpinner.NumberEditor editor = new JSpinner.NumberEditor(portNumber, "#"); 
		portNumber.setEditor(editor);
		panel.add(portNumber, gbc);
		
		gbc.gridy++;
		gbc.gridx = 0;
		gbc.weightx = 1.0;
		gbc.gridwidth = 1;
		panel.add(new JLabel("TorXakis directory"),gbc);
		
		gbc.gridy++;
		gbc.gridwidth = 2;
		pathField = new JTextField(sessionSettings.getAttribute(SessionSettings.TORXAKIS_DIRECTORY), 20);
		pathField.setEditable(false);
		panel.add(pathField, gbc);
		
		JButton btnChoose = new JButton("Choose");
		btnChoose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (Environment.OperatingSystem == Environment.OS.Windows) {
					JFileChooser fc = new JFileChooser(pathField.getText());
					fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					int returnVal = fc.showOpenDialog(runDialog);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						String path = fc.getSelectedFile().getPath();
						pathField.setText(path);
					}
				} else {
					if (Environment.OperatingSystem == Environment.OS.Mac) {
						System.setProperty("apple.awt.fileDialogForDirectories", "true");
					}
					FileDialog fd = new FileDialog(runDialog, "Choose Directory", FileDialog.LOAD);
					fd.setFilenameFilter(new FilenameFilter() {
						@Override
						public boolean accept(File file, String dir) {
							return false;
						}
					});
					fd.setVisible(true);
					File f = new File(fd.getDirectory() + fd.getFile());
					if (Environment.OperatingSystem == Environment.OS.Mac) {
						System.setProperty("apple.awt.fileDialogForDirectories", "false");
					}
					if (f.exists() && f.isDirectory()) {
						pathField.setText(f.getPath());
					}
				}
			}
		});
		gbc.gridx++;
		gbc.gridx++;
		gbc.gridwidth = 1;
		panel.add(btnChoose,gbc);
	
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
		ArrayList<String> models = Session.getSession().getProject().getDefinitionsByTypeDef(TextualDefinition.DefType.MODEL);
		for(String s : models){
			modelDefinitions.addItem(s);
		}
		String savedModel = sessionSettings.getAttribute(SessionSettings.MODEL);
		if(models.contains(savedModel)) modelDefinitions.setSelectedItem(savedModel);
		panel.add(modelDefinitions, gbc);
		gbc.gridy++;
		gbc.gridx = 0;
		gbc.weightx = 0.4;

		JLabel connectLabel = new JLabel("Connect definition");
		panel.add(connectLabel, gbc);
		gbc.gridx++;
		gbc.weightx = 0.6;
		
		connectDefinitions = new JComboBox<String>();
		ArrayList<String> connections = Session.getSession().getProject().getDefinitionsByTypeDef(TextualDefinition.DefType.CNECTDEF);
		for(String s : connections){
			connectDefinitions.addItem(s);
		}
		String savedConnection = sessionSettings.getAttribute(SessionSettings.MODEL);
		if(models.contains(savedConnection)) modelDefinitions.setSelectedItem(savedConnection);
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
		torxakisType.setSelectedItem(TorXakisType.valueOf(sessionSettings.getAttribute(SessionSettings.TORXAKIS_TYPE)));
		panel.add(torxakisType, gbc);
		gbc.gridy++;
		gbc.gridx = 0;
		gbc.weightx = 0.4;
		
		JLabel specLabel = new JLabel("Number of steps");
		panel.add(specLabel, gbc);
		gbc.gridx++;
		gbc.weightx = 0.6;
		
		int iterations = Integer.valueOf(sessionSettings.getAttribute(SessionSettings.ITERATIONS));
		testNumber = new JSpinner(new SpinnerNumberModel(iterations, 0, null, 1));
		JSpinner.NumberEditor editor = new JSpinner.NumberEditor(testNumber, "#"); 
		testNumber.setEditor(editor);
		panel.add(testNumber, gbc);
		gbc.gridy++;
		gbc.gridx = 0;
		gbc.weightx = 0.4;
		return panel;
	}
	
	public void startTorxakisServer(int port){
		String pathToTorXakis = new File(pathField.getText()).getPath();
		if(Environment.OperatingSystem == Environment.OS.Windows){
//			try {
				Runnable r = new Runnable(){
					@Override
					public void run() {
						try {
							process = Runtime.getRuntime().exec("cmd.exe /c start " + pathToTorXakis + "\\txsserver.exe " + port);
						} catch (IOException e) {
							System.out.println("FROM CATCH" + e.toString());
						}
					}
				};
				(new Thread(r)).start();;
//				ProcessBuilder pb = new ProcessBuilder("cmd.exe","/c","start",pathToTorXakis+"\\txsserver.exe " + port);
//				ProcessBuilder pb = new ProcessBuilder(new String[]{pathToTorXakis+"\\txsserver.exe", port+""});
//				process = pb.start();
//				BufferedWriter writeer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
//				writeer.flush();
//			} catch (IOException e) {
//				
//			}
		}else{
			String[] arguments = new String[] {"java", "-jar", pathToTorXakis +"/Test.jar"};
			try {
				Process proc = new ProcessBuilder(arguments).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public JPanel getButtonPanel(){
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton save = new JButton("Save & Run");
		save.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				int port = (int) portNumber.getValue();
				String host = programField.getText();
				String model = String.valueOf(modelDefinitions.getSelectedItem());
				String connection = String.valueOf(connectDefinitions.getSelectedItem());
				int iterations = (int) testNumber.getValue();
				TorXakisType type = (TorXakisType) torxakisType.getSelectedItem();
				File f = new File(pathField.getText());
				SessionSettings settings = new SessionSettings(port, iterations, host, model, connection, type, f.getPath());
				Session.getSession().setSettings(settings);
				if(socketIO == null){
					torxakisPanel.clear();
					startTorxakisServer(port);
					Session.getSession().getProject().saveAs(Session.TEMP_TXS, FileTypeAssociation.TorXakisExport.getDefaultFileType());
					try{
						Thread.sleep(1000);
						socketIO = new SocketIO(runDialog, port, host);
					}catch(Exception socketException){
						JOptionPane.showMessageDialog(null, "Can not connect to TorXakis. Are you sure that you pick the right directory?");
						runDialog.shutdown();
					}
				}
				try{
					if(!socketIO.hasStarted()){
						System.err.println("Try to start socketIO");
						socketIO.startTorXakis(Session.TEMP_TXS);
					}					
				}catch(Exception exception){
					shutdown();
					return;
				}
				if(socketIO.typeChanged(type)){
					socketIO.changeTorXakisType(type, model, connection);
				}
				socketIO.run(iterations);
				
				if(!torxakisPanel.isReading()){
					Runnable r = new Runnable(){
						@Override
						public void run() {
							try{
								torxakisPanel.readLines(socketIO.getReader());
							}catch (Exception e){
								e.printStackTrace();
								if(!stopped){
									System.err.println("CATCH FIRED");
									runDialog.shutdown();
									JOptionPane.showMessageDialog(runDialog, "Possible problems:\n"
											+ "\t\t - TorXakis server is closed.\n"
											+ "\t\t - The SUT is not running at the specified host/port.\n"
											+ "\t\t - Another instance of TorXakis is already running at the same port.", "Communication error", JOptionPane.ERROR_MESSAGE);
								}
								stopped = false;
							}
						}
						
					};
					(new Thread(r)).start();
				}
			}
		});
		JButton stop = new JButton("Stop");
		stop.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				shutdown();
			}
		});
		
		JButton cancel = new JButton("Close");
		cancel.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				shutdown();
				runDialog.dispose();
			}
		});
		panel.add(cancel);
		panel.add(stop);
		panel.add(save);
		return panel;
	}
	
	public void shutdown(){
		stopped = true;
		if(socketIO != null)
			socketIO.close();
		destroyCMD();
		socketIO = null;
	}

	public void destroyCMD(){
		if(process != null && process.isAlive()) process.destroy();
	}
	
	public SocketIO getSocketIO(){
		return socketIO;
	}
	
	@Override
	public void windowActivated(WindowEvent arg0) {
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		shutdown();
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
	}
}
