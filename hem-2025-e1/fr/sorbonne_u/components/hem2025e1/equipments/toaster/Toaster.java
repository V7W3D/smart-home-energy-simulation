package fr.sorbonne_u.components.hem2025e1.equipments.toaster;

import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.hem2025e1.equipments.toaster.connections.ToasterInboundPort;
import fr.sorbonne_u.exceptions.PreconditionException;

// -----------------------------------------------------------------------------
/**
 * The class <code>Toaster</code> implements a toaster component.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The toaster is an uncontrollable appliance that cannot be managed by the
 * HEM energy manager. It is activated only by user action (pushDown) and
 * automatically stops after a maximum duration (2 minutes) or when the user
 * pulls up the lever (pullUp).
 * </p>
 * 
 * <p>
 * This component does NOT implement AdjustableCI as it cannot be controlled
 * by the energy manager. It creates unpredictable power consumption peaks
 * that the HEM must handle by adjusting other controllable appliances.
 * </p>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2025-12-26</p>
 * 
 * @author	Softweavers
 */
@OfferedInterfaces(offered = {ToasterUserCI.class})
public class			Toaster
extends		AbstractComponent
implements	ToasterImplementationI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** URI of the toaster inbound port.									*/
	public static final String	INBOUND_PORT_URI = "TOASTER-INBOUND-PORT-URI";
	/** when true, methods trace their actions.								*/
	public static boolean		VERBOSE = true;
	/** X position of the tracer window.									*/
	public static int			X_RELATIVE_POSITION = 0;
	/** Y position of the tracer window.									*/
	public static int			Y_RELATIVE_POSITION = 0;

	/** current state of the toaster.										*/
	protected ToasterState		currentState;
	/** inbound port offering the ToasterUserCI interface.					*/
	protected ToasterInboundPort	toasterInboundPort;
	/** time when toasting started (for auto pull up).						*/
	protected long				toastingStartTime;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a toaster component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code getState() == ToasterState.OFF}
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	protected			Toaster() throws Exception
	{
		super(1, 1);
		this.initialise(INBOUND_PORT_URI);
	}

	/**
	 * create a toaster component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code toasterInboundPortURI != null && !toasterInboundPortURI.isEmpty()}
	 * post	{@code getState() == ToasterState.OFF}
	 * </pre>
	 *
	 * @param toasterInboundPortURI	URI of the toaster inbound port.
	 * @throws Exception			<i>to do</i>.
	 */
	protected			Toaster(String toasterInboundPortURI) throws Exception
	{
		super(1, 1);

		assert	toasterInboundPortURI != null && !toasterInboundPortURI.isEmpty() :
				new PreconditionException(
						"toasterInboundPortURI != null && !toasterInboundPortURI.isEmpty()");

		this.initialise(toasterInboundPortURI);
	}

	/**
	 * create a toaster component with the given reflection inbound port URI.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code toasterInboundPortURI != null && !toasterInboundPortURI.isEmpty()}
	 * pre	{@code reflectionInboundPortURI != null && !reflectionInboundPortURI.isEmpty()}
	 * post	{@code getState() == ToasterState.OFF}
	 * </pre>
	 *
	 * @param reflectionInboundPortURI	URI of the reflection inbound port.
	 * @param toasterInboundPortURI		URI of the toaster inbound port.
	 * @throws Exception				<i>to do</i>.
	 */
	protected			Toaster(
		String reflectionInboundPortURI,
		String toasterInboundPortURI
		) throws Exception
	{
		super(reflectionInboundPortURI, 1, 1);

		assert	toasterInboundPortURI != null && !toasterInboundPortURI.isEmpty() :
				new PreconditionException(
						"toasterInboundPortURI != null && !toasterInboundPortURI.isEmpty()");

		this.initialise(toasterInboundPortURI);
	}

	/**
	 * initialise the toaster component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code toasterInboundPortURI != null && !toasterInboundPortURI.isEmpty()}
	 * post	{@code getState() == ToasterState.OFF}
	 * </pre>
	 *
	 * @param toasterInboundPortURI	URI of the toaster inbound port.
	 * @throws Exception			<i>to do</i>.
	 */
	protected void		initialise(String toasterInboundPortURI) throws Exception
	{
		assert	toasterInboundPortURI != null && !toasterInboundPortURI.isEmpty() :
				new PreconditionException(
						"toasterInboundPortURI != null && !toasterInboundPortURI.isEmpty()");

		this.currentState = ToasterState.OFF;
		this.toasterInboundPort = new ToasterInboundPort(toasterInboundPortURI, this);
		this.toasterInboundPort.publishPort();

		if (VERBOSE) {
			this.tracer.get().setTitle("Toaster component");
			this.tracer.get().setRelativePosition(X_RELATIVE_POSITION,
												  Y_RELATIVE_POSITION);
			this.toggleTracing();
		}
	}

	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			// Invalidate any pending auto pull up task
			this.toastingStartTime = 0;
			this.toasterInboundPort.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}

	// -------------------------------------------------------------------------
	// Component services implementation
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.toaster.ToasterImplementationI#getState()
	 */
	@Override
	public ToasterState	getState() throws Exception
	{
		if (VERBOSE) {
			this.traceMessage("[Toaster] : getState() -> " + this.currentState + "\n");
		}
		return this.currentState;
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.toaster.ToasterImplementationI#pushDown()
	 */
	@Override
	public void			pushDown() throws Exception
	{
		assert	this.getState() == ToasterState.OFF :
				new PreconditionException("getState() == ToasterState.OFF");

		if (VERBOSE) {
			this.traceMessage("[Toaster] : Début de la chauffe (" + 
					(int)TOASTING_POWER + "W)\n");
		}

		this.currentState = ToasterState.TOASTING;
		this.toastingStartTime = System.currentTimeMillis();

		// Schedule automatic pull up after MAX_TOASTING_DURATION_SECONDS
		final long startTime = this.toastingStartTime;
		this.scheduleTask(
			o -> {
				try {
					// Only auto pull up if still toasting from the same start time
					if (this.currentState == ToasterState.TOASTING &&
							this.toastingStartTime == startTime) {
						this.traceMessage("[Toaster] : Fin automatique de la chauffe " +
								"(durée maximale atteinte)\n");
						this.currentState = ToasterState.OFF;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			},
			MAX_TOASTING_DURATION_SECONDS,
			TimeUnit.SECONDS
		);
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.toaster.ToasterImplementationI#pullUp()
	 */
	@Override
	public void			pullUp() throws Exception
	{
		if (VERBOSE) {
			if (this.currentState == ToasterState.TOASTING) {
				this.traceMessage("[Toaster] : Arrêt de la chauffe (éjection du pain)\n");
			} else {
				this.traceMessage("[Toaster] : pullUp() - déjà éteint\n");
			}
		}

		// Reset toasting start time to invalidate any pending auto pull up task
		this.toastingStartTime = 0;
		this.currentState = ToasterState.OFF;
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.toaster.ToasterImplementationI#getCurrentPowerLevel()
	 */
	@Override
	public double		getCurrentPowerLevel() throws Exception
	{
		double power;
		switch (this.currentState) {
			case TOASTING:
				power = TOASTING_POWER;
				break;
			case OFF:
			default:
				power = OFF_POWER;
				break;
		}

		if (VERBOSE) {
			this.traceMessage("[Toaster] : getCurrentPowerLevel() -> " + power + "W\n");
		}

		return power;
	}
}
// -----------------------------------------------------------------------------
