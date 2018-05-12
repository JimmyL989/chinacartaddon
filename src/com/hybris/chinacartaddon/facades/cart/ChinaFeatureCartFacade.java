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
package com.hybris.chinacartaddon.facades.cart;

import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;

import java.util.List;


/**
 * @author jimmy
 */
public interface ChinaFeatureCartFacade extends CartFacade
{
	/**
	 * Update cart entries selected status
	 *
	 * @param entryNumbers
	 * @param selected
	 * @return cart data
	 */
	public CartData updateEntiresSelectedStatus(final List<Integer> entryNumbers, final Boolean selected);
}
