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
package com.hybris.chinacartaddon.strategies.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.commerceservices.order.impl.DefaultCommercePlaceOrderStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.service.data.CommerceOrderResult;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.servicelayer.time.TimeService;

import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;

import com.hybris.chinacartaddon.services.order.ChinaFeatureOrderService;
import com.hybris.chinacartaddon.strategies.ChinaFeaturePlaceOrderStrategy;


/**
 * @author jimmy
 */
public class ChinaFeaturePlaceOrderStrategyImpl extends DefaultCommercePlaceOrderStrategy
		implements ChinaFeaturePlaceOrderStrategy
{
	private static final Logger LOG = Logger.getLogger(ChinaFeaturePlaceOrderStrategyImpl.class);

	private ChinaFeatureOrderService chinaFeatureOrderService;

	//	private GvgStockService gvgStockService;

	@Resource
	private TimeService timeService;

	@Override
	public CommerceOrderResult placeOrders(final CommerceCheckoutParameter parameter) throws InvalidCartException
	{
		final CartModel cartModel = parameter.getCart();
		validateParameterNotNull(cartModel, "Cart model cannot be null");
		final CommerceOrderResult result = new CommerceOrderResult();
		try
		{
			beforePlaceOrder(parameter);
			if (getCalculationService().requiresCalculation(cartModel))
			{
				// does not make sense to fail here especially since we don't fail below when we calculate order.
				// throw new IllegalArgumentException(String.format("Cart [%s] must be calculated", cartModel.getCode()));
				LOG.error(String.format("CartModel's [%s] calculated flag was false", cartModel.getCode()));
			}

			final CustomerModel customer = (CustomerModel) cartModel.getUser();
			validateParameterNotNull(customer, "Customer model cannot be null");

			final List<OrderModel> orderModels = getChinaFeatureOrderService().createOrdersFromCart(cartModel);
			if (CollectionUtils.isNotEmpty(orderModels))
			{
				for (final OrderModel orderModel : orderModels)
				{
					// Reset the Date attribute for use in determining when the order was placed
					orderModel.setDate(timeService.getCurrentTime());

					// Store the current site and store on the order
					orderModel.setSite(getBaseSiteService().getCurrentBaseSite());
					orderModel.setStore(getBaseStoreService().getCurrentBaseStore());
					orderModel.setLanguage(getCommonI18NService().getCurrentLanguage());

					if (parameter.getSalesApplication() != null)
					{
						orderModel.setSalesApplication(parameter.getSalesApplication());
					}

					// clear the promotionResults that where cloned from cart PromotionService.transferPromotionsToOrder will copy them over bellow.
					orderModel.setAllPromotionResults(Collections.<PromotionResultModel> emptySet());

					getModelService().saveAll(customer, orderModel);

					if (cartModel.getPaymentInfo() != null && cartModel.getPaymentInfo().getBillingAddress() != null)
					{
						final AddressModel billingAddress = cartModel.getPaymentInfo().getBillingAddress();
						orderModel.setPaymentAddress(billingAddress);
						orderModel.getPaymentInfo().setBillingAddress(getModelService().clone(billingAddress));
						getModelService().save(orderModel.getPaymentInfo());
					}
					orderModel.setStatus(OrderStatus.CREATED);

					getModelService().save(orderModel);
					// Transfer promotions to the order
					getPromotionsService().transferPromotionsToOrder(cartModel, orderModel, false);

					// Calculate the order now that it has been copied
					try
					{
						getCalculationService().calculateTotals(orderModel, false);
						getExternalTaxesService().calculateExternalTaxes(orderModel);
					}
					catch (final CalculationException ex)
					{
						LOG.error("Failed to calculate order [" + orderModel + "]", ex);
					}

					getModelService().refresh(orderModel);
					getModelService().refresh(customer);
				}

				//				try {
				//					getGvgStockService().reserve(orderModels, null, cartModel.getCode());
				//				}
				//				catch(Exception e) {
				//					LOG.error("Failed to reserve orders [" + orderModels + "]", e);
				//					throw new InvalidCartException(e);
				//				}

				result.setOrders(orderModels);

				this.beforeSubmitOrder(parameter, result);

				getChinaFeatureOrderService().submitOrders(orderModels);

				this.afterPlaceOrder(parameter, result);
			}
			else
			{
				throw new IllegalArgumentException(String.format("Order was not properly created from cart %s", cartModel.getCode()));
			}
		}
		finally
		{
			getExternalTaxesService().clearSessionTaxDocument();
		}

		return result;
	}

	/**
	 * @return the chinaFeatureOrderService
	 */
	public ChinaFeatureOrderService getChinaFeatureOrderService()
	{
		return chinaFeatureOrderService;
	}

	/**
	 * @param chinaFeatureOrderService
	 *           the chinaFeatureOrderService to set
	 */
	public void setChinaFeatureOrderService(final ChinaFeatureOrderService chinaFeatureOrderService)
	{
		this.chinaFeatureOrderService = chinaFeatureOrderService;
	}

	//	/**
	//	 * @return the gvgStockService
	//	 */
	//	public GvgStockService getGvgStockService()
	//	{
	//		return gvgStockService;
	//	}
	//
	//	/**
	//	 * @param gvgStockService
	//	 *           the gvgStockService to set
	//	 */
	//	public void setGvgStockService(final GvgStockService gvgStockService)
	//	{
	//		this.gvgStockService = gvgStockService;
	//	}
}
