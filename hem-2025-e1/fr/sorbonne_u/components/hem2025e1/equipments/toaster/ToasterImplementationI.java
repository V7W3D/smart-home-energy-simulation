package fr.sorbonne_u.components.hem2025e1.equipments.toaster;

import fr.sorbonne_u.exceptions.AssertionChecking;

// -----------------------------------------------------------------------------
/**
 * The interface <code>ToasterImplementationI</code> defines the signatures
 * of the services implemented by the toaster component.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The toaster is an uncontrollable appliance that cannot be managed by the
 * HEM energy manager. It is activated only by user action and consumes a
 * significant amount of power (1000W) for a short period.
 * </p>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code TOASTING_POWER > 0.0}
 * invariant	{@code OFF_POWER >= 0.0}
 * invariant	{@code MAX_TOASTING_DURATION_SECONDS > 0}
 * </pre>
 * 
 * <p>Created on : 2025-12-26</p>
 * 
 * @author	Softweavers
 */
public interface		ToasterImplementationI
{
	// -------------------------------------------------------------------------
	// Inner interfaces and types
	// -------------------------------------------------------------------------

	/**
	 * The enumeration <code>ToasterState</code> describes the operation
	 * states of the toaster.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>Created on : 2025-12-26</p>
	 * 
	 * @author	Softweavers
	 */
	public static enum	ToasterState
	{
		/** toaster is off/idle, lever is up.								*/
		OFF,
		/** toaster is toasting, lever is down.								*/
		TOASTING
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** power consumption when off (watts).									*/
	public static final double	OFF_POWER = 0.0;
	/** power consumption when toasting (watts).							*/
	public static final double	TOASTING_POWER = 1000.0;
	/** maximum toasting duration in seconds (2 minutes).					*/
	public static final int		MAX_TOASTING_DURATION_SECONDS = 120;

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
				TOASTING_POWER > 0.0,
				ToasterImplementationI.class,
				"TOASTING_POWER > 0.0");
		ret &= AssertionChecking.checkStaticInvariant(
				OFF_POWER >= 0.0,
				ToasterImplementationI.class,
				"OFF_POWER >= 0.0");
		ret &= AssertionChecking.checkStaticInvariant(
				MAX_TOASTING_DURATION_SECONDS > 0,
				ToasterImplementationI.class,
				"MAX_TOASTING_DURATION_SECONDS > 0");
		return ret;
	}

	// -------------------------------------------------------------------------
	// Component services signatures
	// -------------------------------------------------------------------------

	/**
	 * return the current state of the toaster.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				the current state of the toaster.
	 * @throws Exception 	<i>to do</i>.
	 */
	public ToasterState	getState() throws Exception;

	/**
	 * push down the lever to start toasting.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code getState() == ToasterState.OFF}
	 * post	{@code getState() == ToasterState.TOASTING}
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void			pushDown() throws Exception;

	/**
	 * pull up the lever to stop toasting and eject the toast.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code getState() == ToasterState.OFF}
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void			pullUp() throws Exception;

	/**
	 * return the current power consumption level of the toaster.
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
