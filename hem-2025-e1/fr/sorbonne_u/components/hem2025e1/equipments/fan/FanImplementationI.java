package fr.sorbonne_u.components.hem2025e1.equipments.fan;

import fr.sorbonne_u.exceptions.AssertionChecking;

// -----------------------------------------------------------------------------
/**
 * The interface <code>FanImplementationI</code> defines the signatures
 * of the services implemented by the fan component.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code OFF_POWER >= 0.0}
 * invariant	{@code LOW_POWER > 0.0}
 * invariant	{@code MEDIUM_POWER > LOW_POWER}
 * invariant	{@code HIGH_POWER > MEDIUM_POWER}
 * </pre>
 * 
 * <p>Created on : 2025-12-25</p>
 * 
 * @author	Softweavers
 */
public interface		FanImplementationI
{
	// -------------------------------------------------------------------------
	// Inner interfaces and types
	// -------------------------------------------------------------------------

	/**
	 * The enumeration <code>FanState</code> describes the operation
	 * states of the fan.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>Created on : 2025-12-25</p>
	 * 
	 * @author	Softweavers
	 */
	public static enum	FanState
	{
		/** fan is off.														*/
		OFF,
		/** fan is on at low speed.											*/
		LOW,
		/** fan is on at medium speed.										*/
		MEDIUM,
		/** fan is on at high speed.										*/
		HIGH
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** power consumption in OFF state (watts).								*/
	public static final double	OFF_POWER = 0.0;
	/** power consumption in LOW state (watts).								*/
	public static final double	LOW_POWER = 50.0;
	/** power consumption in MEDIUM state (watts).							*/
	public static final double	MEDIUM_POWER = 100.0;
	/** power consumption in HIGH state (watts).							*/
	public static final double	HIGH_POWER = 150.0;

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
				OFF_POWER >= 0.0,
				FanImplementationI.class,
				"OFF_POWER >= 0.0");
		ret &= AssertionChecking.checkStaticInvariant(
				LOW_POWER > 0.0,
				FanImplementationI.class,
				"LOW_POWER > 0.0");
		ret &= AssertionChecking.checkStaticInvariant(
				MEDIUM_POWER > LOW_POWER,
				FanImplementationI.class,
				"MEDIUM_POWER > LOW_POWER");
		ret &= AssertionChecking.checkStaticInvariant(
				HIGH_POWER > MEDIUM_POWER,
				FanImplementationI.class,
				"HIGH_POWER > MEDIUM_POWER");
		return ret;
	}

	// -------------------------------------------------------------------------
	// Component services signatures
	// -------------------------------------------------------------------------

	/**
	 * return the current state of the fan.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				the current state of the fan.
	 * @throws Exception 	<i>to do</i>.
	 */
	public FanState		getState() throws Exception;

	/**
	 * turn on the fan, put in the low speed mode.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code getState() == FanState.OFF}
	 * post	{@code getState() == FanState.LOW}
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void			turnOn() throws Exception;

	/**
	 * turn off the fan.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code getState() == FanState.OFF}
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void			turnOff() throws Exception;

	/**
	 * set the fan to low speed mode.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code getState() != FanState.OFF}
	 * post	{@code getState() == FanState.LOW}
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void			setLow() throws Exception;

	/**
	 * set the fan to medium speed mode.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code getState() != FanState.OFF}
	 * post	{@code getState() == FanState.MEDIUM}
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void			setMedium() throws Exception;

	/**
	 * set the fan to high speed mode.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code getState() != FanState.OFF}
	 * post	{@code getState() == FanState.HIGH}
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void			setHigh() throws Exception;

	/**
	 * return the current power consumption level of the fan.
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
	public double		getCurrentPowerLevel() throws Exception;
}
// -----------------------------------------------------------------------------
