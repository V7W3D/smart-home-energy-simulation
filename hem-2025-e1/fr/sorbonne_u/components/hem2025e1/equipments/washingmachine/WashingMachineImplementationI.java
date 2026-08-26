package fr.sorbonne_u.components.hem2025e1.equipments.washingmachine;

import fr.sorbonne_u.exceptions.AssertionChecking;

// -----------------------------------------------------------------------------
/**
 * The interface <code>WashingMachineImplementationI</code> defines the signatures
 * of the services implemented by the washing machine component.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2025-12-29</p>
 * 
 * @author	Softweavers
 */
public interface		WashingMachineImplementationI
{
	// -------------------------------------------------------------------------
	// Inner interfaces and types
	// -------------------------------------------------------------------------

	/**
	 * The enumeration <code>WashingMachineState</code> describes the operation
	 * states of the washing machine.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>Created on : 2025-12-29</p>
	 * 
	 * @author	Softweavers
	 */
	public static enum	WashingMachineState
	{
		/** washing machine is off.											*/
		OFF,
		/** washing machine is on and idle, waiting for program selection.	*/
		IDLE,
		/** a deferred start has been programmed.							*/
		SCHEDULED,
		/** a wash cycle is in progress.									*/
		RUNNING,
		/** the wash cycle is temporarily paused by HEM.					*/
		SUSPENDED
	}

	/**
	 * The enumeration <code>WashProgram</code> describes the available
	 * wash programs with their duration and power consumption.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>Created on : 2025-12-29</p>
	 * 
	 * @author	Softweavers
	 */
	public static enum	WashProgram
	{
		/** quick wash: 30 minutes, 500W.									*/
		QUICK(30, 500.0),
		/** standard wash: 60 minutes, 1000W.								*/
		STANDARD(60, 1000.0),
		/** intensive wash: 90 minutes, 1500W.								*/
		INTENSIVE(90, 1500.0),
		/** eco wash: 120 minutes, 400W.									*/
		ECO(120, 400.0),
		/** delicate wash: 45 minutes, 300W.								*/
		DELICATE(45, 300.0);

		/** duration of the program in minutes.								*/
		private final int durationMinutes;
		/** power consumption of the program in watts.						*/
		private final double powerWatts;

		/**
		 * create a wash program with given duration and power.
		 * 
		 * @param duration	duration in minutes.
		 * @param power		power consumption in watts.
		 */
		WashProgram(int duration, double power)
		{
			this.durationMinutes = duration;
			this.powerWatts = power;
		}

		/**
		 * return the duration of the program in minutes.
		 * 
		 * @return	the duration in minutes.
		 */
		public int		getDurationMinutes() { return this.durationMinutes; }

		/**
		 * return the power consumption of the program in watts.
		 * 
		 * @return	the power consumption in watts.
		 */
		public double	getPowerWatts() { return this.powerWatts; }
	}

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
		for (WashProgram p : WashProgram.values()) {
			ret &= AssertionChecking.checkStaticInvariant(
					p.getDurationMinutes() > 0,
					WashingMachineImplementationI.class,
					p.name() + ".getDurationMinutes() > 0");
			ret &= AssertionChecking.checkStaticInvariant(
					p.getPowerWatts() > 0.0,
					WashingMachineImplementationI.class,
					p.name() + ".getPowerWatts() > 0.0");
		}
		return ret;
	}

	// -------------------------------------------------------------------------
	// Component services signatures
	// -------------------------------------------------------------------------

	/**
	 * return the current state of the washing machine.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				the current state of the washing machine.
	 * @throws Exception 	<i>to do</i>.
	 */
	public WashingMachineState	getState() throws Exception;

	/**
	 * turn on the washing machine.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code getState() == WashingMachineState.OFF}
	 * post	{@code getState() == WashingMachineState.IDLE}
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void					turnOn() throws Exception;

	/**
	 * turn off the washing machine.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code getState() == WashingMachineState.IDLE || getState() == WashingMachineState.OFF}
	 * post	{@code getState() == WashingMachineState.OFF}
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void					turnOff() throws Exception;

	/**
	 * return the currently selected wash program.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code getState() != WashingMachineState.OFF}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				the currently selected program, or null if none.
	 * @throws Exception 	<i>to do</i>.
	 */
	public WashProgram			getSelectedProgram() throws Exception;

	/**
	 * select a wash program.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code getState() == WashingMachineState.IDLE}
	 * pre	{@code program != null}
	 * post	{@code getSelectedProgram() == program}
	 * </pre>
	 *
	 * @param program		the program to select.
	 * @throws Exception	<i>to do</i>.
	 */
	public void					selectProgram(WashProgram program) throws Exception;

	/**
	 * start the wash cycle immediately.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code getState() == WashingMachineState.IDLE}
	 * pre	{@code getSelectedProgram() != null}
	 * post	{@code getState() == WashingMachineState.RUNNING}
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void					startCycle() throws Exception;

	/**
	 * schedule a deferred start of the wash cycle.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code getState() == WashingMachineState.IDLE}
	 * pre	{@code getSelectedProgram() != null}
	 * pre	{@code delayMinutes > 0}
	 * post	{@code getState() == WashingMachineState.SCHEDULED}
	 * </pre>
	 *
	 * @param delayMinutes	the delay in minutes before starting.
	 * @throws Exception	<i>to do</i>.
	 */
	public void					scheduleCycle(int delayMinutes) throws Exception;

	/**
	 * return the scheduled delay before the cycle starts.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code getState() == WashingMachineState.SCHEDULED}
	 * post	{@code return >= 0}
	 * </pre>
	 *
	 * @return				the remaining delay in minutes.
	 * @throws Exception 	<i>to do</i>.
	 */
	public int					getScheduledDelay() throws Exception;

	/**
	 * cancel the current or scheduled cycle.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code getState() == WashingMachineState.RUNNING || getState() == WashingMachineState.SCHEDULED || getState() == WashingMachineState.SUSPENDED}
	 * post	{@code getState() == WashingMachineState.IDLE}
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void					cancelCycle() throws Exception;

	/**
	 * suspend the current wash cycle (called by HEM for energy management).
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code getState() == WashingMachineState.RUNNING}
	 * post	{@code getState() == WashingMachineState.SUSPENDED}
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void					suspendCycle() throws Exception;

	/**
	 * resume the wash cycle after suspension.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code getState() == WashingMachineState.SUSPENDED}
	 * post	{@code getState() == WashingMachineState.RUNNING}
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void					resumeCycle() throws Exception;

	/**
	 * return true if the wash cycle is currently suspended.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				true if the cycle is suspended, false otherwise.
	 * @throws Exception 	<i>to do</i>.
	 */
	public boolean				isSuspended() throws Exception;

	/**
	 * return the current power consumption level of the washing machine.
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
