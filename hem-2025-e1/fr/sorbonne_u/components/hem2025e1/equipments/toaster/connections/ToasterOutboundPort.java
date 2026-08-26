package fr.sorbonne_u.components.hem2025e1.equipments.toaster.connections;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2025e1.equipments.toaster.ToasterUserCI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

// -----------------------------------------------------------------------------
/**
 * The class <code>ToasterOutboundPort</code> implements an outbound port for
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
public class			ToasterOutboundPort
extends		AbstractOutboundPort
implements	ToasterUserCI
{
	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create an outbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param owner			component that owns this port.
	 * @throws Exception	<i>to do</i>.
	 */
	public				ToasterOutboundPort(ComponentI owner) throws Exception
	{
		super(ToasterUserCI.class, owner);
	}

	/**
	 * create an outbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param uri			unique identifier of the port.
	 * @param owner			component that owns this port.
	 * @throws Exception	<i>to do</i>.
	 */
	public				ToasterOutboundPort(String uri, ComponentI owner)
	throws Exception
	{
		super(uri, ToasterUserCI.class, owner);
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.toaster.ToasterUserCI#getState()
	 */
	@Override
	public ToasterState	getState() throws Exception
	{
		return ((ToasterUserCI)this.getConnector()).getState();
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.toaster.ToasterUserCI#pushDown()
	 */
	@Override
	public void			pushDown() throws Exception
	{
		((ToasterUserCI)this.getConnector()).pushDown();
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.toaster.ToasterUserCI#pullUp()
	 */
	@Override
	public void			pullUp() throws Exception
	{
		((ToasterUserCI)this.getConnector()).pullUp();
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.toaster.ToasterUserCI#getCurrentPowerLevel()
	 */
	@Override
	public double		getCurrentPowerLevel() throws Exception
	{
		return ((ToasterUserCI)this.getConnector()).getCurrentPowerLevel();
	}
}
// -----------------------------------------------------------------------------
