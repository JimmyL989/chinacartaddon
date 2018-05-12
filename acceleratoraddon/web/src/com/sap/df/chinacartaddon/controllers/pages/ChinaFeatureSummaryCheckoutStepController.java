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
package com.sap.df.chinacartaddon.controllers.pages;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.acceleratorstorefrontcommons.forms.PlaceOrderForm;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.yacceleratorstorefront.controllers.pages.checkout.steps.SummaryCheckoutStepController;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hybris.chinacartaddon.facades.checkout.ChinaFeatureCheckoutFacade;


/**
 * @author jimmy
 */
@Controller
@RequestMapping(value = "/checkout/multi/summary")
public class ChinaFeatureSummaryCheckoutStepController extends SummaryCheckoutStepController
{
	private static final Logger LOGGER = Logger.getLogger(ChinaFeatureSummaryCheckoutStepController.class);

	@Resource(name = "chinaFeatureCheckoutFacade")
	private ChinaFeatureCheckoutFacade chinaFeatureCheckoutFacade;

	// private static final String SUMMARY = "summary";

	@Override
	@RequestMapping(value = "/placeOrder")
	@RequireHardLogIn
	public String placeOrder(@ModelAttribute("placeOrderForm") final PlaceOrderForm placeOrderForm, final Model model,
			final HttpServletRequest request, final RedirectAttributes redirectModel) throws CMSItemNotFoundException, // NOSONAR
			InvalidCartException, CommerceCartModificationException
	{
		if (validateOrderForm(placeOrderForm, model))
		{
			return enterStep(model, redirectModel);
		}

		//Validate the cart
		if (validateCart(redirectModel))
		{
			// Invalid cart. Bounce back to the cart page.
			return REDIRECT_PREFIX + "/cart";
		}

		// authorize, if failure occurs don't allow to place the order
		// final boolean isPaymentUthorized = false;
		//		try
		//		{
		//			isPaymentUthorized = getCheckoutFacade().authorizePayment(placeOrderForm.getSecurityCode());
		//		}
		//		catch (final AdapterException ae)
		//		{
		//			// handle a case where a wrong paymentProvider configurations on the store see getCommerceCheckoutService().getPaymentProvider()
		//			LOGGER.error(ae.getMessage(), ae);
		//		}
		//		if (!isPaymentUthorized)
		//		{
		//			GlobalMessages.addErrorMessage(model, "checkout.error.authorization.failed");
		//			return enterStep(model, redirectModel);
		//		}

		final List<OrderData> orderData;
		try
		{
			final CommerceCheckoutParameter parameter = new CommerceCheckoutParameter();
			orderData = getChinaFeatureCheckoutFacade().placeOrders(parameter);
		}
		catch (final Exception e)
		{
			LOGGER.error("Failed to place Order", e);
			GlobalMessages.addErrorMessage(model, "checkout.placeOrder.failed");
			return enterStep(model, redirectModel);
		}

		return redirectToOrderConfirmationPage(orderData);
	}

	protected String redirectToOrderConfirmationPage(final List<OrderData> orderData)
	{
		if (orderData.size() == 1)
		{
			return REDIRECT_PREFIX + "/my-account/order/" + orderData.get(0).getCode();
		}

		//		return REDIRECT_URL_ORDER_CONFIRMATION
		//				+ (getCheckoutCustomerStrategy().isAnonymousCheckout() ? orderData.get(0).getGuid() : orderData.get(0).getCode());
		return REDIRECT_URL_ORDER_CONFIRMATION
				+ (getCheckoutCustomerStrategy().isAnonymousCheckout() ? StringUtils.join(orderData.stream().map(data -> {
					return data.getGuid();
				}).toArray(), ",") : StringUtils.join(orderData.stream().map(data -> {
					return data.getCode();
				}).toArray(), ","));
	}

	/**
	 * @return the chinaFeatureCheckoutFacade
	 */
	public ChinaFeatureCheckoutFacade getChinaFeatureCheckoutFacade()
	{
		return chinaFeatureCheckoutFacade;
	}

	/**
	 * @param chinaFeatureCheckoutFacade
	 *           the chinaFeatureCheckoutFacade to set
	 */
	public void setChinaFeatureCheckoutFacade(final ChinaFeatureCheckoutFacade chinaFeatureCheckoutFacade)
	{
		this.chinaFeatureCheckoutFacade = chinaFeatureCheckoutFacade;
	}
}
