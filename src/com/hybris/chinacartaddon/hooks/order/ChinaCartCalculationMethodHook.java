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
package com.hybris.chinacartaddon.hooks.order;

import de.hybris.platform.commerceservices.order.hook.CommerceCartCalculationMethodHook;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;


/**
 * @author jimmy
 *
 */
public class ChinaCartCalculationMethodHook implements CommerceCartCalculationMethodHook
{
	private ModelService modelService;

	@Autowired
	private SessionService sessionService;

	@Override
	public void afterCalculate(final CommerceCartParameter parameter)
	{
		final CartModel cartModel = parameter.getCart();
		//		cartModel.setTempEntries(null);

		final Collection<AbstractOrderEntryModel> collection = cartModel.getEntries();
		final List<AbstractOrderEntryModel> paymentTransactionList = new ArrayList<AbstractOrderEntryModel>();

		paymentTransactionList.addAll(collection);


		final List<AbstractOrderEntryModel> original = sessionService.getAttribute("NOTSELECTENTRY");
		getModelService().saveAll(original);
		//		getModelService().refresh(original);
		paymentTransactionList.addAll(original);

		cartModel.setEntries(paymentTransactionList);

		getModelService().save(cartModel);
		getModelService().refresh(cartModel);
	}

	@Override
	public void beforeCalculate(final CommerceCartParameter parameter)
	{
		final CartModel cartModel = parameter.getCart();

		final List<AbstractOrderEntryModel> original = new ArrayList<AbstractOrderEntryModel>();
		//		original.addAll(cartModel.getEntries());


		original.addAll(cartModel.getEntries().stream().filter(entry -> {
			return !entry.isSelected();
		}).map(entry -> {
			return getModelService().clone(entry);
		}).collect(Collectors.toList()));

		sessionService.setAttribute("NOTSELECTENTRY", original);

		//		cartModel.setTempEntries(original);

		//		getModelService().save(cartModel);
		//		getModelService().refresh(cartModel);

		final List<AbstractOrderEntryModel> selected = cartModel.getEntries().stream().filter(entry -> entry.isSelected())
				.collect(Collectors.toList());
		cartModel.setEntries(selected);

	}

	/**
	 * @return the modelService
	 */
	public ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @param modelService
	 *           the modelService to set
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

}
