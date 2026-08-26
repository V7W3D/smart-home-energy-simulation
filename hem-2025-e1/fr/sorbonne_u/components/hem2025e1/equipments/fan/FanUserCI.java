package fr.sorbonne_u.components.hem2025e1.equipments.fan;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

/**
 * The component interface <code>FanUserCI</code> defines the signatures
 * of the services offered by the fan component to user components.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2025-12-25</p>
 * 
 * @author	Softweavers
 */
public interface		FanUserCI
extends		OfferedCI,
			RequiredCI,
			FanImplementationI
{
	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.fan.FanImplementationI#getState()
	 */
	@Override
	public FanState		getState() throws Exception;

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.fan.FanImplementationI#turnOn()
	 */
	@Override
	public void			turnOn() throws Exception;

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.fan.FanImplementationI#turnOff()
	 */
	@Override
	public void			turnOff() throws Exception;

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.fan.FanImplementationI#setLow()
	 */
	@Override
	public void			setLow() throws Exception;

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.fan.FanImplementationI#setMedium()
	 */
	@Override
	public void			setMedium() throws Exception;

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.fan.FanImplementationI#setHigh()
	 */
	@Override
	public void			setHigh() throws Exception;

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.fan.FanImplementationI#getCurrentPowerLevel()
	 */
	@Override
	public double		getCurrentPowerLevel() throws Exception;
}
