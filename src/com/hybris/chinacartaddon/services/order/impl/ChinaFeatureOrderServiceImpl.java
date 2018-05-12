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
package com.hybris.chinacartaddon.services.order.impl;

import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.impl.DefaultOrderService;
import de.hybris.platform.order.strategies.SubmitOrderStrategy;
import de.hybris.platform.servicelayer.i18n.I18NService;

import java.util.Collections;
import java.util.List;

import com.hybris.chinacartaddon.services.order.ChinaFeatureOrderService;
import com.hybris.chinacartaddon.strategies.ChinaFeatureCreateOrderFromCartStrategy;


/**
 * @author jimmy
 */
@SuppressWarnings("serial")
public class ChinaFeatureOrderServiceImpl extends DefaultOrderService implements ChinaFeatureOrderService
{
	private ChinaFeatureCreateOrderFromCartStrategy cfCreateOrderFromCartStrategy;

	private I18NService i18nService;

	private List<SubmitOrderStrategy> submitOrderStrategies = Collections.emptyList();

	@Override
	public List<OrderModel> createOrdersFromCart(final CartModel cart) throws InvalidCartException
	{
		//		cart.setLocale(getI18nService().getCurrentLocale().toString());
		return cfCreateOrderFromCartStrategy.createOrdersFromCart(cart);
	}


	@Override
	public void submitOrders(final List<OrderModel> orderModels)
	{
		for (final OrderModel orderModel : orderModels)
		{
			for (final SubmitOrderStrategy strategy : submitOrderStrategies)
			{
				strategy.submitOrder(orderModel);
			}
		}
	}

	/**
	 * @return the cfCreateOrderFromCartStrategy
	 */
	public ChinaFeatureCreateOrderFromCartStrategy getCfCreateOrderFromCartStrategy()
	{
		return cfCreateOrderFromCartStrategy;
	}

	/**
	 * @param cfCreateOrderFromCartStrategy
	 *           the cfCreateOrderFromCartStrategy to set
	 */
	public void setCfCreateOrderFromCartStrategy(final ChinaFeatureCreateOrderFromCartStrategy cfCreateOrderFromCartStrategy)
	{
		this.cfCreateOrderFromCartStrategy = cfCreateOrderFromCartStrategy;
	}

	/**
	 * @return the submitOrderStrategies
	 */
	public List<SubmitOrderStrategy> getSubmitOrderStrategies()
	{
		return submitOrderStrategies;
	}

	@Override
	public void setSubmitOrderStrategies(final List<SubmitOrderStrategy> submitOrderStrategies)
	{
		this.submitOrderStrategies = submitOrderStrategies;
	}

	/**
	 * @return the i18nService
	 */
	public I18NService getI18nService()
	{
		return i18nService;
	}

	/**
	 * @param i18nService
	 *           the i18nService to set
	 */
	public void setI18nService(final I18NService i18nService)
	{
		this.i18nService = i18nService;
	}
}
