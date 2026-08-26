package fr.sorbonne_u.components.hem2025e1.equipments.toaster.connections;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.components.hem2025e1.equipments.toaster.ToasterUserCI;

// -----------------------------------------------------------------------------
/**
 * The class <code>ToasterConnector</code> implements a connector for
 * the {@code ToasterUserCI} component interface.
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
public class			ToasterConnector
extends		AbstractConnector
implements	ToasterUserCI
{
	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.toaster.ToasterUserCI#getState()
	 */
	@Override
	public ToasterState	getState() throws Exception
	{
		return ((ToasterUserCI)this.offering).getState();
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.toaster.ToasterUserCI#pushDown()
	 */
	@Override
	public void			pushDown() throws Exception
	{
		((ToasterUserCI)this.offering).pushDown();
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.toaster.ToasterUserCI#pullUp()
	 */
	@Override
	public void			pullUp() throws Exception
	{
		((ToasterUserCI)this.offering).pullUp();
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.toaster.ToasterUserCI#getCurrentPowerLevel()
	 */
	@Override
	public double		getCurrentPowerLevel() throws Exception
	{
		return ((ToasterUserCI)this.offering).getCurrentPowerLevel();
	}
}
// -----------------------------------------------------------------------------
