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
package com.hybris.chinacartaddon.facades.checkout.impl;

import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.impl.DefaultCheckoutFacade;
import de.hybris.platform.commerceservices.enums.SalesApplication;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.service.data.CommerceOrderResult;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.exceptions.CalculationException;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;

import com.hybris.chinacartaddon.facades.checkout.ChinaFeatureCheckoutFacade;
import com.hybris.chinacartaddon.services.checkout.ChinaFeatureCheckoutService;


/**
 * @author jimmy
 */
public class ChinaFeatureCheckoutFacadeImpl extends DefaultCheckoutFacade implements ChinaFeatureCheckoutFacade
{
	private static final Logger LOG = Logger.getLogger(ChinaFeatureCheckoutFacadeImpl.class);

	private ChinaFeatureCheckoutService chinaFeatureCheckoutService;

	private CalculationService calculationService;

	@Override
	public List<OrderData> placeOrders(final CommerceCheckoutParameter parameter) throws InvalidCartException
	{
		final CartModel cartModel = getCart();
		if (cartModel != null)
		{
			final UserModel currentUser = getCurrentUserForCheckout();
			if (cartModel.getUser().equals(currentUser) || getCheckoutCustomerStrategy().isAnonymousCheckout())
			{
				beforePlaceOrder(cartModel);

				final List<OrderModel> orderModels = placeOrders(cartModel);
				// final OrderModel orderModel = placeOrder(cartModel);

				afterPlaceOrder(cartModel, orderModels);

				// Convert the order to an order data
				if (CollectionUtils.isNotEmpty(orderModels))
				{
					return Converters.convertAll(orderModels, getOrderConverter());
				}
			}
		}

		return null;
	}

	protected List<OrderModel> placeOrders(final CartModel cartModel) throws InvalidCartException
	{
		final CommerceCheckoutParameter parameter = new CommerceCheckoutParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(cartModel);
		parameter.setSalesApplication(SalesApplication.WEB);

		final CommerceOrderResult commerceOrderResult = getChinaFeatureCheckoutService().placeOrders(parameter);
		return commerceOrderResult.getOrders();
	}

	@Override
	protected void beforePlaceOrder(final CartModel cartModel)
	{
		final List<AbstractOrderEntryModel> original = cartModel.getEntries();
		final List<AbstractOrderEntryModel> selected = cartModel.getEntries().stream().filter(entry -> entry.isSelected())
				.collect(Collectors.toList());

		cartModel.setTempEntries(original);
		cartModel.setEntries(selected);

		// getModelService().save(cartModel);
		// getModelService().refresh(cartModel);
	}

	protected void afterPlaceOrder(final CartModel cartModel, final List<OrderModel> orderModel)
	{
		if (orderModel != null && !orderModel.isEmpty())
		{
			orderModel.stream().forEach(order -> {
				if (order != null)
				{
					getModelService().refresh(order);
				}
			});

			cartModel
					.setEntries(cartModel.getTempEntries().stream().filter(entry -> !entry.isSelected()).collect(Collectors.toList()));
			cartModel.setTempEntries(null);

			try
			{
				getCalculationService().recalculate(cartModel);
			}
			catch (final CalculationException e)
			{
				// YTODO Auto-generated catch block
				e.printStackTrace();
			}

			getModelService().save(cartModel);
			getModelService().refresh(cartModel);

			if (cartModel.getEntries().isEmpty())
			{
				// Remove cart
				getCartService().removeSessionCart();
			}

			// getModelService().refresh(orderModel);
		}
	}

	/**
	 * @return the chinaFeatureCheckoutService
	 */
	public ChinaFeatureCheckoutService getChinaFeatureCheckoutService()
	{
		return chinaFeatureCheckoutService;
	}

	/**
	 * @param chinaFeatureCheckoutService
	 *           the chinaFeatureCheckoutService to set
	 */
	public void setChinaFeatureCheckoutService(final ChinaFeatureCheckoutService chinaFeatureCheckoutService)
	{
		this.chinaFeatureCheckoutService = chinaFeatureCheckoutService;
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

}
