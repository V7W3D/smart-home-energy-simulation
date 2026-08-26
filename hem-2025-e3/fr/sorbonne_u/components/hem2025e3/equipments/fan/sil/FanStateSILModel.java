package fr.sorbonne_u.components.hem2025e3.equipments.fan.sil;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide a basic
// household management systems as an example of a cyber-physical system.
//
// This software is governed by the CeCILL-C license under French law and
// abiding by the rules of distribution of free software.  You can use,
// modify and/ or redistribute the software under the terms of the
// CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
// URL "http://www.cecill.info".
//
// As a counterpart to the access to the source code and  rights to copy,
// modify and redistribute granted by the license, users are provided only
// with a limited warranty  and the software's author,  the holder of the
// economic rights,  and the successive licensors  have only  limited
// liability. 
//
// In this respect, the user's attention is drawn to the risks associated
// with loading,  using,  modifying and/or developing or reproducing the
// software by the user in light of its specific status of free software,
// that may mean  that it is complicated to manipulate,  and  that  also
// therefore means  that it is reserved for developers  and  experienced
// professionals having in-depth computer knowledge. Users are therefore
// encouraged to load and test the software's suitability as regards their
// requirements in conditions enabling the security of their systems and/or 
// data to be ensured and,  more generally, to use and operate it in the 
// same conditions as regards security. 
//
// The fact that you are presently reading this means that you have had
// knowledge of the CeCILL-C license and that you accept its terms.

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.FanImplementationI.FanState;
import fr.sorbonne_u.components.hem2025e2.GlobalReportI;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.FanOperationI;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.FanSimulationConfigurationI;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.AbstractFanEvent;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SetHighSpeed;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SetLowSpeed;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SetMediumSpeed;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SwitchOffFan;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SwitchOnFan;
import fr.sorbonne_u.components.hem2025e3.equipments.fan.FanCyPhy;
import fr.sorbonne_u.devs_simulation.exceptions.MissingRunParameterException;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import fr.sorbonne_u.exceptions.AssertionChecking;

// -----------------------------------------------------------------------------
/**
 * The class <code>FanStateSILModel</code> defines a simulation model
 * tracking the state changes on a fan.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The model receives event from the fan component (corresponding to
 * calls to operations on the fan in this component), keeps track of
 * the current state of the fan in the simulation and then emits the
 * received events again towards another model simulating the electricity
 * consumption of the fan given its current operating state.
 * </p>
 *
 * <ul>
 * <li>Imported events:
 *   {@code SwitchOnFan},
 *   {@code SwitchOffFan},
 *   {@code SetLowSpeed},
 *   {@code SetMediumSpeed},
 *   {@code SetHighSpeed}</li>
 * <li>Exported events:
 *   {@code SwitchOnFan},
 *   {@code SwitchOffFan},
 *   {@code SetLowSpeed},
 *   {@code SetMediumSpeed},
 *   {@code SetHighSpeed}</li>
 * <li>Imported variables: none</li>
 * <li>Exported variables: none</li>
 * </ul>
 * 
 * <p>Created on : 2026-01-10</p>
 * 
 * @author	Softweavers
 */
// -----------------------------------------------------------------------------
@ModelExternalEvents(
	imported = {SwitchOnFan.class,SwitchOffFan.class,
				SetLowSpeed.class,SetMediumSpeed.class,SetHighSpeed.class},
	exported = {SwitchOnFan.class,SwitchOffFan.class,
				SetLowSpeed.class,SetMediumSpeed.class,SetHighSpeed.class}
	)
// -----------------------------------------------------------------------------
public class			FanStateSILModel
extends		AtomicModel
implements	FanOperationI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	/** when true, leaves a trace of the execution of the model. */
	public static boolean			VERBOSE = true;
	/** when true, leaves a debugging trace of the execution of the model. */
	public static boolean			DEBUG = false;

	/** URI for an instance model; works as long as only one instance is
	 *  created. */
	public static final String		URI = FanStateSILModel.class.
										getSimpleName();

	/** current state (OFF, LOW, MEDIUM, HIGH) of the fan. */
	protected FanState				currentState = FanState.OFF;
	/** last received event or null if none. */
	protected AbstractFanEvent		lastReceived;
	/** time series of fan state samples. */
	protected ArrayList<String>		stateTrace;
	/** final report of the simulation run. */
	protected FanStateReport		finalReport;

	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------

	public static boolean	staticInvariants()
	{
		boolean ret = true;
		ret &= FanCyPhy.staticInvariants();
		ret &= FanSimulationConfigurationI.staticInvariants();
		ret &= AssertionChecking.checkStaticInvariant(
				URI != null && !URI.isEmpty(),
				FanStateSILModel.class,
				"URI != null && !URI.isEmpty()");
		return ret;
	}

	protected static boolean	implementationInvariants(
		FanStateSILModel instance
		)
	{
		assert	instance != null :
				new NeoSim4JavaException(
						"Precondition violation: instance != null");

		boolean ret = true;
		ret &= staticInvariants();
		return ret;
	}

	protected static boolean	invariants(FanStateSILModel instance)
	{
		assert	instance != null :
				new NeoSim4JavaException(
						"Precondition violation: instance != null");

		boolean ret = true;
		ret &= staticInvariants();
		return ret;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a fan state model instance.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code uri == null || !uri.isEmpty()}
	 * pre	{@code simulatedTimeUnit != null}
	 * pre	{@code simulationEngine == null || !simulationEngine.isModelSet()}
	 * pre	{@code simulationEngine == null || simulationEngine instanceof AtomicEngine}
	 * post	{@code !isDebugModeOn()}
	 * post	{@code getURI() != null && !getURI().isEmpty()}
	 * post	{@code uri == null || getURI().equals(uri)}
	 * post	{@code getSimulatedTimeUnit().equals(simulatedTimeUnit)}
	 * </pre>
	 *
	 * @param uri				URI of the model.
	 * @param simulatedTimeUnit	simulated time unit used in the model.
	 * @param simulationEngine	simulation engine enacting the model.
	 * @throws Exception			<i>to do</i>.
	 */
	public				FanStateSILModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		AtomicSimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);

		this.getSimulationEngine().setLogger(new StandardLogger());

		assert	FanStateSILModel.implementationInvariants(this) :
				new NeoSim4JavaException(
						"FanStateModel.implementationInvariants(this)");
		assert	FanStateSILModel.invariants(this) :
				new NeoSim4JavaException("FanStateModel.invariants(this)");
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.FanOperationI#turnOn()
	 */
	@Override
	public void			turnOn()
	{
		if (this.currentState == FanState.OFF) {
			this.currentState = FanState.LOW;
		}
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.FanOperationI#turnOff()
	 */
	@Override
	public void			turnOff()
	{
		if (this.currentState != FanState.OFF) {
			this.currentState = FanState.OFF;
		}
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.FanOperationI#setHigh()
	 */
	@Override
	public void			setHigh()
	{
		if (this.currentState != FanState.OFF
				&& this.currentState != FanState.HIGH) {
			this.currentState = FanState.HIGH;
		}
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.FanOperationI#setLow()
	 */
	@Override
	public void			setLow()
	{
		if (this.currentState != FanState.OFF
				&& this.currentState != FanState.LOW) {
			this.currentState = FanState.LOW;
		}
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.FanOperationI#setMedium()
	 */
	@Override
	public void			setMedium()
	{
		if (this.currentState != FanState.OFF
				&& this.currentState != FanState.MEDIUM) {
			this.currentState = FanState.MEDIUM;
		}
	}

	// -------------------------------------------------------------------------
	// DEVS simulation protocol
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time initialTime)
	{
		super.initialiseState(initialTime);

		this.lastReceived = null;
		this.currentState = FanState.OFF;
		this.stateTrace = new ArrayList<>();
		this.recordStateSample();

		if (VERBOSE) {
			this.logMessage("simulation begins.");
		}
	}

	@Override
	public ArrayList<EventI>	output()
	{
		assert	this.lastReceived != null :
				new NeoSim4JavaException("lastReceived != null");

		ArrayList<EventI> ret = new ArrayList<EventI>();
		ret.add(this.lastReceived);
		this.lastReceived = null;
		return ret;
	}

	@Override
	public Duration		timeAdvance()
	{
		if (this.lastReceived != null) {
			return Duration.zero(this.getSimulatedTimeUnit());
		} else {
			return Duration.INFINITY;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedExternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedExternalTransition(Duration elapsedTime)
	{
		super.userDefinedExternalTransition(elapsedTime);

		ArrayList<EventI> currentEvents = this.getStoredEventAndReset();
		assert	currentEvents != null && currentEvents.size() == 1 :
				new NeoSim4JavaException(
						"currentEvents != null && currentEvents.size() == 1");

		this.lastReceived = (AbstractFanEvent) currentEvents.get(0);
		this.lastReceived.executeOn(this);
		this.recordStateSample();

		if (VERBOSE) {
			StringBuffer message = new StringBuffer(this.uri);
			message.append(" executes the external event ");
			message.append(this.lastReceived);
			this.logMessage(message.toString());
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			endSimulation(Time endTime)
	{
		this.finalReport = new FanStateReport(this.getURI(),
									new ArrayList<>(this.stateTrace));
		if (VERBOSE) {
			this.logMessage("simulation ends.");
		}
		super.endSimulation(endTime);
	}

	// -------------------------------------------------------------------------
	// Optional DEVS simulation protocol: simulation run parameters
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#setSimulationRunParameters(java.util.Map)
	 */
	@Override
	public void			setSimulationRunParameters(
		Map<String, Object> simParams
		) throws MissingRunParameterException
	{
		super.setSimulationRunParameters(simParams);

		if (simParams.containsKey(
					AtomicSimulatorPlugin.OWNER_RUNTIME_PARAMETER_NAME)) {
			this.getSimulationEngine().setLogger(
						AtomicSimulatorPlugin.createComponentLogger(simParams));
		}
	}

	// -------------------------------------------------------------------------
	// Optional DEVS simulation protocol: simulation report
	// -------------------------------------------------------------------------

	public static class		FanStateReport
	implements	SimulationReportI, GlobalReportI
	{
		private static final long serialVersionUID = 1L;
		protected String		modelURI;
		protected ArrayList<String>	stateTrace;

		public			FanStateReport(
			String modelURI,
			ArrayList<String> stateTrace
			)
		{
			super();
			this.modelURI = modelURI;
			this.stateTrace = stateTrace;
		}

		@Override
		public String	getModelURI()
		{
			return this.modelURI;
		}

		@Override
		public String	printout(String indent)
		{
			StringBuffer ret = new StringBuffer(indent);
			ret.append("---\n");
			ret.append(indent);
			ret.append('|');
			ret.append(this.modelURI);
			ret.append(" report\n");
			ret.append(indent);
			ret.append('|');
			ret.append("fan mode curve (time, state)\n");
			for (int i = 0; i < this.stateTrace.size(); i++) {
				ret.append(indent);
				ret.append('|');
				ret.append("  ");
				ret.append(this.stateTrace.get(i));
				ret.append('\n');
			}
			ret.append(indent);
			ret.append("---\n");
			return ret.toString();
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#getFinalReport()
	 */
	@Override
	public SimulationReportI	getFinalReport()
	{
		if (this.finalReport != null) {
			return this.finalReport;
		}
		ArrayList<String> trace = this.stateTrace;
		if (trace == null) {
			trace = new ArrayList<>();
		}
		return new FanStateReport(this.getURI(), new ArrayList<>(trace));
	}

	// -------------------------------------------------------------------------
	// Internal helpers
	// -------------------------------------------------------------------------

	protected void		recordStateSample()
	{
		StringBuffer sample = new StringBuffer("t=");
		sample.append(this.getCurrentStateTime());
		sample.append(", state=");
		sample.append(this.currentState);
		this.stateTrace.add(sample.toString());
	}
}
// -----------------------------------------------------------------------------
