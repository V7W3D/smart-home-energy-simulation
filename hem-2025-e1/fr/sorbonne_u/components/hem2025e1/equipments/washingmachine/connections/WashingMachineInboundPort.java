package fr.sorbonne_u.components.hem2025e1.equipments.washingmachine.connections;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2025e1.equipments.washingmachine.WashingMachineImplementationI;
import fr.sorbonne_u.components.hem2025e1.equipments.washingmachine.WashingMachineUserCI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.exceptions.PreconditionException;

/**
 * The class <code>WashingMachineInboundPort</code> implements an inbound port for
 * the <code>WashingMachineUserCI</code> component interface.
 *
 * <p>Created on : 2025-12-29</p>
 * 
 * @author	Softweavers
 */
public class			WashingMachineInboundPort
extends		AbstractInboundPort
implements	WashingMachineUserCI
{
	private static final long serialVersionUID = 1L;

	public				WashingMachineInboundPort(ComponentI owner) throws Exception
	{
		super(WashingMachineUserCI.class, owner);
		assert	owner instanceof WashingMachineImplementationI :
				new PreconditionException("owner instanceof WashingMachineImplementationI");
	}

	public				WashingMachineInboundPort(String uri, ComponentI owner) throws Exception
	{
		super(uri, WashingMachineUserCI.class, owner);
		assert	owner instanceof WashingMachineImplementationI :
				new PreconditionException("owner instanceof WashingMachineImplementationI");
	}

	@Override
	public WashingMachineState	getState() throws Exception
	{
		return this.getOwner().handleRequest(
				o -> ((WashingMachineImplementationI)o).getState());
	}

	@Override
	public void			turnOn() throws Exception
	{
		this.getOwner().handleRequest(
				o -> { ((WashingMachineImplementationI)o).turnOn(); return null; });
	}

	@Override
	public void			turnOff() throws Exception
	{
		this.getOwner().handleRequest(
				o -> { ((WashingMachineImplementationI)o).turnOff(); return null; });
	}

	@Override
	public WashProgram	getSelectedProgram() throws Exception
	{
		return this.getOwner().handleRequest(
				o -> ((WashingMachineImplementationI)o).getSelectedProgram());
	}

	@Override
	public void			selectProgram(WashProgram program) throws Exception
	{
		this.getOwner().handleRequest(
				o -> { ((WashingMachineImplementationI)o).selectProgram(program); return null; });
	}

	@Override
	public void			startCycle() throws Exception
	{
		this.getOwner().handleRequest(
				o -> { ((WashingMachineImplementationI)o).startCycle(); return null; });
	}

	@Override
	public void			scheduleCycle(int delayMinutes) throws Exception
	{
		this.getOwner().handleRequest(
				o -> { ((WashingMachineImplementationI)o).scheduleCycle(delayMinutes); return null; });
	}

	@Override
	public int			getScheduledDelay() throws Exception
	{
		return this.getOwner().handleRequest(
				o -> ((WashingMachineImplementationI)o).getScheduledDelay());
	}

	@Override
	public void			cancelCycle() throws Exception
	{
		this.getOwner().handleRequest(
				o -> { ((WashingMachineImplementationI)o).cancelCycle(); return null; });
	}

	@Override
	public void			suspendCycle() throws Exception
	{
		this.getOwner().handleRequest(
				o -> { ((WashingMachineImplementationI)o).suspendCycle(); return null; });
	}

	@Override
	public void			resumeCycle() throws Exception
	{
		this.getOwner().handleRequest(
				o -> { ((WashingMachineImplementationI)o).resumeCycle(); return null; });
	}

	@Override
	public boolean		isSuspended() throws Exception
	{
		return this.getOwner().handleRequest(
				o -> ((WashingMachineImplementationI)o).isSuspended());
	}

	@Override
	public double		getCurrentPowerLevel() throws Exception
	{
		return this.getOwner().handleRequest(
				o -> ((WashingMachineImplementationI)o).getCurrentPowerLevel());
	}
}
