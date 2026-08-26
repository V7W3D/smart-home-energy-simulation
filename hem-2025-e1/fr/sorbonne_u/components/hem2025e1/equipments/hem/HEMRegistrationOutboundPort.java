package fr.sorbonne_u.components.hem2025e1.equipments.hem;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

// -----------------------------------------------------------------------------
/**
 * The class <code>HEMRegistrationOutboundPort</code> implements an outbound port
 * for the {@code HEMRegistrationCI} component interface.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>Created on : 2025-12-26</p>
 * 
 * @author	Softweavers
 */
public class			HEMRegistrationOutboundPort
extends		AbstractOutboundPort
implements	HEMRegistrationCI
{
	private static final long serialVersionUID = 1L;

	/**
	 * create a port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code owner != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param owner			component owning this port.
	 * @throws Exception	<i>to do</i>.
	 */
	public				HEMRegistrationOutboundPort(ComponentI owner)
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
	 * pre	{@code owner != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param uri			URI of the port.
	 * @param owner			component owning this port.
	 * @throws Exception	<i>to do</i>.
	 */
	public				HEMRegistrationOutboundPort(String uri, ComponentI owner)
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
		((HEMRegistrationCI)this.getConnector()).register(id, controlPortURI, xmlDescriptorPath);
	}
}
// -----------------------------------------------------------------------------
