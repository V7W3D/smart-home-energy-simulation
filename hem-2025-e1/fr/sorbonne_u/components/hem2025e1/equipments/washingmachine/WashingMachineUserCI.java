package fr.sorbonne_u.components.hem2025e1.equipments.washingmachine;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

/**
 * The component interface <code>WashingMachineUserCI</code> defines the signatures
 * of the services offered by the washing machine component to user components.
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
public interface		WashingMachineUserCI
extends		OfferedCI,
			RequiredCI,
			WashingMachineImplementationI
{
	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.washingmachine.WashingMachineImplementationI#getState()
	 */
	@Override
	public WashingMachineState	getState() throws Exception;

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.washingmachine.WashingMachineImplementationI#turnOn()
	 */
	@Override
	public void					turnOn() throws Exception;

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.washingmachine.WashingMachineImplementationI#turnOff()
	 */
	@Override
	public void					turnOff() throws Exception;

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.washingmachine.WashingMachineImplementationI#getSelectedProgram()
	 */
	@Override
	public WashProgram			getSelectedProgram() throws Exception;

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.washingmachine.WashingMachineImplementationI#selectProgram(WashProgram)
	 */
	@Override
	public void					selectProgram(WashProgram program) throws Exception;

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.washingmachine.WashingMachineImplementationI#startCycle()
	 */
	@Override
	public void					startCycle() throws Exception;

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.washingmachine.WashingMachineImplementationI#scheduleCycle(int)
	 */
	@Override
	public void					scheduleCycle(int delayMinutes) throws Exception;

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.washingmachine.WashingMachineImplementationI#getScheduledDelay()
	 */
	@Override
	public int					getScheduledDelay() throws Exception;

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.washingmachine.WashingMachineImplementationI#cancelCycle()
	 */
	@Override
	public void					cancelCycle() throws Exception;

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.washingmachine.WashingMachineImplementationI#suspendCycle()
	 */
	@Override
	public void					suspendCycle() throws Exception;

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.washingmachine.WashingMachineImplementationI#resumeCycle()
	 */
	@Override
	public void					resumeCycle() throws Exception;

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.washingmachine.WashingMachineImplementationI#isSuspended()
	 */
	@Override
	public boolean				isSuspended() throws Exception;

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.washingmachine.WashingMachineImplementationI#getCurrentPowerLevel()
	 */
	@Override
	public double				getCurrentPowerLevel() throws Exception;
}
