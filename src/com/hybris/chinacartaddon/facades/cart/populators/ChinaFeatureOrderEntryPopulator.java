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

import de.hybris.platform.commercefacades.order.converters.populator.OrderEntryPopulator;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;


public class ChinaFeatureOrderEntryPopulator extends OrderEntryPopulator
{
	// @Autowired
	// private StockAvaliablePopulator stockAvaliablePopulator;

	// public static final String ANGUO_WAREHOUSE_CODE = "anguo_warehouse";

	@Override
	public void populate(final AbstractOrderEntryModel source, final OrderEntryData target)
	{
		// super.populate(source, target);
		if (source instanceof CartEntryModel)
		{
			target.setSelected(Boolean.valueOf(((CartEntryModel) source).isSelected()));
			// target.setDepositPaymentType(source.getDepositPaymentType());
			// target.setDisabled(((CartEntryModel) source).isDisabled());
		}

		// stockAvaliablePopulator.populate(source, target);
	}

	//	@Override
	//	protected void addTotals(final AbstractOrderEntryModel orderEntry, final OrderEntryData entry)
	//	{
	//		if (orderEntry.getBasePrice() != null)
	//		{
	//			final List<DiscountValue> discounts = orderEntry.getDiscountValues();
	//			Double basePrice = orderEntry.getBasePrice();
	//			Double totalDiscount = 0.0;
	//			for (final DiscountValue discount : discounts)
	//			{
	//				basePrice = basePrice - discount.getValue();
	//				totalDiscount += discount.getValue();
	//			}
	//			entry.setBasePrice(createPrice(orderEntry, basePrice));
	//			entry.setTotalDiscount(createPrice(orderEntry, totalDiscount));
	//		}
	//		if (orderEntry.getTotalPrice() != null)
	//		{
	//			entry.setTotalPrice(createPrice(orderEntry, orderEntry.getTotalPrice()));
	//		}
	//	}
}
