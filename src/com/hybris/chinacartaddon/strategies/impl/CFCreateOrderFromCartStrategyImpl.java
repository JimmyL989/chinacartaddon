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

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.strategies.CartValidator;
import de.hybris.platform.order.strategies.impl.DefaultCreateOrderFromCartStrategy;
import de.hybris.platform.order.strategies.ordercloning.CloneAbstractOrderStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hybris.chinacartaddon.strategies.ChinaFeatureCreateOrderFromCartStrategy;


/**
 * @author jimmy
 */
public class CFCreateOrderFromCartStrategyImpl extends DefaultCreateOrderFromCartStrategy
		implements ChinaFeatureCreateOrderFromCartStrategy
{
	private CartValidator cartValidator;

	private CloneAbstractOrderStrategy cloneAbstractOrderStrategy;

	// private static final String VEHICLEFUTURES_PARTIAL_PAY = "VEHICLEFUTURES_PARTIAL_PAY";

	@Override
	public List<OrderModel> createOrdersFromCart(final CartModel cart) throws InvalidCartException
	{
		if (cartValidator != null)
		{
			cartValidator.validateCart(cart);
		}

		//		final Map<ProductAttribute, List<AbstractOrderEntryModel>> cartMap1 = cart.getEntries().stream()
		//				.collect(Collectors.groupingBy(entry -> {
		//					if (entry.getProduct() instanceof GVGConfigurationProductModel)
		//					{
		//						final GVGConfigurationProductModel model = (GVGConfigurationProductModel) entry.getProduct();
		//
		//						if (ProductAttribute.VEHICLEFUTURES.getCode().equals(model.getProductAttribute().getCode())
		//								&& !DepositPaymentType.ALL.getCode().equals(entry.getDepositPaymentType().getCode()))
		//						{
		//							return ProductAttribute.VEHICLEFUTURES_PARTIAL_PAY;
		//						}
		//
		//						return model.getProductAttribute();
		//					}
		//					else if (entry.getProduct() instanceof GVGPartsProductModel)
		//					{
		//						// return ((GVGPartsVariantProductModel) entry.getProduct()).getProductAttribute();
		//						return ProductAttribute.PARTSSTOCK;
		//					}
		//					return null;
		//				}));
		final Map<String, List<AbstractOrderEntryModel>> cartMap1 = new HashMap();
		cartMap1.put("testStore", cart.getEntries());

		final List<OrderModel> results = new ArrayList<OrderModel>();

		//		cartMap1.entrySet().stream().forEach(map -> {
		//			cart.setEntries(map.getValue());
		//			final OrderModel res = cloneAbstractOrderStrategy.clone(null, null, cart, generateOrderCode(cart), OrderModel.class,
		//					OrderEntryModel.class);
		//			res.setSubType(map.getKey());
		//			if (ProductAttribute.PARTSFUTURES.equals(res.getSubType()) || ProductAttribute.PARTSSTOCK.equals(res.getSubType()))
		//			{
		//				res.setInvoice(cart.getPartsInvoice());
		//			}
		//			if (ProductAttribute.VEHICLEFUTURES_PARTIAL_PAY.equals(res.getSubType()))
		//			{
		//				res.setFirstPaymentPrice(res.getEntries().stream().collect(Collectors.summingDouble(entry -> {
		//					return getGvglhb2bpriceService().getFirstPaymentPrice(entry);
		//				})));
		//			}
		//			results.add(res);
		//		});

		cartMap1.entrySet().stream().forEach(map -> {
			cart.setEntries(map.getValue());
			final OrderModel res = cloneAbstractOrderStrategy.clone(null, null, cart, generateOrderCode(cart), OrderModel.class,
					OrderEntryModel.class);
			results.add(res);
		});

		return results;
	}

	/**
	 * @return the cartValidator
	 */
	public CartValidator getCartValidator()
	{
		return cartValidator;
	}

	/**
	 * @param cartValidator
	 *           the cartValidator to set
	 */
	@Override
	public void setCartValidator(final CartValidator cartValidator)
	{
		this.cartValidator = cartValidator;
	}

	/**
	 * @return the cloneAbstractOrderStrategy
	 */
	public CloneAbstractOrderStrategy getCloneAbstractOrderStrategy()
	{
		return cloneAbstractOrderStrategy;
	}

	/**
	 * @param cloneAbstractOrderStrategy
	 *           the cloneAbstractOrderStrategy to set
	 */
	@Override
	public void setCloneAbstractOrderStrategy(final CloneAbstractOrderStrategy cloneAbstractOrderStrategy)
	{
		this.cloneAbstractOrderStrategy = cloneAbstractOrderStrategy;
	}

	//	@Override
	//	public void createB2BBusinessProcess(final OrderModel order)
	//	{
	//		final OrderStatus status = order.getStatus();
	//		Assert.notNull(status, "Order status should have been set for order " + order.getCode());
	//		// retrieve an appropriate strategy for the order based on status.
	//		final BusinessProcessStrategy businessProcessStrategy = getBusinessProcessStrategy(order.getSubType().getCode());
	//		Assert.notNull(businessProcessStrategy, String
	//				.format("The strategy for creating a business process with name %s should have been created", status.getCode()));
	//		businessProcessStrategy.createB2BBusinessProcess(order);
	//	}
	//
	//	/**
	//	 * Looks up the correct business process creation strategy based on the order status. The strategy.code attribute
	//	 * should be injected with an appropriate OrderStatus enumeration value
	//	 *
	//	 * @param code
	//	 * @return BusinessProcessStrategy
	//	 */
	//	public BusinessProcessStrategy getBusinessProcessStrategy(final String code)
	//	{
	//		final BeanPropertyValueEqualsPredicate predicate = new BeanPropertyValueEqualsPredicate("processName", code);
	//		// filter the Collection
	//		return (BusinessProcessStrategy) CollectionUtils.find(getBusinessProcessStrategies(), predicate);
	//	}

}
