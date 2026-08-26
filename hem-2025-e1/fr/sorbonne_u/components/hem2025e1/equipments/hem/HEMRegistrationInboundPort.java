package fr.sorbonne_u.components.hem2025e1.equipments.hem;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

// -----------------------------------------------------------------------------
/**
 * The class <code>HEMRegistrationInboundPort</code> implements an inbound port
 * for the {@code HEMRegistrationCI} component interface.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>Created on : 2025-12-26</p>
 * 
 * @author	Softweavers
 */
public class			HEMRegistrationInboundPort
extends		AbstractInboundPort
implements	HEMRegistrationCI
{
	private static final long serialVersionUID = 1L;

	/**
	 * create a port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code owner instanceof HEM}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param owner			component owning this port.
	 * @throws Exception	<i>to do</i>.
	 */
	public				HEMRegistrationInboundPort(ComponentI owner)
	throws Exception
	{
		super(HEMRegistrationCI.class, owner);
	}

	/**
	 * create a port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code uri != null && !uri.isEmpty()}
	 * pre	{@code owner instanceof HEM}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param uri			URI of the port.
	 * @param owner			component owning this port.
	 * @throws Exception	<i>to do</i>.
	 */
	public				HEMRegistrationInboundPort(String uri, ComponentI owner)
	throws Exception
	{
		super(uri, HEMRegistrationCI.class, owner);
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.hem.HEMRegistrationCI#register(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void			register(String id, String controlPortURI, String xmlDescriptorPath)
	throws Exception
	{
		this.getOwner().handleRequest(
				o -> {	((HEM)o).register(id, controlPortURI, xmlDescriptorPath);
						return null;
				});
	}
}
// -----------------------------------------------------------------------------
