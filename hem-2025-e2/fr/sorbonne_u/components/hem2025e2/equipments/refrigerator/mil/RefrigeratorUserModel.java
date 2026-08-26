package fr.sorbonne_u.components.hem2025e2.equipments.refrigerator.mil;

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
import fr.sorbonne_u.components.hem2025e2.equipments.refrigerator.mil.events.CompressorActive;
import fr.sorbonne_u.components.hem2025e2.equipments.refrigerator.mil.events.CompressorInactive;
import fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;

// -----------------------------------------------------------------------------
/**
 * The class <code>RefrigeratorUserModel</code>.
 *
 * <p>Created on : 2026-01-15</p>
 *
 * @author	Softweavers
 */
@ModelExternalEvents(exported = {CompressorActive.class,
							 CompressorInactive.class})
// -----------------------------------------------------------------------------
public class			RefrigeratorUserModel
extends		AtomicES_Model
{
	private static final long	serialVersionUID = 1L;
	public static final String	URI = RefrigeratorUserModel.class.getSimpleName();
	public static boolean		VERBOSE = true;
	public static boolean		DEBUG = false;

	public static final double	COMPRESSOR_ON_1 = 0.0; // hours
	public static final double	COMPRESSOR_OFF = 1.0; // hours
	public static final double	COMPRESSOR_ON_2 = 1.5; // hours

	public				RefrigeratorUserModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		AtomicSimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);
		this.getSimulationEngine().setLogger(new StandardLogger());
	}

	@Override
	public void			initialiseState(Time initialTime)
	{
		super.initialiseState(initialTime);

		Time t0 = this.getCurrentStateTime();
		this.scheduleEvent(new CompressorActive(t0.add(
				new Duration(COMPRESSOR_ON_1, this.getSimulatedTimeUnit()))));
		this.scheduleEvent(new CompressorInactive(t0.add(
				new Duration(COMPRESSOR_OFF, this.getSimulatedTimeUnit()))));
		/*this.scheduleEvent(new CompressorActive(t0.add(
				new Duration(COMPRESSOR_ON_2, this.getSimulatedTimeUnit()))));
		*/
		this.nextTimeAdvance = this.timeAdvance();
		this.timeOfNextEvent =
				this.getCurrentStateTime().add(this.getNextTimeAdvance());

		if (VERBOSE) {
			this.logMessage("simulation begins.");
		}
	}

	@Override
	public ArrayList<EventI>	output()
	{
		return super.output();
	}

	@Override
	public void			endSimulation(Time endTime)
	{
		if (VERBOSE) {
			this.logMessage("simulation ends.");
		}
		super.endSimulation(endTime);
	}

	@Override
	public SimulationReportI	getFinalReport()
	{
		return null;
	}
}
// -----------------------------------------------------------------------------
