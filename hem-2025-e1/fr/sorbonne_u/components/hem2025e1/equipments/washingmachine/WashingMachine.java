package fr.sorbonne_u.components.hem2025e1.equipments.washingmachine;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2025e1.equipments.washingmachine.connections.WashingMachineInboundPort;
import fr.sorbonne_u.components.hem2025e1.equipments.hem.HEM;
import fr.sorbonne_u.components.hem2025e1.equipments.hem.HEMRegistrationCI;
import fr.sorbonne_u.components.hem2025e1.equipments.hem.HEMRegistrationOutboundPort;
import fr.sorbonne_u.components.hem2025e1.equipments.hem.HEMRegistrationConnector;
import fr.sorbonne_u.exceptions.PreconditionException;

/**
 * The class <code>WashingMachine</code> implements a programmable washing machine
 * component with multiple programs and deferred start capability.
 *
 * <p>Created on : 2025-12-29</p>
 * 
 * @author	Softweavers
 */
@OfferedInterfaces(offered={WashingMachineUserCI.class})
@RequiredInterfaces(required={HEMRegistrationCI.class})
public class			WashingMachine
extends		AbstractComponent
implements	WashingMachineImplementationI
{
	public static final String	INBOUND_PORT_URI = "WASHINGMACHINE-INBOUND-PORT-URI";
	public static final String	APPLIANCE_ID = "WashingMachine";
	public static final String	XML_DESCRIPTOR_PATH = "hem-adapter/washingmachine-config.xml";
	public static boolean		VERBOSE = false;
	public static int			X_RELATIVE_POSITION = 0;
	public static int			Y_RELATIVE_POSITION = 0;

	protected WashingMachineState		currentState;
	protected WashProgram				selectedProgram;
	protected int						scheduledDelay;
	protected WashingMachineInboundPort	wmip;
	protected HEMRegistrationOutboundPort	hemRegistrationPort;

	protected			WashingMachine() throws Exception
	{
		super(1, 0);
		this.initialise(INBOUND_PORT_URI);
	}

	protected			WashingMachine(String washingMachineInboundPortURI) throws Exception
	{
		super(1, 0);
		this.initialise(washingMachineInboundPortURI);
	}

	protected			WashingMachine(String reflectionInboundPortURI, String washingMachineInboundPortURI)
	throws Exception
	{
		super(reflectionInboundPortURI, 1, 0);
		this.initialise(washingMachineInboundPortURI);
	}

	protected void		initialise(String washingMachineInboundPortURI) throws Exception
	{
		assert	washingMachineInboundPortURI != null && !washingMachineInboundPortURI.isEmpty() :
				new PreconditionException("washingMachineInboundPortURI != null && !washingMachineInboundPortURI.isEmpty()");

		this.currentState = WashingMachineState.OFF;
		this.selectedProgram = WashProgram.STANDARD;
		this.scheduledDelay = 0;
		this.wmip = new WashingMachineInboundPort(washingMachineInboundPortURI, this);
		this.wmip.publishPort();

		if (WashingMachine.VERBOSE) {
			this.tracer.get().setTitle("WashingMachine component");
			this.tracer.get().setRelativePosition(X_RELATIVE_POSITION, Y_RELATIVE_POSITION);
			this.toggleTracing();
		}
	}

	@Override
	public synchronized void	start() throws ComponentStartException
	{
		super.start();
		
		try {
			// Create outbound port to HEM registration service
			this.hemRegistrationPort = new HEMRegistrationOutboundPort(this);
			this.hemRegistrationPort.publishPort();
			
			// Connect to HEM
			this.doPortConnection(
					this.hemRegistrationPort.getPortURI(),
					HEM.REGISTRATION_INBOUND_PORT_URI,
					HEMRegistrationConnector.class.getCanonicalName());
			
			if (WashingMachine.VERBOSE) {
				this.traceMessage("WashingMachine connected to HEM registration service.\n");
			}
		} catch (Exception e) {
			throw new ComponentStartException(e);
		}
	}

	@Override
	public synchronized void	execute() throws Exception
	{
		// Register with HEM after all components have started
		this.hemRegistrationPort.register(APPLIANCE_ID, INBOUND_PORT_URI, XML_DESCRIPTOR_PATH);
		
		if (WashingMachine.VERBOSE) {
			this.traceMessage("WashingMachine registered with HEM.\n");
		}
	}

	@Override
	public synchronized void	finalise() throws Exception
	{
		this.doPortDisconnection(this.hemRegistrationPort.getPortURI());
		super.finalise();
	}

	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.hemRegistrationPort.unpublishPort();
			this.wmip.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}

	@Override
	public WashingMachineState	getState() throws Exception
	{
		if (WashingMachine.VERBOSE) {
			this.traceMessage("WashingMachine returns its state: " + this.currentState + ".\n");
		}
		return this.currentState;
	}

	@Override
	public void			turnOn() throws Exception
	{
		if (WashingMachine.VERBOSE) {
			this.traceMessage("WashingMachine is turned on.\n");
		}
		assert	this.currentState == WashingMachineState.OFF :
				new PreconditionException("getState() == WashingMachineState.OFF");
		this.currentState = WashingMachineState.IDLE;
		this.selectedProgram = WashProgram.STANDARD;
	}

	@Override
	public void			turnOff() throws Exception
	{
		if (WashingMachine.VERBOSE) {
			this.traceMessage("WashingMachine is turned off.\n");
		}
		assert	this.currentState != WashingMachineState.OFF :
				new PreconditionException("getState() != WashingMachineState.OFF");
		this.currentState = WashingMachineState.OFF;
		this.scheduledDelay = 0;
	}

	@Override
	public WashProgram	getSelectedProgram() throws Exception
	{
		if (WashingMachine.VERBOSE) {
			this.traceMessage("WashingMachine returns selected program: " + this.selectedProgram + ".\n");
		}
		return this.selectedProgram;
	}

	@Override
	public void			selectProgram(WashProgram program) throws Exception
	{
		assert	this.currentState == WashingMachineState.IDLE :
				new PreconditionException("getState() == WashingMachineState.IDLE");
		if (WashingMachine.VERBOSE) {
			this.traceMessage("WashingMachine selects program: " + program + ".\n");
		}
		this.selectedProgram = program;
	}

	@Override
	public void			startCycle() throws Exception
	{
		assert	this.currentState == WashingMachineState.IDLE :
				new PreconditionException("getState() == WashingMachineState.IDLE");
		if (WashingMachine.VERBOSE) {
			this.traceMessage("WashingMachine starts cycle with program: " + this.selectedProgram + ".\n");
		}
		this.currentState = WashingMachineState.RUNNING;
	}

	@Override
	public void			scheduleCycle(int delayMinutes) throws Exception
	{
		assert	this.currentState == WashingMachineState.IDLE :
				new PreconditionException("getState() == WashingMachineState.IDLE");
		assert	delayMinutes > 0 :
				new PreconditionException("delayMinutes > 0");
		if (WashingMachine.VERBOSE) {
			this.traceMessage("WashingMachine schedules cycle in " + delayMinutes + " minutes.\n");
		}
		this.scheduledDelay = delayMinutes;
		this.currentState = WashingMachineState.SCHEDULED;
	}

	@Override
	public int			getScheduledDelay() throws Exception
	{
		return this.scheduledDelay;
	}

	@Override
	public void			cancelCycle() throws Exception
	{
		assert	this.currentState == WashingMachineState.RUNNING ||
				this.currentState == WashingMachineState.SCHEDULED ||
				this.currentState == WashingMachineState.SUSPENDED :
				new PreconditionException("state is RUNNING, SCHEDULED, or SUSPENDED");
		if (WashingMachine.VERBOSE) {
			this.traceMessage("WashingMachine cancels cycle.\n");
		}
		this.currentState = WashingMachineState.IDLE;
		this.scheduledDelay = 0;
	}

	@Override
	public void			suspendCycle() throws Exception
	{
		assert	this.currentState == WashingMachineState.RUNNING :
				new PreconditionException("getState() == WashingMachineState.RUNNING");
		if (WashingMachine.VERBOSE) {
			this.traceMessage("WashingMachine suspends cycle.\n");
		}
		this.currentState = WashingMachineState.SUSPENDED;
	}

	@Override
	public void			resumeCycle() throws Exception
	{
		assert	this.currentState == WashingMachineState.SUSPENDED :
				new PreconditionException("getState() == WashingMachineState.SUSPENDED");
		if (WashingMachine.VERBOSE) {
			this.traceMessage("WashingMachine resumes cycle.\n");
		}
		this.currentState = WashingMachineState.RUNNING;
	}

	@Override
	public boolean		isSuspended() throws Exception
	{
		return this.currentState == WashingMachineState.SUSPENDED;
	}

	@Override
	public double		getCurrentPowerLevel() throws Exception
	{
		double power;
		switch (this.currentState) {
			case RUNNING: power = this.selectedProgram.getPowerWatts(); break;
			case SUSPENDED:
			case SCHEDULED:
			case IDLE:
			case OFF:
			default: power = 0.0;
		}
		if (WashingMachine.VERBOSE) {
			this.traceMessage("WashingMachine returns its current power level: " + power + " W.\n");
		}
		return power;
	}
}
