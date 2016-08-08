package opticalraytracer;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

import opticalraytracer.Emitter.EmitterParam;

public class Emitter {
	// The EnumMap is used to map a parameter name to a value
	private EnumMap<EmitterParam, Object> emitterParameterValueMap;
		
	// This AtomicLong is used as a monotonic-increasing counter number that
	// is assigned to each Emitter object whenever one is instantiated and is
	// immediately incremented by one. This ensures that each Emitter object
	// gets a unique-per-program-run identifier.
	private static final AtomicLong emitterID = new AtomicLong();
	
	public static enum EmitterParam {
		ID,				// Unique identifier 
		NAME, 			// String
		XPOS, 			// double
		YPOS, 			// double
		ANGLE, 			// double
		BEAMANGLE, 		// double
		NUMRAYS, 		// integer
		ISDIVERGING, 	// boolean
		ISCOLLIMATED,	// boolean
		TYPE,			// EmitterType enum
		DRAW			// boolean
	}
	
	public static enum EmitterType {
		POINT,			// A point source
		AREA			// A non-point source
	}
	
	public Emitter() {
		// Instantiate parameter value map and populate it with default, hard-coded values.
		emitterParameterValueMap = new EnumMap<EmitterParam, Object>(EmitterParam.class);
		for(EmitterParam name: EmitterParam.values()) {
			Object value;
			switch(name) {
				case ID:
					value = emitterID.getAndIncrement();
					break;
				case XPOS:
					value = new Double(0.0);
					break;
				case YPOS:
					value = new Double(0.0);
					break;
				case ANGLE:
					value = new Double(0.0);
					break;
				case BEAMANGLE:
					value = new Double(60.0); 
					break;
				case NUMRAYS:
					value = new Integer(8);
					break;
				case ISDIVERGING:
					value = new Boolean(true);
					break;
				case ISCOLLIMATED:
					value = new Boolean(false);
					break;
				case NAME: // This is handled separately... see below.
//					value = new String("Default Emitter");	
					value = null;
					break;
				case TYPE:
					value = EmitterType.POINT;
					break;
				case DRAW:
					value = true;
					break;
				default:
					value = null;
					break;
			}
			emitterParameterValueMap.put(name, value);
		}
		// We set the NAME parameter here on purpose, after the ID parameter has been set. 
		this.setParamValue(EmitterParam.NAME, "Default Emitter " + this.getID());
	}
	
	/**
	 * Returns the value associated with the given parameter.
	 * 
	 * @param name - The name of the requested parameter value.
	 * @return The value associated with the given parameter.
	 */
	public Object getParamValue(EmitterParam name) {
		return this.emitterParameterValueMap.get(name);
	}
	
	/**
	 * Returns a Collection containing all the parameter values. The collection's 
	 * iterator will return the values in the order their corresponding keys appear 
	 * in map, which is their natural order (the order in which the enum constants 
	 * are declared)
	 * 
	 * @return A collection containing all the Emitter's parameter values.
	 */
	public Collection<Object> getParamValues() {
		return this.emitterParameterValueMap.values();
	}
	
	/**
	 * Returns the unique (per program run) identifier number for the Emitter object.
	 * 
	 * @return The unique (type Long) identifier number assigned to the Emitter object.
	 */
	public Long getID() {
		return (Long) this.emitterParameterValueMap.get(EmitterParam.ID);
	}
	
	/**
	 * Stores the provided value for the parameter associated with name.
	 * This method will overwrite a value without warning if it is already set. This 
	 * method also does not currently check to make sure the value is the correct
	 * type.
	 * 
	 * @param name - The name of the parameter.
	 * @param value - The value to which the parameter shall be set.
	 */
	public void setParamValue(EmitterParam name, Object value) {
		if(!name.equals(EmitterParam.ID)) {
			this.emitterParameterValueMap.put(name, value);
		}
		else {
			System.err.println("You are not allowed to change an Emitter's ID.");
		}
	}
	
	@Override
	public String toString() {
		return (String) this.getParamValue(EmitterParam.NAME);
	}
	
	/* ######################################################################
	 * PUBLIC GETTERS
	 * ###################################################################### */
	public double getXPos() {
		return (double) this.getParamValue(EmitterParam.XPOS);
	}
	
	public double getYPos() {
		return (double) this.getParamValue(EmitterParam.YPOS);
	}
	
	public double getAngle() {
		return (double) this.getParamValue(EmitterParam.ANGLE);
	}
	
	public double getBeamAngle() {
		return (double) this.getParamValue(EmitterParam.BEAMANGLE);
	}
	
	public int getNumRays() {
		return (int) this.getParamValue(EmitterParam.NUMRAYS);
	}
	
	public String getName() {
		return (String) this.getParamValue(EmitterParam.NAME);
	}
	
	public EmitterType getType() {
		return (EmitterType) this.getParamValue(EmitterParam.TYPE);
	}
	
	public boolean getDraw() {
		return (boolean) this.getParamValue(EmitterParam.DRAW);
	}
	
	/* ######################################################################
	 * MAIN METHOD (FOR TEST PURPOSES)
	 * ###################################################################### */
	public static void main(String args[]) {
		System.out.println("hi");
		Emitter e = new Emitter();
		
		EmitterParam[] x = EmitterParam.values();
		
		EnumMap<EmitterParam, Object> m = new EnumMap<EmitterParam, Object>(EmitterParam.class);
		m.put(EmitterParam.XPOS, 1.0);
		m.put(EmitterParam.YPOS, 2.0);
		
		System.out.println(m.get(EmitterParam.XPOS).toString());
		System.out.println(m.get(EmitterParam.XPOS).getClass().toString());
		Object z = m.get(EmitterParam.XPOS).getClass().cast(0.0);
		
		AtomicLong foofoo = new AtomicLong();
		System.out.println(foofoo.toString());
		System.out.println(String.valueOf(foofoo.getAndIncrement()));
		System.out.println(String.valueOf(foofoo.getAndIncrement()));
		System.out.println(String.valueOf(foofoo.getAndIncrement()));
		
		AtomicLong booboo = new AtomicLong();
		System.out.println(String.valueOf(booboo.getAndIncrement()));
		
		Object o = e.getParamValue(EmitterParam.ID);
		System.out.println(o.getClass().toString());
		System.out.println(o.toString());
	}



	
}