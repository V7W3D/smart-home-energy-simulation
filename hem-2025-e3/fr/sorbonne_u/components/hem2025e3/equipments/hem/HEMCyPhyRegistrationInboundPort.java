package fr.sorbonne_u.components.hem2025e3.equipments.hem;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2025e1.equipments.hem.HEMRegistrationCI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class			HEMCyPhyRegistrationInboundPort
extends		AbstractInboundPort
implements	HEMRegistrationCI
{
	private static final long serialVersionUID = 1L;

	public			HEMCyPhyRegistrationInboundPort(ComponentI owner)
	throws Exception
	{
		super(HEMRegistrationCI.class, owner);
	}

	public			HEMCyPhyRegistrationInboundPort(String uri, ComponentI owner)
	throws Exception
	{
		super(uri, HEMRegistrationCI.class, owner);
	}

	@Override
	public void			register(
		String id,
		String controlPortURI,
		String xmlDescriptorPath
		) throws Exception
	{
		this.getOwner().handleRequest(
			o -> {
				((HEMCyPhy) o).register(id, controlPortURI, xmlDescriptorPath);
				return null;
			});
	}
}
