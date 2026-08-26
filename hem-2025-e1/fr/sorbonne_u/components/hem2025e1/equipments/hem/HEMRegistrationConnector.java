package fr.sorbonne_u.components.hem2025e1.equipments.hem;

import fr.sorbonne_u.components.connectors.AbstractConnector;

// -----------------------------------------------------------------------------
/**
 * The class <code>HEMRegistrationConnector</code> implements a connector for
 * the {@code HEMRegistrationCI} component interface.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>Created on : 2025-12-26</p>
 * 
 * @author	Softweavers
 */
public class			HEMRegistrationConnector
extends		AbstractConnector
implements	HEMRegistrationCI
{
	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.hem.HEMRegistrationCI#register(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void			register(String id, String controlPortURI, String xmlDescriptorPath)
	throws Exception
	{
		((HEMRegistrationCI)this.offering).register(id, controlPortURI, xmlDescriptorPath);
	}
}
// -----------------------------------------------------------------------------
