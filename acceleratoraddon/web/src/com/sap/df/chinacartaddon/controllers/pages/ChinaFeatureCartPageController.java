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

import de.hybris.platform.acceleratorservices.controllers.page.PageType;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.acceleratorstorefrontcommons.forms.UpdateQuantityForm;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.yacceleratorstorefront.controllers.ControllerConstants;
import de.hybris.platform.yacceleratorstorefront.controllers.pages.CartPageController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hybris.chinacartaddon.facades.cart.ChinaFeatureCartFacade;
import com.sap.df.chinacartaddon.controllers.ChinacartaddonControllerConstants;


/**
 * @author Yang
 * @since 2016-10-10 19:51:04
 */
@Controller
@RequestMapping(value = "/cart")
public class ChinaFeatureCartPageController extends CartPageController
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(ChinaFeatureCartPageController.class);

	// private static final String REDIRECT_CART_URL = REDIRECT_PREFIX + "/cart";

	@Resource(name = "simpleBreadcrumbBuilder")
	private ResourceBreadcrumbBuilder resourceBreadcrumbBuilder;

	@Resource(name = "chinaFeatureCartFacade")
	private ChinaFeatureCartFacade chinaFeatureCartFacade;

	private static final String REDIRECT_CART_URL = REDIRECT_PREFIX + "/cart";

	/**
	 * Display the cart page
	 */
	@Override
	@RequestMapping(method = RequestMethod.GET)
	public String showCart(final Model model) throws CMSItemNotFoundException, CommerceCartModificationException // NOSONAR
	{
		prepareDataForPage(model);
		createProductListByType(model);
		model.addAttribute(WebConstants.BREADCRUMBS_KEY, resourceBreadcrumbBuilder.getBreadcrumbs("breadcrumb.cart"));
		model.addAttribute("pageType", PageType.CART.name());
		return ControllerConstants.Views.Pages.Cart.CartPage;
	}

	/**
	 * customize the cart group
	 *
	 * @param model
	 */
	private void createProductListByType(final Model model)
	{
		if (getCartFacade().hasEntries())
		{
			final CartData cartData = chinaFeatureCartFacade.getSessionCartWithEntryOrdering(true);
			// TODO: customize your cart group logic
			//			final Map<ProductAttribute, List<OrderEntryData>> cartMap = cartData.getEntries().stream()
			//					.collect(Collectors.groupingBy(orderEntryData -> orderEntryData.getProduct().getProductAttribute(),
			//							() -> new TreeMap<ProductAttribute, List<OrderEntryData>>((a1, a2) -> a1.ordinal() - a2.ordinal()),
			//							Collectors.mapping(Function.identity(), Collectors.toList())));

			final Map<String, List<OrderEntryData>> cartMap = new HashMap();
			cartMap.put("testStore", cartData.getEntries());

			model.addAttribute("cartMap", cartMap);

			cartData.setEntries(null);
			model.addAttribute("cartDataForTotalPrice", cartData);
		}
		else
		{
			model.addAttribute("cartMap", new HashMap<>());
		}
	}

	/**
	 * Used to update the entry selected status
	 *
	 * @param entryNumbers
	 * @param selected
	 * @return customized cart data
	 */
	@ResponseBody
	@RequestMapping(value = "/updateEntriesStatus/{selected}", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public CartData updateEntriesStatus(@RequestBody final List<Integer> entryNumbers, @PathVariable final Boolean selected)
	{
		if (getCartFacade().hasEntries())
		{
			return chinaFeatureCartFacade.updateEntiresSelectedStatus(entryNumbers, selected);
		}

		return null;
	}

	@RequestMapping(value = "/update-cart", method = RequestMethod.POST)
	public String updateB2bCartQuantities(@RequestParam("entryNumber") final long entryNumber, final Model model,
			@Valid final UpdateQuantityForm form, final BindingResult bindingResult, final HttpServletRequest request,
			final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		if (bindingResult.hasErrors())
		{
			for (final ObjectError error : bindingResult.getAllErrors())
			{
				if ("typeMismatch".equals(error.getCode()))
				{
					GlobalMessages.addErrorMessage(model, "basket.error.quantity.invalid");
				}
				else
				{
					GlobalMessages.addErrorMessage(model, error.getDefaultMessage());
				}
			}
			return REDIRECT_CART_URL;
		}
		else if (getCartFacade().hasEntries())
		{
			try
			{
				chinaFeatureCartFacade.updateEntiresSelectedStatus(Arrays.asList(new Integer[]
				{ Integer.valueOf((int) entryNumber) }), Boolean.TRUE);

				final CartModificationData cartModification = getCartFacade().updateCartEntry(entryNumber,
						form.getQuantity().longValue());
				if (cartModification.getQuantity() == form.getQuantity().longValue())
				{
					// Success
					if (cartModification.getQuantity() == 0)
					{
						// Success in removing entry
						GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER,
								"basket.page.message.remove");
						return REDIRECT_CART_URL;
					}
				}
				else if (cartModification.getQuantity() > 0)
				{
					// Less than successful
					GlobalMessages.addMessage(model, GlobalMessages.ERROR_MESSAGES_HOLDER,
							"basket.page.message.update.reducedNumberOfItemsAdded.lowStock", new Object[]
							{ cartModification.getEntry().getProduct().getName(), Long.valueOf(cartModification.getQuantity()),
									form.getQuantity(),
									request.getRequestURL().append(cartModification.getEntry().getProduct().getUrl()) });
					model.addAttribute("isLowStock", Integer.valueOf((int) entryNumber));
				}
				else
				{
					// No more stock available
					GlobalMessages.addMessage(model, GlobalMessages.ERROR_MESSAGES_HOLDER,
							"basket.page.message.update.reducedNumberOfItemsAdded.noStock", new Object[]
							{ cartModification.getEntry().getProduct().getName(),
									request.getRequestURL().append(cartModification.getEntry().getProduct().getUrl()) });
					model.addAttribute("isNoStock" + Integer.valueOf((int) entryNumber));
				}
			}
			catch (final CommerceCartModificationException ex)
			{
				LOG.warn("Couldn't update product with the entry number: " + entryNumber + ".", ex);
			}
		}

		prepareDataForPage(model);
		createProductList(model);
		createProductListByType(model);

		model.addAttribute(WebConstants.BREADCRUMBS_KEY, resourceBreadcrumbBuilder.getBreadcrumbs("breadcrumb.cart"));
		model.addAttribute("pageType", PageType.CART.name());

		return ChinacartaddonControllerConstants.Views.Pages.Cart.CartItem;
	}
}
