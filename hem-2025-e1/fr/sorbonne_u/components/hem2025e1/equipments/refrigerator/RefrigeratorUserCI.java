package fr.sorbonne_u.components.hem2025e1.equipments.refrigerator;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

/**
 * The component interface <code>RefrigeratorUserCI</code> defines the signatures
 * of the services offered by the refrigerator component to user components.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2025-12-27</p>
 * 
 * @author	Softweavers
 */
public interface		RefrigeratorUserCI
extends		OfferedCI,
			RequiredCI,
			RefrigeratorImplementationI
{
	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.refrigerator.RefrigeratorImplementationI#getState()
	 */
	@Override
	public RefrigeratorState	getState() throws Exception;

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.refrigerator.RefrigeratorImplementationI#turnOn()
	 */
	@Override
	public void					turnOn() throws Exception;

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.refrigerator.RefrigeratorImplementationI#turnOff()
	 */
	@Override
	public void					turnOff() throws Exception;

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.refrigerator.RefrigeratorImplementationI#getCurrentTemperature()
	 */
	@Override
	public double				getCurrentTemperature() throws Exception;

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.refrigerator.RefrigeratorImplementationI#getTargetTemperature()
	 */
	@Override
	public double				getTargetTemperature() throws Exception;

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.refrigerator.RefrigeratorImplementationI#setTargetTemperature(double)
	 */
	@Override
	public void					setTargetTemperature(double target) throws Exception;

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.refrigerator.RefrigeratorImplementationI#suspendCompressor()
	 */
	@Override
	public void					suspendCompressor() throws Exception;

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.refrigerator.RefrigeratorImplementationI#resumeCompressor()
	 */
	@Override
	public void					resumeCompressor() throws Exception;

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.refrigerator.RefrigeratorImplementationI#isCompressorSuspended()
	 */
	@Override
	public boolean				isCompressorSuspended() throws Exception;

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.refrigerator.RefrigeratorImplementationI#getCurrentPowerLevel()
	 */
	@Override
	public double				getCurrentPowerLevel() throws Exception;
}
