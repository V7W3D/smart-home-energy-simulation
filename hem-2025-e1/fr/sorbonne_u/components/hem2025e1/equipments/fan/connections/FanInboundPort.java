package fr.sorbonne_u.components.hem2025e1.equipments.fan.connections;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.FanImplementationI;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.FanUserCI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.exceptions.PreconditionException;

/**
 * The class <code>FanInboundPort</code> implements an inbound port for
 * the <code>FanUserCI</code> component interface.
 *
 * <p>Created on : 2025-12-25</p>
 * 
 * @author	Softweavers
 */
public class			FanInboundPort
extends		AbstractInboundPort
implements	FanUserCI
{
	private static final long serialVersionUID = 1L;

	public				FanInboundPort(ComponentI owner) throws Exception
	{
		super(FanUserCI.class, owner);
		assert	owner instanceof FanImplementationI :
				new PreconditionException("owner instanceof FanImplementationI");
	}

	public				FanInboundPort(String uri, ComponentI owner) throws Exception
	{
		super(uri, FanUserCI.class, owner);
		assert	owner instanceof FanImplementationI :
				new PreconditionException("owner instanceof FanImplementationI");
	}

	@Override
	public FanState		getState() throws Exception
	{
		return this.getOwner().handleRequest(
				o -> ((FanImplementationI)o).getState());
	}

	@Override
	public void			turnOn() throws Exception
	{
		this.getOwner().handleRequest(
				o -> { ((FanImplementationI)o).turnOn(); return null; });
	}

	@Override
	public void			turnOff() throws Exception
	{
		this.getOwner().handleRequest(
				o -> { ((FanImplementationI)o).turnOff(); return null; });
	}

	@Override
	public void			setLow() throws Exception
	{
		this.getOwner().handleRequest(
				o -> { ((FanImplementationI)o).setLow(); return null; });
	}

	@Override
	public void			setMedium() throws Exception
	{
		this.getOwner().handleRequest(
				o -> { ((FanImplementationI)o).setMedium(); return null; });
	}

	@Override
	public void			setHigh() throws Exception
	{
		this.getOwner().handleRequest(
				o -> { ((FanImplementationI)o).setHigh(); return null; });
	}

	@Override
	public double		getCurrentPowerLevel() throws Exception
	{
		return this.getOwner().handleRequest(
				o -> ((FanImplementationI)o).getCurrentPowerLevel());
	}
}
