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
package com.hybris.chinacartaddon.facades.cart.populators;

import de.hybris.platform.commercefacades.order.converters.populator.AbstractOrderPopulator;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;


/**
 * Populate the selected entry related numbers for the page
 */
public class CartSelectedEntryStatisticPopulator extends AbstractOrderPopulator<CartModel, CartData>
{
	@Override
	public void populate(final CartModel source, final CartData target) throws ConversionException
	{
		if (CollectionUtils.isEmpty(target.getEntries()))
		{
			this.addEntries(source, target);
		}
		target.setSelectedTotalItemsCount(calculateSelectedTotalItemsCount(source));
		target.setSelectedTotalPrice(calculateSelectedTotalPrice(source));
		//		target.setSelectedTotalSellersCount(calculateSelectedTotalTypesCount(source));
	}

	protected Integer calculateSelectedTotalItemsCount(final CartModel source)
	{
		return Integer.valueOf(source.getEntries().stream().filter(new SelectedPredicate())
				.collect(Collectors.summingLong(AbstractOrderEntryModel::getQuantity)).intValue());
	}

	//	protected Integer calculateSelectedTotalTypesCount(final CartModel source)
	//	{
	//		return source.getEntries().stream().filter(new SelectedPredicate())
	//				.map(entry -> entry.getProduct().getProductAttribute().getCode()).collect(Collectors.toSet())
	//				.size();
	//	}

	protected PriceData calculateSelectedTotalPrice(final CartModel source)
	{
		final Double sum = source.getEntries().stream().filter(new SelectedPredicate())
				.collect(Collectors.summingDouble(AbstractOrderEntryModel::getTotalPrice));
		return createPrice(source, sum);
	}

	private class SelectedPredicate implements Predicate<AbstractOrderEntryModel>
	{
		@Override
		public boolean test(final AbstractOrderEntryModel entryModel)
		{
			if (entryModel instanceof CartEntryModel)
			{
				final CartEntryModel cartEntryModel = (CartEntryModel) entryModel;
				// return cartEntryModel.isDisabled() == false && cartEntryModel.getSelected();
				return cartEntryModel.isSelected();
			}
			return false;
		}
	}
}
