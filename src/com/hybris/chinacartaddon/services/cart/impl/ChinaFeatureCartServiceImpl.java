/**Copyright [Yang]

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package com.hybris.chinacartaddon.services.cart.impl;

import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.order.impl.DefaultCartService;

import java.util.List;

import com.hybris.chinacartaddon.services.cart.ChinaFeatureCartService;


/**
 * @author jimmy
 */
@SuppressWarnings("serial")
public class ChinaFeatureCartServiceImpl extends DefaultCartService implements ChinaFeatureCartService
{
	private CalculationService calculationService;

	private CommerceCartService commerceCartService;

	@Override
	public void updateCartEntriesStatus(final CartModel cart, final List<Integer> entryNumbers, final Boolean selected)
	{
		cart.getEntries().stream().filter(entry -> entryNumbers.contains(entry.getEntryNumber())).forEach(action -> {
			if (action instanceof CartEntryModel)
			{
				final CartEntryModel cartEntryModel = (CartEntryModel) action;
				cartEntryModel.setSelected(selected.booleanValue());
				getModelService().save(cartEntryModel);
				getModelService().refresh(cartEntryModel);
			}
		});

		try
		{
			final CommerceCartParameter parameter = new CommerceCartParameter();
			parameter.setEnableHooks(true);
			parameter.setCart(cart);
			getCommerceCartService().recalculateCart(parameter);
		}
		catch (final CalculationException e)
		{
			// YTODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @return the calculationService
	 */
	public CalculationService getCalculationService()
	{
		return calculationService;
	}

	/**
	 * @param calculationService
	 *           the calculationService to set
	 */
	public void setCalculationService(final CalculationService calculationService)
	{
		this.calculationService = calculationService;
	}

	/**
	 * @return the commerceCartService
	 */
	public CommerceCartService getCommerceCartService()
	{
		return commerceCartService;
	}

	/**
	 * @param commerceCartService
	 *           the commerceCartService to set
	 */
	public void setCommerceCartService(final CommerceCartService commerceCartService)
	{
		this.commerceCartService = commerceCartService;
	}

}
