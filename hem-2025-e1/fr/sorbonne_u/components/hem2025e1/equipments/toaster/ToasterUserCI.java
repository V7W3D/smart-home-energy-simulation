package fr.sorbonne_u.components.hem2025e1.equipments.toaster;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

// -----------------------------------------------------------------------------
/**
 * The component interface <code>ToasterUserCI</code> defines the signatures
 * of the services offered by the toaster component to user components.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2025-12-26</p>
 * 
 * @author	Softweavers
 */
public interface		ToasterUserCI
extends		OfferedCI,
			RequiredCI,
			ToasterImplementationI
{
	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.toaster.ToasterImplementationI#getState()
	 */
	@Override
	public ToasterState	getState() throws Exception;

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.toaster.ToasterImplementationI#pushDown()
	 */
	@Override
	public void			pushDown() throws Exception;

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.toaster.ToasterImplementationI#pullUp()
	 */
	@Override
	public void			pullUp() throws Exception;

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.toaster.ToasterImplementationI#getCurrentPowerLevel()
	 */
	@Override
	public double		getCurrentPowerLevel() throws Exception;
}
// -----------------------------------------------------------------------------
