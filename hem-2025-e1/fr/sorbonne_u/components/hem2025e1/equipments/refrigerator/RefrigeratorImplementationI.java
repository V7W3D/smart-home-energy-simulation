package fr.sorbonne_u.components.hem2025e1.equipments.refrigerator;

import fr.sorbonne_u.exceptions.AssertionChecking;

// -----------------------------------------------------------------------------
/**
 * The interface <code>RefrigeratorImplementationI</code> defines the signatures
 * of the services implemented by the refrigerator component.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code MIN_TEMPERATURE >= 0.0}
 * invariant	{@code MAX_TEMPERATURE > MIN_TEMPERATURE}
 * invariant	{@code DEFAULT_TARGET_TEMPERATURE >= MIN_TEMPERATURE && DEFAULT_TARGET_TEMPERATURE <= MAX_TEMPERATURE}
 * invariant	{@code OFF_POWER >= 0.0}
 * invariant	{@code STANDBY_POWER >= 0.0}
 * invariant	{@code COMPRESSOR_POWER > STANDBY_POWER}
 * </pre>
 * 
 * <p>Created on : 2025-12-27</p>
 * 
 * @author	Softweavers
 */
public interface		RefrigeratorImplementationI
{
	// -------------------------------------------------------------------------
	// Inner interfaces and types
	// -------------------------------------------------------------------------

	/**
	 * The enumeration <code>RefrigeratorState</code> describes the operation
	 * states of the refrigerator.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>Created on : 2025-12-27</p>
	 * 
	 * @author	Softweavers
	 */
	public static enum	RefrigeratorState
	{
		/** refrigerator is off.											*/
		OFF,
		/** refrigerator is on with compressor running.						*/
		ON,
		/** refrigerator is on but compressor is suspended by HEM.			*/
		SUSPENDED
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** minimum temperature setting (degrees Celsius).						*/
	public static final double	MIN_TEMPERATURE = 0.0;
	/** maximum temperature setting (degrees Celsius).						*/
	public static final double	MAX_TEMPERATURE = 10.0;
	/** default target temperature (degrees Celsius).						*/
	public static final double	DEFAULT_TARGET_TEMPERATURE = 4.0;
	/** power consumption in OFF state (watts).								*/
	public static final double	OFF_POWER = 0.0;
	/** power consumption when compressor is suspended (watts).				*/
	public static final double	STANDBY_POWER = 10.0;
	/** power consumption when compressor is running (watts).				*/
	public static final double	COMPRESSOR_POWER = 150.0;

	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------

	/**
	 * return true if the static invariants are observed, false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if the static invariants are observed, false otherwise.
	 */
	public static boolean	staticInvariants()
	{
		boolean ret = true;
		ret &= AssertionChecking.checkStaticInvariant(
				MIN_TEMPERATURE >= 0.0,
				RefrigeratorImplementationI.class,
				"MIN_TEMPERATURE >= 0.0");
		ret &= AssertionChecking.checkStaticInvariant(
				MAX_TEMPERATURE > MIN_TEMPERATURE,
				RefrigeratorImplementationI.class,
				"MAX_TEMPERATURE > MIN_TEMPERATURE");
		ret &= AssertionChecking.checkStaticInvariant(
				DEFAULT_TARGET_TEMPERATURE >= MIN_TEMPERATURE &&
				DEFAULT_TARGET_TEMPERATURE <= MAX_TEMPERATURE,
				RefrigeratorImplementationI.class,
				"DEFAULT_TARGET_TEMPERATURE >= MIN_TEMPERATURE && DEFAULT_TARGET_TEMPERATURE <= MAX_TEMPERATURE");
		ret &= AssertionChecking.checkStaticInvariant(
				OFF_POWER >= 0.0,
				RefrigeratorImplementationI.class,
				"OFF_POWER >= 0.0");
		ret &= AssertionChecking.checkStaticInvariant(
				STANDBY_POWER >= 0.0,
				RefrigeratorImplementationI.class,
				"STANDBY_POWER >= 0.0");
		ret &= AssertionChecking.checkStaticInvariant(
				COMPRESSOR_POWER > STANDBY_POWER,
				RefrigeratorImplementationI.class,
				"COMPRESSOR_POWER > STANDBY_POWER");
		return ret;
	}

	// -------------------------------------------------------------------------
	// Component services signatures
	// -------------------------------------------------------------------------

	/**
	 * return the current state of the refrigerator.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				the current state of the refrigerator.
	 * @throws Exception 	<i>to do</i>.
	 */
	public RefrigeratorState	getState() throws Exception;

	/**
	 * turn on the refrigerator.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code getState() == RefrigeratorState.OFF}
	 * post	{@code getState() == RefrigeratorState.ON}
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void					turnOn() throws Exception;

	/**
	 * turn off the refrigerator.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code getState() == RefrigeratorState.OFF}
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void					turnOff() throws Exception;

	/**
	 * return the current temperature inside the refrigerator.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code getState() != RefrigeratorState.OFF}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				the current temperature in degrees Celsius.
	 * @throws Exception 	<i>to do</i>.
	 */
	public double				getCurrentTemperature() throws Exception;

	/**
	 * return the target temperature setting.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code getState() != RefrigeratorState.OFF}
	 * post	{@code return >= MIN_TEMPERATURE && return <= MAX_TEMPERATURE}
	 * </pre>
	 *
	 * @return				the target temperature in degrees Celsius.
	 * @throws Exception 	<i>to do</i>.
	 */
	public double				getTargetTemperature() throws Exception;

	/**
	 * set the target temperature.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code getState() != RefrigeratorState.OFF}
	 * pre	{@code target >= MIN_TEMPERATURE && target <= MAX_TEMPERATURE}
	 * post	{@code getTargetTemperature() == target}
	 * </pre>
	 *
	 * @param target		the new target temperature in degrees Celsius.
	 * @throws Exception	<i>to do</i>.
	 */
	public void					setTargetTemperature(double target) throws Exception;

	/**
	 * suspend the compressor (called by HEM for energy management).
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code getState() == RefrigeratorState.ON}
	 * post	{@code getState() == RefrigeratorState.SUSPENDED}
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void					suspendCompressor() throws Exception;

	/**
	 * resume the compressor after suspension.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code getState() == RefrigeratorState.SUSPENDED}
	 * post	{@code getState() == RefrigeratorState.ON}
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void					resumeCompressor() throws Exception;

	/**
	 * return true if the compressor is currently suspended.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code getState() != RefrigeratorState.OFF}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				true if the compressor is suspended, false otherwise.
	 * @throws Exception 	<i>to do</i>.
	 */
	public boolean				isCompressorSuspended() throws Exception;

	/**
	 * return the current power consumption level of the refrigerator.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code return >= 0.0}
	 * </pre>
	 *
	 * @return				the current power consumption in watts.
	 * @throws Exception 	<i>to do</i>.
	 */
	public double				getCurrentPowerLevel() throws Exception;
}
// -----------------------------------------------------------------------------
