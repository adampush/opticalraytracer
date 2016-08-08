package opticalraytracer;

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridBagLayout;
import javax.swing.JTextField;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import java.awt.event.MouseWheelListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.awt.event.MouseWheelEvent;
import javax.swing.event.ChangeListener;

import opticalraytracer.Emitter.EmitterParam;

import javax.swing.event.ChangeEvent;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JRadioButton;
import javax.swing.JCheckBox;

public class MyCustomFrame extends JFrame {

	private JPanel contentPane;
	
	/* ################################################################
	 * USER APPLICATION MEMBERS 
	 * ################################################################ */
	
	
	// Reference to the "master" OpticalRayTracer object. We need this so that
	// we can call the ORT's updateGraphicDisplay() method to do the ray tracing
	// and drawing.
	private OpticalRayTracer parent;
	
	private MyInitializationManager initManager;
	
	// Items for keeping track of and organizing Emitter objects.
	private HashMap<Long, Emitter> emitterMap;		// HashMap to store key/values: Long/Emitter. The Long ID is obtained from the Emitter object.
	private Emitter currentEmitter;					// This variable is used as a pointer to the "active" Emitter object.
	
	// Important GUI items that may need to talk to each other and pass data
	// and information between them.
	private JSpinner xPosSpinner;
	private JSpinner yPosSpinner;
	private JSpinner numRaysSpinner;
	private JSpinner angleSpinner;
	private JSpinner beamAngleSpinner;
	private JComboBox<Emitter[]> emitterCmbBox;
	private JLabel lblName;
	private JTextField emitterNameField;
	private JCheckBox chkboxDraw;
	private JTextField configFileNameField;
	private JButton btnNewButton;
	/* ################################################################
	 * END USER APPLICATION MEMBERS
	 * ################################################################ */
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MyCustomFrame frame = new MyCustomFrame(new OpticalRayTracer(null));	// This probably is broken, I just hacked it so that it will compile.
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MyCustomFrame(OpticalRayTracer o) {
		
		/* ########################## USER APPLICATION CODE ############################## */
		// Set parent OpticalRayTracer object
		this.setParentORT(o);
		// Instantiate the HashMap to keep track of the Emitter objects
		this.emitterMap = new HashMap<Long, Emitter>();
		Emitter foo = new Emitter();
		emitterMap.put(foo.getID(), foo);
		this.currentEmitter = foo;
		this.initManager = new MyInitializationManager(o, new ProgramValues(), this);		// ProgramValues needs to be refactored out of MyInitializationManager
		/* ######################### END USER APPLICATION CODE ########################## */
		
		
		setAlwaysOnTop(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 385, 309);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{46, 111, 56, 124, 0};
		gbl_contentPane.rowHeights = new int[]{2, 20, 19, -10, 20, -27, 23, 23, 0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		numRaysSpinner = new JSpinner();
		numRaysSpinner.setModel(new SpinnerNumberModel(this.currentEmitter.getNumRays(), 0, 256, 1));
		numRaysSpinner.addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent e) {
				handleSpinnerMouseWheelMoved(e, numRaysSpinner, Integer.class, EmitterParam.NUMRAYS);
			}
		});
		numRaysSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				handleSpinnerChanged(e, numRaysSpinner, Integer.class, EmitterParam.NUMRAYS);
			}
		});
		
		lblName = new JLabel("Name");
		GridBagConstraints gbc_lblName = new GridBagConstraints();
		gbc_lblName.anchor = GridBagConstraints.EAST;
		gbc_lblName.insets = new Insets(0, 0, 5, 5);
		gbc_lblName.gridx = 0;
		gbc_lblName.gridy = 0;
		contentPane.add(lblName, gbc_lblName);
		
		emitterNameField = new JTextField(this.currentEmitter.getName());
		GridBagConstraints gbc_emitterNameField = new GridBagConstraints();
		gbc_emitterNameField.insets = new Insets(0, 0, 5, 5);
		gbc_emitterNameField.fill = GridBagConstraints.HORIZONTAL;
		gbc_emitterNameField.gridx = 1;
		gbc_emitterNameField.gridy = 0;
		contentPane.add(emitterNameField, gbc_emitterNameField);
		emitterNameField.setColumns(10);
		
		JLabel lblNumRays = new JLabel("Num Rays");
		GridBagConstraints gbc_lblNumRays = new GridBagConstraints();
		gbc_lblNumRays.anchor = GridBagConstraints.EAST;
		gbc_lblNumRays.insets = new Insets(0, 0, 5, 5);
		gbc_lblNumRays.gridx = 2;
		gbc_lblNumRays.gridy = 0;
		contentPane.add(lblNumRays, gbc_lblNumRays);
		GridBagConstraints gbc_numRaysSpinner = new GridBagConstraints();
		gbc_numRaysSpinner.fill = GridBagConstraints.HORIZONTAL;
		gbc_numRaysSpinner.insets = new Insets(0, 0, 5, 0);
		gbc_numRaysSpinner.gridx = 3;
		gbc_numRaysSpinner.gridy = 0;
		contentPane.add(numRaysSpinner, gbc_numRaysSpinner);
		
		angleSpinner = new JSpinner();
		angleSpinner.setModel(new SpinnerNumberModel((Double)this.currentEmitter.getAngle(), new Double(-360), new Double(360), new Double(1)));
		angleSpinner.addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent e) {
				handleSpinnerMouseWheelMoved(e, angleSpinner, Double.class, EmitterParam.ANGLE);
			}
		});
		angleSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				handleSpinnerChanged(e, angleSpinner, Double.class, EmitterParam.ANGLE);
			}
		});
		
		xPosSpinner = new JSpinner();
		xPosSpinner.setModel(new SpinnerNumberModel(this.currentEmitter.getXPos(), -100.0, 100.0, 1.0));
		xPosSpinner.addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent e) {
				handleSpinnerMouseWheelMoved(e, xPosSpinner, Double.class, EmitterParam.XPOS);
			}
		});
		xPosSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				handleSpinnerChanged(arg0, xPosSpinner, Double.class, EmitterParam.XPOS);
			}
		});
		
		JLabel lblXPosition = new JLabel("X Position");
		GridBagConstraints gbc_lblXPosition = new GridBagConstraints();
		gbc_lblXPosition.anchor = GridBagConstraints.EAST;
		gbc_lblXPosition.insets = new Insets(0, 0, 5, 5);
		gbc_lblXPosition.gridx = 0;
		gbc_lblXPosition.gridy = 1;
		contentPane.add(lblXPosition, gbc_lblXPosition);
		GridBagConstraints gbc_xPosSpinner = new GridBagConstraints();
		gbc_xPosSpinner.fill = GridBagConstraints.HORIZONTAL;
		gbc_xPosSpinner.insets = new Insets(0, 0, 5, 5);
		gbc_xPosSpinner.gridx = 1;
		gbc_xPosSpinner.gridy = 1;
		contentPane.add(xPosSpinner, gbc_xPosSpinner);
		
		JLabel lblAngle = new JLabel("Angle");
		GridBagConstraints gbc_lblAngle = new GridBagConstraints();
		gbc_lblAngle.anchor = GridBagConstraints.EAST;
		gbc_lblAngle.insets = new Insets(0, 0, 5, 5);
		gbc_lblAngle.gridx = 2;
		gbc_lblAngle.gridy = 1;
		contentPane.add(lblAngle, gbc_lblAngle);
		GridBagConstraints gbc_angleSpinner = new GridBagConstraints();
		gbc_angleSpinner.fill = GridBagConstraints.HORIZONTAL;
		gbc_angleSpinner.insets = new Insets(0, 0, 5, 0);
		gbc_angleSpinner.gridx = 3;
		gbc_angleSpinner.gridy = 1;
		contentPane.add(angleSpinner, gbc_angleSpinner);
		
		JLabel lblYPosition = new JLabel("Y Position");
		GridBagConstraints gbc_lblYPosition = new GridBagConstraints();
		gbc_lblYPosition.anchor = GridBagConstraints.EAST;
		gbc_lblYPosition.insets = new Insets(0, 0, 5, 5);
		gbc_lblYPosition.gridx = 0;
		gbc_lblYPosition.gridy = 2;
		contentPane.add(lblYPosition, gbc_lblYPosition);
		
		yPosSpinner = new JSpinner();
		yPosSpinner.setModel(new SpinnerNumberModel(this.currentEmitter.getYPos(), -100.0, 100.0, 1.0));
		yPosSpinner.addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent e) {
				handleSpinnerMouseWheelMoved(e, yPosSpinner, Double.class, EmitterParam.YPOS);
			}
		});
		yPosSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				handleSpinnerChanged(e, yPosSpinner, Double.class, EmitterParam.YPOS);
			}
		});
		GridBagConstraints gbc_yPosSpinner = new GridBagConstraints();
		gbc_yPosSpinner.fill = GridBagConstraints.HORIZONTAL;
		gbc_yPosSpinner.insets = new Insets(0, 0, 5, 5);
		gbc_yPosSpinner.gridx = 1;
		gbc_yPosSpinner.gridy = 2;
		contentPane.add(yPosSpinner, gbc_yPosSpinner);
		
		JLabel lblBeamAngle = new JLabel("Beam Angle");
		GridBagConstraints gbc_lblBeamAngle = new GridBagConstraints();
		gbc_lblBeamAngle.insets = new Insets(0, 0, 5, 5);
		gbc_lblBeamAngle.gridx = 2;
		gbc_lblBeamAngle.gridy = 2;
		contentPane.add(lblBeamAngle, gbc_lblBeamAngle);
		
		beamAngleSpinner = new JSpinner();
		beamAngleSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				handleSpinnerChanged(e, beamAngleSpinner, Double.class, EmitterParam.BEAMANGLE);
			}
		});
		beamAngleSpinner.addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent e) {
				handleSpinnerMouseWheelMoved(e, beamAngleSpinner, Double.class, EmitterParam.BEAMANGLE);
			}
		});
		beamAngleSpinner.setModel(new SpinnerNumberModel(this.currentEmitter.getBeamAngle(), -180.0, 180.0, 1.0));
		GridBagConstraints gbc_beamAngleSpinner = new GridBagConstraints();
		gbc_beamAngleSpinner.fill = GridBagConstraints.HORIZONTAL;
		gbc_beamAngleSpinner.insets = new Insets(0, 0, 5, 0);
		gbc_beamAngleSpinner.gridx = 3;
		gbc_beamAngleSpinner.gridy = 2;
		contentPane.add(beamAngleSpinner, gbc_beamAngleSpinner);
		

		/* ##############################
		 *  COMBO BOX
		 * ############################## */
		emitterCmbBox = new JComboBox<Emitter[]>();
		emitterCmbBox.setModel(new DefaultComboBoxModel( (Emitter[])emitterMap.values().toArray(new Emitter[0]) ));
		emitterCmbBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Common.p("Combo box action performed.");
				System.out.println(emitterCmbBox.getSelectedItem().toString());
				
				// Set field currentEmitter to point to the currently selected Emitter.
				currentEmitter = (Emitter)emitterCmbBox.getSelectedItem();
				// Load Emitter params into all the GUI controls
				loadGUIParamsFromCurrentEmitter();
			}
		});
		emitterCmbBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				Common.p("combo box state changed.");
			}
		});
		GridBagConstraints gbc_emitterCmbBox = new GridBagConstraints();
		gbc_emitterCmbBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_emitterCmbBox.insets = new Insets(0, 0, 5, 5);
		gbc_emitterCmbBox.gridwidth = 2;
		gbc_emitterCmbBox.gridx = 0;
		gbc_emitterCmbBox.gridy = 5;
		contentPane.add(emitterCmbBox, gbc_emitterCmbBox);
		
		/* ##############################
		 *  ADD BUTTON
		 * ############################## */
		JButton btnAdd = new JButton("Add");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Common.p("Add button action performed.");
				long t0 = System.nanoTime();	// Time measurement start
				Emitter foo = new Emitter();
				emitterMap.put(foo.getID(), foo);
				emitterCmbBox.setModel(new DefaultComboBoxModel( (Emitter[])emitterMap.values().toArray(new Emitter[0])));
				// Set the currently selected combo box item to the Emitter we just added.
				emitterCmbBox.setSelectedItem(foo);
				System.out.println("Adding new Emitter took " + ((Long)(System.nanoTime() - t0)).toString());		// Time measurement end
			}
		});
		
		chkboxDraw = new JCheckBox("Draw?");
		chkboxDraw.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				currentEmitter.setParamValue(EmitterParam.DRAW, chkboxDraw.isSelected());
				getParentORT().updateGraphicDisplay();
			}
		});
		chkboxDraw.setSelected(true);
		GridBagConstraints gbc_chkboxDraw = new GridBagConstraints();
		gbc_chkboxDraw.anchor = GridBagConstraints.WEST;
		gbc_chkboxDraw.insets = new Insets(0, 0, 5, 0);
		gbc_chkboxDraw.gridx = 3;
		gbc_chkboxDraw.gridy = 5;
		contentPane.add(chkboxDraw, gbc_chkboxDraw);
		GridBagConstraints gbc_btnAdd = new GridBagConstraints();
		gbc_btnAdd.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnAdd.insets = new Insets(0, 0, 5, 5);
		gbc_btnAdd.gridwidth = 2;
		gbc_btnAdd.gridx = 0;
		gbc_btnAdd.gridy = 6;
		contentPane.add(btnAdd, gbc_btnAdd);
//		comboBox.setModel(new DefaultComboBoxModel(emitters));
		
		/* ##############################
		 * 	REMOVE BUTTON
		 * ############################## */
		JButton btnRemove = new JButton("Remove");
		btnRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Common.p("Remove button action performed.");
				
				// TODO: add check to make sure we only try to remove Emitter object if the list is not empty.
				
				// Create local reference to current selected combo box item (Emitter)
				Emitter currEm = (Emitter)emitterCmbBox.getSelectedItem();
				// Get index of current selected combo box item
				int ind = emitterCmbBox.getSelectedIndex();
				// Remove the current selected Emitter from the Emitter HashMap
				emitterMap.remove(currEm.getID());
				// Update the combo box model based on the Emitter HashMap we just modified
				emitterCmbBox.setModel(new DefaultComboBoxModel( (Emitter[])emitterMap.values().toArray(new Emitter[0])));
				// Set the current selected combo box index to the previous index, unless the current index is 0 (or -1).  
				if(ind > 0) {
					emitterCmbBox.setSelectedIndex(ind-1);
				}
			}
		});
		GridBagConstraints gbc_btnRemove = new GridBagConstraints();
		gbc_btnRemove.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnRemove.insets = new Insets(0, 0, 5, 5);
		gbc_btnRemove.gridwidth = 2;
		gbc_btnRemove.gridx = 0;
		gbc_btnRemove.gridy = 7;
		contentPane.add(btnRemove, gbc_btnRemove);
		
		configFileNameField = new JTextField();
		GridBagConstraints gbc_configFileNameField = new GridBagConstraints();
		gbc_configFileNameField.gridwidth = 2;
		gbc_configFileNameField.insets = new Insets(0, 0, 5, 5);
		gbc_configFileNameField.fill = GridBagConstraints.HORIZONTAL;
		gbc_configFileNameField.gridx = 0;
		gbc_configFileNameField.gridy = 8;
		contentPane.add(configFileNameField, gbc_configFileNameField);
		configFileNameField.setColumns(10);
		
		btnNewButton = new JButton("Set Config File");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnNewButton.gridwidth = 2;
		gbc_btnNewButton.insets = new Insets(0, 0, 0, 5);
		gbc_btnNewButton.gridx = 0;
		gbc_btnNewButton.gridy = 9;
		contentPane.add(btnNewButton, gbc_btnNewButton);
	}

	
	
	/* ################################################################
	 *  EVENT HANDLERS 
	 * ############################################################# */
	
	private void loadGUIParamsFromCurrentEmitter() {
		this.angleSpinner.setValue(this.currentEmitter.getAngle());
		this.xPosSpinner.setValue(this.currentEmitter.getXPos());
		this.yPosSpinner.setValue(this.currentEmitter.getYPos());
		this.numRaysSpinner.setValue(this.currentEmitter.getNumRays());
		this.beamAngleSpinner.setValue(this.currentEmitter.getBeamAngle());
		this.emitterNameField.setText(this.currentEmitter.getName());
		this.chkboxDraw.setSelected(this.currentEmitter.getDraw());
	}
	
	private void handleSpinnerMouseWheelMoved(MouseWheelEvent evt, JSpinner jSpin, Class<?> C, EmitterParam param) {
		Common.p("Mouse wheel moved.");
		Common.p("Class arg is: " + C.toString());
		
		// TODO: use generics to consolidate val casting and assignment.
		if(C.equals(Integer.class)) {
			Common.p("Spinner value is type Integer");	// debug
			Integer val = (Integer)jSpin.getValue();
			Integer dval = evt.getWheelRotation()*MyCommon.MOUSE_INCREMENT_INT;
			
			Common.p("val is: " + val.toString());		// debug
			Common.p("dval is: " + dval.toString());	// debug
			
			val -= dval;
			jSpin.setValue((Object)val);
			this.updateParams(param,val);
		}
		else if(C.equals(Float.class)) {
			Common.p("Spinner value is type Float");	// debug
			Float val = (Float)jSpin.getValue();
			Float dval = evt.getWheelRotation()*MyCommon.MOUSE_INCREMENT_FLOAT;
			Common.p("val is: " + val.toString());		// debug
			Common.p("dval is: " + dval.toString());	// debug
			
			val -= dval;
			jSpin.setValue((Object)val);
			this.updateParams(param, val);
		}
		else if(C.equals(Double.class)) {
			Common.p("Spinner value is type Double");	// debug
			Double val = (Double)jSpin.getValue();
			Double dval = evt.getWheelRotation()*MyCommon.MOUSE_INCREMENT_DOUBLE;
			Common.p("val is: " + val.toString());		// debug
			Common.p("dval is: " + dval.toString());	// debug
			
			val -= dval;
			jSpin.setValue((Object)val);
			this.updateParams(param, val);
		}
		
		/* Vestigial code from OpticalRayTracer Mouse Wheel handler (see 
		 * handleMouseWheelMoved() and handleIncrement() in ControlManager.java */
//		double v = sens;
//		v = (evt.isShiftDown()) ? v * 0.1 : v;
//		v = (evt.isAltDown()) ? v * 0.1 : v;
//		int n = -evt.getWheelRotation();
//		double sign = 1;
//		double sv = v;
//		
//		Double val = (Double)jSpin.getValue();
//		if(val.toString() == null) {
//			Common.p("null value jSpin");
//		}
//		else {
//			Common.p(val.toString());
//			Common.p("hi");
//			Double d = MyCommon.MOUSE_INCREMENT;
//			Common.p(d.toString());	
//		}
//		if (sign != 0) {
//			if (jSpin != null) {
//				String text = "1";
//				double dv = 0;
//				try {
//					dv = LocaleHandler.getDouble(text,
//							LocaleHandler.localeDecimalSeparator);
//				} catch (Exception ex) {
//					System.out.println(getClass().getName() + ": Error: " + ex);
//				}
//				dv += (n * sv);
//				dv *= sign;
//				dv = min(DMAX, dv);
//				dv = max(DMIN, dv);
//				String s = parent.formatNum(dv);
//				numberField.setText(s);
//				 parent.p("handleincrement setting value: " + s);
//			}
//			updateAllControls();
//		}
		/* End vestigial code */
//		this.getParentORT().updateGraphicDisplay();
		evt.consume();
	}
	
	private void updateParams(EmitterParam param, Object val) {
		// TODO: we probably can replace the Object-type argument with generics to make
		// this a little cleaner looking.
		// TODO: also/alternatively we can replace the String param with the Enum ParameterName.
		// TODO: also/alternatively we can replace the if-then-else construct with a switch-case construct.
		if(param.equals(EmitterParam.XPOS)) {
			this.currentEmitter.setParamValue(EmitterParam.XPOS, val);
		}
		else if(param.equals(EmitterParam.YPOS)) {
			this.currentEmitter.setParamValue(EmitterParam.YPOS, val);
		}
		else if(param.equals(EmitterParam.ANGLE)) {
			this.currentEmitter.setParamValue(EmitterParam.ANGLE, val);
		}
		else if(param.equals(EmitterParam.NUMRAYS)) {
			this.currentEmitter.setParamValue(EmitterParam.NUMRAYS, val);
		}
		else if(param.equals(EmitterParam.BEAMANGLE)) {
			this.currentEmitter.setParamValue(EmitterParam.BEAMANGLE, val);
		}
	}
	
	private void handleSpinnerChanged(ChangeEvent evt, JSpinner jSpin, Class<?> C, EmitterParam param) {
		Common.p("Spinner " + jSpin.toString() + " changed.");
		Common.p("New value is: " + jSpin.getValue().toString());
		this.updateParams(param, jSpin.getValue());
		this.getParentORT().updateGraphicDisplay();
	}
	
	/* #############################################################################
	 * GETTERS & SETTERS
	 * ############################################################################# */
	
	
	public OpticalRayTracer getParentORT() {
		return this.parent;
	}
	public void setParentORT(OpticalRayTracer o) {
		this.parent = o;
	}
	
	public HashMap<Long, Emitter> getEmitters() {
		return this.emitterMap;
	}

	/**
	 * Returns the current (active) Emitter object.
	 * @return The current (active) Emitter object.
	 */
	public Emitter getCurrentEmitter() {
		return this.currentEmitter;
	}
	
	
	/* #############################################################################
	 * CONFIGURATION READ & WRITE
	 * ############################################################################# */
	
	// TODO: Need to refactor and modify the below stuff which is all copy-pasted from P. Lutus code.
//	protected void writeConfig(boolean includeHeader) {
//		writeFile(initPath, getFullConfiguration(includeHeader));
//	}
//
//	protected String getFullConfiguration(boolean includeHeader) {
//		StringBuilder sb = new StringBuilder();
//		if (includeHeader) {
//			sb.append(String.format("# %s\n", parent.fullAppName));
//			sb.append("# http://arachnoid.com/OpticalRayTracer\n");
//			String date = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z")
//					.format(new Date());
//			sb.append(String.format("# %s\n\n", date));
//		}
//		sb.append(String.format("program {\n"));
//		sb.append(programValues.getValues());
//		sb.append(String.format("}\n\n"));
//		for (OpticalComponent oc : parent.componentList) {
//			sb.append(oc.getValues());
//			sb.append("\n");
//		}
//		return sb.toString();
//	}
//	
//	protected String getValues() {
//		ArrayList<String> list = new ArrayList<>();
//		Field[] fields = getClass().getDeclaredFields();
//		for (Field f : fields) {
//			String tag = f.getName();
//			list.add(String.format("  %-25s = %s\n", tag, getOneValue(tag)));
//		}
//		Collections.sort(list);
//		StringBuilder sb = new StringBuilder();
//		for (String s : list) {
//			sb.append(s);
//		}
//		return sb.toString();
//	}
	
	private String readFile(String path) {
		String result = null;
		try {
			result = new String(Files.readAllBytes(Paths.get(path)),
					StandardCharsets.UTF_8);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			// no initialization file, so first run
			parent.setDefaults(true);
		}
		return result;
	}

	private void writeFile(String path, String data) {
		try {
			PrintWriter out = new PrintWriter(path);
			out.write(data);
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
