package fr.sorbonne_u.components.hem2025e1.equipments.refrigerator.connections;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2025e1.equipments.refrigerator.RefrigeratorImplementationI;
import fr.sorbonne_u.components.hem2025e1.equipments.refrigerator.RefrigeratorUserCI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.exceptions.PreconditionException;

/**
 * The class <code>RefrigeratorInboundPort</code> implements an inbound port for
 * the <code>RefrigeratorUserCI</code> component interface.
 *
 * <p>Created on : 2025-12-27</p>
 * 
 * @author	Softweavers
 */
public class			RefrigeratorInboundPort
extends		AbstractInboundPort
implements	RefrigeratorUserCI
{
	private static final long serialVersionUID = 1L;

	public				RefrigeratorInboundPort(ComponentI owner) throws Exception
	{
		super(RefrigeratorUserCI.class, owner);
		assert	owner instanceof RefrigeratorImplementationI :
				new PreconditionException("owner instanceof RefrigeratorImplementationI");
	}

	public				RefrigeratorInboundPort(String uri, ComponentI owner) throws Exception
	{
		super(uri, RefrigeratorUserCI.class, owner);
		assert	owner instanceof RefrigeratorImplementationI :
				new PreconditionException("owner instanceof RefrigeratorImplementationI");
	}

	@Override
	public RefrigeratorState	getState() throws Exception
	{
		return this.getOwner().handleRequest(
				o -> ((RefrigeratorImplementationI)o).getState());
	}

	@Override
	public void			turnOn() throws Exception
	{
		this.getOwner().handleRequest(
				o -> { ((RefrigeratorImplementationI)o).turnOn(); return null; });
	}

	@Override
	public void			turnOff() throws Exception
	{
		this.getOwner().handleRequest(
				o -> { ((RefrigeratorImplementationI)o).turnOff(); return null; });
	}

	@Override
	public double		getCurrentTemperature() throws Exception
	{
		return this.getOwner().handleRequest(
				o -> ((RefrigeratorImplementationI)o).getCurrentTemperature());
	}

	@Override
	public double		getTargetTemperature() throws Exception
	{
		return this.getOwner().handleRequest(
				o -> ((RefrigeratorImplementationI)o).getTargetTemperature());
	}

	@Override
	public void			setTargetTemperature(double target) throws Exception
	{
		this.getOwner().handleRequest(
				o -> { ((RefrigeratorImplementationI)o).setTargetTemperature(target); return null; });
	}

	@Override
	public void			suspendCompressor() throws Exception
	{
		this.getOwner().handleRequest(
				o -> { ((RefrigeratorImplementationI)o).suspendCompressor(); return null; });
	}

	@Override
	public void			resumeCompressor() throws Exception
	{
		this.getOwner().handleRequest(
				o -> { ((RefrigeratorImplementationI)o).resumeCompressor(); return null; });
	}

	@Override
	public boolean		isCompressorSuspended() throws Exception
	{
		return this.getOwner().handleRequest(
				o -> ((RefrigeratorImplementationI)o).isCompressorSuspended());
	}

	@Override
	public double		getCurrentPowerLevel() throws Exception
	{
		return this.getOwner().handleRequest(
				o -> ((RefrigeratorImplementationI)o).getCurrentPowerLevel());
	}
}
