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
package com.hybris.chinacartaddon.facades.cart.impl;

import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.impl.DefaultCartFacade;
import de.hybris.platform.core.model.order.CartModel;

import java.util.List;

import com.hybris.chinacartaddon.facades.cart.ChinaFeatureCartFacade;
import com.hybris.chinacartaddon.services.cart.ChinaFeatureCartService;


/**
 * @author jimmy
 */
public class ChinaFeatureCartFacadeImpl extends DefaultCartFacade implements ChinaFeatureCartFacade
{
	private ChinaFeatureCartService chinaFeatureCartService;

	private Converter<CartModel, CartData> modificationCartConverter;

	@Override
	public CartData updateEntiresSelectedStatus(final List<Integer> entryNumbers, final Boolean selected)
	{
		final CartData cartData;
		if (hasSessionCart())
		{
			final CartModel cart = getCartService().getSessionCart();
			chinaFeatureCartService.updateCartEntriesStatus(cart, entryNumbers, selected);
			cartData = modificationCartConverter.convert(cart);
			cartData.setEntries(null);
		}
		else
		{
			cartData = createEmptyCart();
		}
		return cartData;
	}

	@Override
	public CartData getSessionCartWithEntryOrdering(final boolean recentlyAddedFirst)
	{
		final CartData cartData = super.getSessionCartWithEntryOrdering(recentlyAddedFirst);
		return cartData;
	}

	/**
	 * @return the chinaFeatureCartService
	 */
	public ChinaFeatureCartService getChinaFeatureCartService()
	{
		return chinaFeatureCartService;
	}

	/**
	 * @param chinaFeatureCartService
	 *           the chinaFeatureCartService to set
	 */
	public void setChinaFeatureCartService(final ChinaFeatureCartService chinaFeatureCartService)
	{
		this.chinaFeatureCartService = chinaFeatureCartService;
	}

	/**
	 * @return the modificationCartConverter
	 */
	public Converter<CartModel, CartData> getModificationCartConverter()
	{
		return modificationCartConverter;
	}

	/**
	 * @param modificationCartConverter
	 *           the modificationCartConverter to set
	 */
	public void setModificationCartConverter(final Converter<CartModel, CartData> modificationCartConverter)
	{
		this.modificationCartConverter = modificationCartConverter;
	}
}
