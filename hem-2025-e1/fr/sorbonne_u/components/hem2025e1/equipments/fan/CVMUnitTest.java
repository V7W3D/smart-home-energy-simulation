package fr.sorbonne_u.components.hem2025e1.equipments.fan;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;

/**
 * The class <code>CVMUnitTest</code> deploys and runs a unit test for the
 * <code>Fan</code> component.
 *
 * <p>Created on : 2025-12-25</p>
 * 
 * @author	Softweavers
 */
public class			CVMUnitTest
extends		AbstractCVM
{
	public				CVMUnitTest() throws Exception
	{
		super();
	}

	@Override
	public void			deploy() throws Exception
	{
		AbstractComponent.createComponent(
				Fan.class.getCanonicalName(),
				new Object[]{});

		AbstractComponent.createComponent(
				FanTester.class.getCanonicalName(),
				new Object[]{true});

		super.deploy();
	}

	public static void	main(String[] args)
	{
		try {
			CVMUnitTest c = new CVMUnitTest();
			c.startStandardLifeCycle(10000L);
			Thread.sleep(1000L);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
