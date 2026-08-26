package fr.sorbonne_u.components.hem2025e1.equipments.refrigerator;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2025e1.equipments.refrigerator.connections.RefrigeratorInboundPort;
import fr.sorbonne_u.components.hem2025e1.equipments.hem.HEM;
import fr.sorbonne_u.components.hem2025e1.equipments.hem.HEMRegistrationCI;
import fr.sorbonne_u.components.hem2025e1.equipments.hem.HEMRegistrationOutboundPort;
import fr.sorbonne_u.components.hem2025e1.equipments.hem.HEMRegistrationConnector;
import fr.sorbonne_u.exceptions.PreconditionException;

/**
 * The class <code>Refrigerator</code> implements a refrigerator component
 * with thermostat control and a suspendable compressor.
 *
 * <p>Created on : 2025-12-27</p>
 * 
 * @author	Softweavers
 */
@OfferedInterfaces(offered={RefrigeratorUserCI.class})
@RequiredInterfaces(required={HEMRegistrationCI.class})
public class			Refrigerator
extends		AbstractComponent
implements	RefrigeratorImplementationI
{
	public static final String	INBOUND_PORT_URI = "REFRIGERATOR-INBOUND-PORT-URI";
	public static final String	APPLIANCE_ID = "Refrigerator";
	public static final String	XML_DESCRIPTOR_PATH = "hem-adapter/refrigerator-config.xml";
	public static boolean		VERBOSE = false;
	public static int			X_RELATIVE_POSITION = 0;
	public static int			Y_RELATIVE_POSITION = 0;

	protected RefrigeratorState				currentState;
	protected double						currentTemperature;
	protected double						targetTemperature;
	protected RefrigeratorInboundPort		rip;
	protected HEMRegistrationOutboundPort	hemRegistrationPort;

	protected			Refrigerator() throws Exception
	{
		super(1, 0);
		this.initialise(INBOUND_PORT_URI);
	}

	protected			Refrigerator(String refrigeratorInboundPortURI) throws Exception
	{
		super(1, 0);
		this.initialise(refrigeratorInboundPortURI);
	}

	protected			Refrigerator(String reflectionInboundPortURI, String refrigeratorInboundPortURI)
	throws Exception
	{
		super(reflectionInboundPortURI, 1, 0);
		this.initialise(refrigeratorInboundPortURI);
	}

	protected void		initialise(String refrigeratorInboundPortURI) throws Exception
	{
		assert	refrigeratorInboundPortURI != null && !refrigeratorInboundPortURI.isEmpty() :
				new PreconditionException("refrigeratorInboundPortURI != null && !refrigeratorInboundPortURI.isEmpty()");

		this.currentState = RefrigeratorState.OFF;
		this.currentTemperature = 5.0;
		this.targetTemperature = DEFAULT_TARGET_TEMPERATURE;
		this.rip = new RefrigeratorInboundPort(refrigeratorInboundPortURI, this);
		this.rip.publishPort();

		if (Refrigerator.VERBOSE) {
			this.tracer.get().setTitle("Refrigerator component");
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
			
			if (Refrigerator.VERBOSE) {
				this.traceMessage("Refrigerator connected to HEM registration service.\n");
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
		
		if (Refrigerator.VERBOSE) {
			this.traceMessage("Refrigerator registered with HEM.\n");
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
			this.rip.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}

	@Override
	public RefrigeratorState	getState() throws Exception
	{
		if (Refrigerator.VERBOSE) {
			this.traceMessage("Refrigerator returns its state: " + this.currentState + ".\n");
		}
		return this.currentState;
	}

	@Override
	public void			turnOn() throws Exception
	{
		if (Refrigerator.VERBOSE) {
			this.traceMessage("Refrigerator is turned on.\n");
		}
		assert	this.currentState == RefrigeratorState.OFF :
				new PreconditionException("getState() == RefrigeratorState.OFF");
		this.currentState = RefrigeratorState.ON;
	}

	@Override
	public void			turnOff() throws Exception
	{
		if (Refrigerator.VERBOSE) {
			this.traceMessage("Refrigerator is turned off.\n");
		}
		assert	this.currentState != RefrigeratorState.OFF :
				new PreconditionException("getState() != RefrigeratorState.OFF");
		this.currentState = RefrigeratorState.OFF;
	}

	@Override
	public double		getCurrentTemperature() throws Exception
	{
		if (Refrigerator.VERBOSE) {
			this.traceMessage("Refrigerator returns current temperature: " + this.currentTemperature + "°C.\n");
		}
		return this.currentTemperature;
	}

	@Override
	public double		getTargetTemperature() throws Exception
	{
		if (Refrigerator.VERBOSE) {
			this.traceMessage("Refrigerator returns target temperature: " + this.targetTemperature + "°C.\n");
		}
		return this.targetTemperature;
	}

	@Override
	public void			setTargetTemperature(double target) throws Exception
	{
		assert	target >= MIN_TEMPERATURE && target <= MAX_TEMPERATURE :
				new PreconditionException("target >= MIN_TEMPERATURE && target <= MAX_TEMPERATURE");
		if (Refrigerator.VERBOSE) {
			this.traceMessage("Refrigerator sets target temperature to: " + target + "°C.\n");
		}
		this.targetTemperature = target;
	}

	@Override
	public void			suspendCompressor() throws Exception
	{
		if (Refrigerator.VERBOSE) {
			this.traceMessage("Refrigerator compressor is suspended.\n");
		}
		assert	this.currentState == RefrigeratorState.ON :
				new PreconditionException("getState() == RefrigeratorState.ON");
		this.currentState = RefrigeratorState.SUSPENDED;
	}

	@Override
	public void			resumeCompressor() throws Exception
	{
		if (Refrigerator.VERBOSE) {
			this.traceMessage("Refrigerator compressor is resumed.\n");
		}
		assert	this.currentState == RefrigeratorState.SUSPENDED :
				new PreconditionException("getState() == RefrigeratorState.SUSPENDED");
		this.currentState = RefrigeratorState.ON;
	}

	@Override
	public boolean		isCompressorSuspended() throws Exception
	{
		return this.currentState == RefrigeratorState.SUSPENDED;
	}

	@Override
	public double		getCurrentPowerLevel() throws Exception
	{
		double power;
		switch (this.currentState) {
			case OFF: power = OFF_POWER; break;
			case ON: power = COMPRESSOR_POWER; break;
			case SUSPENDED: power = STANDBY_POWER; break;
			default: power = 0.0;
		}
		if (Refrigerator.VERBOSE) {
			this.traceMessage("Refrigerator returns its current power level: " + power + " W.\n");
		}
		return power;
	}
}
