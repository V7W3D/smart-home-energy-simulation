package fr.sorbonne_u.components.hem2025e1.equipments.toaster.connections;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2025e1.equipments.toaster.ToasterImplementationI;
import fr.sorbonne_u.components.hem2025e1.equipments.toaster.ToasterUserCI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

// -----------------------------------------------------------------------------
/**
 * The class <code>ToasterInboundPort</code> implements an inbound port for
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
public class			ToasterInboundPort
extends		AbstractInboundPort
implements	ToasterUserCI
{
	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create an inbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code owner instanceof ToasterImplementationI}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param owner			component that owns this port.
	 * @throws Exception	<i>to do</i>.
	 */
	public				ToasterInboundPort(ComponentI owner) throws Exception
	{
		super(ToasterUserCI.class, owner);
	}

	/**
	 * create an inbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code owner instanceof ToasterImplementationI}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param uri			unique identifier of the port.
	 * @param owner			component that owns this port.
	 * @throws Exception	<i>to do</i>.
	 */
	public				ToasterInboundPort(String uri, ComponentI owner)
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
		return this.getOwner().handleRequest(
				o -> ((ToasterImplementationI)o).getState());
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.toaster.ToasterUserCI#pushDown()
	 */
	@Override
	public void			pushDown() throws Exception
	{
		this.getOwner().handleRequest(
				o -> { ((ToasterImplementationI)o).pushDown(); return null; });
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.toaster.ToasterUserCI#pullUp()
	 */
	@Override
	public void			pullUp() throws Exception
	{
		this.getOwner().handleRequest(
				o -> { ((ToasterImplementationI)o).pullUp(); return null; });
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.toaster.ToasterUserCI#getCurrentPowerLevel()
	 */
	@Override
	public double		getCurrentPowerLevel() throws Exception
	{
		return this.getOwner().handleRequest(
				o -> ((ToasterImplementationI)o).getCurrentPowerLevel());
	}
}
// -----------------------------------------------------------------------------
