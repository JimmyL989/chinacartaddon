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
package com.hybris.chinacartaddon.services.checkout.impl;

import de.hybris.platform.commerceservices.order.impl.DefaultCommerceCheckoutService;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.service.data.CommerceOrderResult;
import de.hybris.platform.order.InvalidCartException;

import com.hybris.chinacartaddon.services.checkout.ChinaFeatureCheckoutService;
import com.hybris.chinacartaddon.strategies.ChinaFeaturePlaceOrderStrategy;


/**
 * @author jimmy
 */
public class ChinaFeatureCheckoutServiceImpl extends DefaultCommerceCheckoutService implements ChinaFeatureCheckoutService
{
	private ChinaFeaturePlaceOrderStrategy chinaFeaturePlaceOrderStrategy;

	@Override
	public CommerceOrderResult placeOrders(final CommerceCheckoutParameter parameter) throws InvalidCartException
	{
		return getChinaFeaturePlaceOrderStrategy().placeOrders(parameter);
	}

	/**
	 * @return the chinaFeaturePlaceOrderStrategy
	 */
	public ChinaFeaturePlaceOrderStrategy getChinaFeaturePlaceOrderStrategy()
	{
		return chinaFeaturePlaceOrderStrategy;
	}

	/**
	 * @param chinaFeaturePlaceOrderStrategy
	 *           the chinaFeaturePlaceOrderStrategy to set
	 */
	public void setChinaFeaturePlaceOrderStrategy(final ChinaFeaturePlaceOrderStrategy chinaFeaturePlaceOrderStrategy)
	{
		this.chinaFeaturePlaceOrderStrategy = chinaFeaturePlaceOrderStrategy;
	}
}
