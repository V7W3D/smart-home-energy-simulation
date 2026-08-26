package fr.sorbonne_u.components.hem2025e1.equipments.hem;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

// -----------------------------------------------------------------------------
/**
 * The component interface <code>HEMRegistrationCI</code> defines the service
 * that allows appliances to register with the Home Energy Manager.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * This interface is used by appliances to register themselves with the HEM
 * during initialization. The HEM will generate a dynamic connector and establish
 * the connection.
 * </p>
 * 
 * <p>Created on : 2025-12-26</p>
 * 
 * @author	Softweavers
 */
public interface		HEMRegistrationCI
extends		OfferedCI,
			RequiredCI
{
	/**
	 * Register an appliance with the HEM.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code id != null && !id.isEmpty()}
	 * pre	{@code controlPortURI != null && !controlPortURI.isEmpty()}
	 * pre	{@code xmlDescriptorPath != null && !xmlDescriptorPath.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param id					unique identifier for the appliance
	 * @param controlPortURI		URI of the appliance's control inbound port
	 * @param xmlDescriptorPath		path to the XML descriptor file
	 * @throws Exception			if registration fails
	 */
	public void		register(String id, String controlPortURI, String xmlDescriptorPath) 
			throws Exception;
}
// -----------------------------------------------------------------------------
