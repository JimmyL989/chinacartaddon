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
package com.hybris.chinacartaddon.services.cart;

import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;

import java.util.List;


/**
 * @author jimmy
 */
public interface ChinaFeatureCartService extends CartService
{
	/**
	 * Update the cart entries selected status
	 *
	 * @param cart
	 * @param entryNumbers
	 * @param selected
	 */
	public void updateCartEntriesStatus(final CartModel cart, final List<Integer> entryNumbers, final Boolean selected);

}
