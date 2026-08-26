package fr.sorbonne_u.components.hem2025e2.equipments.fan.mil;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to implement a mock-up
// of household energy management system.
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
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ModelExportedVariables;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ModelExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import fr.sorbonne_u.exceptions.AssertionChecking;

// -----------------------------------------------------------------------------
/**
 * The class <code>FanMeterInputStubModel</code>.
 *
 * <p>Created on : 2026-01-10</p>
 *
 * @author	Softweavers
 */
@ModelExportedVariables(
	{@ModelExportedVariable(name = "batteriesInputPower", type = Double.class),
	 @ModelExportedVariable(name = "batteriesOutputPower", type = Double.class),
	 @ModelExportedVariable(name = "solarPanelOutputPower", type = Double.class),
	 @ModelExportedVariable(name = "generatorOutputPower", type = Double.class),
	 @ModelExportedVariable(name = "currentHeaterIntensity", type = Double.class),
	 @ModelExportedVariable(name = "currentHairDryerIntensity", type = Double.class),
	 @ModelExportedVariable(name = "currentRefrigeratorIntensity", type = Double.class)
	})
public class			FanMeterInputStubModel
extends		AtomicHIOA
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long	serialVersionUID = 1L;
	public static final String	URI = FanMeterInputStubModel.class.getSimpleName();
	public static boolean		VERBOSE = false;

	@ExportedVariable(type = Double.class)
	protected final Value<Double>	batteriesInputPower = new Value<Double>(this);
	@ExportedVariable(type = Double.class)
	protected final Value<Double>	batteriesOutputPower = new Value<Double>(this);
	@ExportedVariable(type = Double.class)
	protected final Value<Double>	solarPanelOutputPower = new Value<Double>(this);
	@ExportedVariable(type = Double.class)
	protected final Value<Double>	generatorOutputPower = new Value<Double>(this);
	@ExportedVariable(type = Double.class)
	protected final Value<Double>	currentHeaterIntensity = new Value<Double>(this);
	@ExportedVariable(type = Double.class)
	protected final Value<Double>	currentHairDryerIntensity = new Value<Double>(this);
	@ExportedVariable(type = Double.class)
	protected final Value<Double>	currentRefrigeratorIntensity = new Value<Double>(this);

	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------

	protected static boolean	implementationInvariants(FanMeterInputStubModel instance)
	{
		assert	instance != null :
				new NeoSim4JavaException("Precondition violation: instance != null");

		boolean ret = true;
		ret &= AssertionChecking.checkImplementationInvariant(
				instance.batteriesInputPower != null,
				FanMeterInputStubModel.class,
				instance,
				"batteriesInputPower != null");
		return ret;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public				FanMeterInputStubModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		AtomicSimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);

		this.getSimulationEngine().setLogger(new StandardLogger());

		assert	FanMeterInputStubModel.implementationInvariants(this) :
				new NeoSim4JavaException(
						"FanMeterInputStubModel.implementationInvariants(this)");
	}

	// -------------------------------------------------------------------------
	// DEVS simulation protocol
	// -------------------------------------------------------------------------

	@Override
	public void			initialiseVariables()
	{
		super.initialiseVariables();

		this.batteriesInputPower.initialise(0.0);
		this.batteriesOutputPower.initialise(0.0);
		this.solarPanelOutputPower.initialise(0.0);
		this.generatorOutputPower.initialise(0.0);
		this.currentHeaterIntensity.initialise(0.0);
		this.currentHairDryerIntensity.initialise(0.0);
		this.currentRefrigeratorIntensity.initialise(0.0);

		assert	FanMeterInputStubModel.implementationInvariants(this) :
				new NeoSim4JavaException(
						"FanMeterInputStubModel.implementationInvariants(this)");
	}

	@Override
	public ArrayList<EventI>	output()
	{
		return null;
	}

	@Override
	public Duration		timeAdvance()
	{
		return Duration.INFINITY;
	}

	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		super.userDefinedInternalTransition(elapsedTime);
	}

	@Override
	public void			endSimulation(Time endTime)
	{
		if (VERBOSE) {
			this.logMessage("simulation ends.");
		}
		super.endSimulation(endTime);
	}
}
// -----------------------------------------------------------------------------
