<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product" %>
<%@ taglib prefix="grid" tagdir="/WEB-INF/tags/responsive/grid" %>
<%@ taglib prefix="storepickup" tagdir="/WEB-INF/tags/responsive/storepickup" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div id="cartItems">
<c:forEach items="${cartMap}" var="typeEntry">
	<div>
		<input id="selectAll_${typeEntry.key}" type="checkbox" /><spring:theme code="basket.page.${typeEntry.key}" />
	</div>
	<div>
		 <ul class="item__list item__list__cart">
		    <li class="hidden-xs hidden-sm">
		        <ul class="item__list--header">
		            <li class="item__toggle"></li>
		            <li class="item__image"></li>
		            <li class="item__info"><spring:theme code="basket.page.item"/></li>
		            <li class="item__price"><spring:theme code="basket.page.price"/></li>
		            <li class="item__quantity"><spring:theme code="basket.page.qty"/></li>
		            <%-- <li class="item__delivery"><spring:theme code="basket.page.delivery"/></li> --%>
		            <li class="item__total--column  allmonery "><spring:theme code="basket.page.total"/></li>
		            <li class="item__total--column"><spring:theme code="basket.page.deposit.payment.type"/></li>
		            <li class="item__remove"></li>
				</ul>
    		</li>
    		<c:forEach items="${typeEntry.value}" var="entry" >
        		<c:if test="${not empty entry.statusSummaryMap}" >
            		<c:set var="errorCount" value="${entry.statusSummaryMap.get(errorStatus)}"/>
            		<c:if test="${not empty errorCount && errorCount > 0}" >
            			<li>
            				<div class="notification has-error">
			                    <spring:theme code="basket.error.invalid.configuration" arguments="${errorCount}"/>
			                    <a href="<c:url value="/cart/${entry.entryNumber}/configuration/${entry.configurationInfos[0].configuratorType}" />" >
			                        <spring:theme code="basket.error.invalid.configuration.edit"/>
			                    </a>
			                    <div></div>
		                	</div>
            			</li>		                
            		</c:if>
        		</c:if>
        	<c:set var="showEditableGridClass" value=""/>
       		<c:url value="${entry.product.url}" var="productUrl"/>   
        	<li class="item__list--item ">	  		
            	<%-- chevron for multi-d products --%>
	            <div class="hidden-xs hidden-sm item__toggle">
	                <c:if test="${entry.product.multidimensional}" >
	                    <div class="js-show-editable-grid" data-index="${entry.entryNumber}" data-read-only-multid-grid="${not entry.updateable}">
	                        <ycommerce:testId code="cart_product_updateQuantity">
	                            <span class="glyphicon glyphicon-chevron-down"></span>
	                        </ycommerce:testId>
	                    </div>
	                </c:if>
	            </div>

	            <%-- product image --%>
	            <div class="item__image">
	            	<input id="select_${typeEntry.key}_${product.code}" data-entrynumber="${entry.entryNumber}" name="items" type="checkbox" <c:if test="${entry.selected eq true}">checked</c:if>/>
	                <a href="${productUrl}">
	                	<product:productPrimaryImage product="${entry.product}" format="thumbnail"/>
	                </a>
	            </div>

            	<%-- product name, code, promotions --%>
             
	            <div class="item__info ">
	                <ycommerce:testId code="cart_product_name">
	                    <a href="${productUrl}"><span class="item__name">${entry.product.name}</span></a>
	                </ycommerce:testId>
	
	                <div class="item__code">${entry.product.code}</div>
	
	                <c:if test="${ycommerce:doesPotentialPromotionExistForOrderEntry(cartData, entry.entryNumber)}">
	                    <c:forEach items="${cartData.potentialProductPromotions}" var="promotion">
	                        <c:set var="displayed" value="false"/>
	                        <c:forEach items="${promotion.consumedEntries}" var="consumedEntry">
	                            <c:if test="${not displayed && consumedEntry.orderEntryNumber == entry.entryNumber && not empty promotion.description}">
	                                <c:set var="displayed" value="true"/>
	
	                                    <div class="promo">
	                                         <ycommerce:testId code="cart_potentialPromotion_label">
	                                             ${promotion.description}
	                                         </ycommerce:testId>
	                                    </div>
	                            </c:if>
	                        </c:forEach>
	                    </c:forEach>
	                </c:if>
	                <c:if test="${ycommerce:doesAppliedPromotionExistForOrderEntry(cartData, entry.entryNumber)}">
	                    <c:forEach items="${cartData.appliedProductPromotions}" var="promotion">
	                        <c:set var="displayed" value="false"/>
	                        <c:forEach items="${promotion.consumedEntries}" var="consumedEntry">
	                            <c:if test="${not displayed && consumedEntry.orderEntryNumber == entry.entryNumber}">
	                                <c:set var="displayed" value="true"/>
	                                <div class="promo">
	                                    <ycommerce:testId code="cart_appliedPromotion_label">
	                                        ${promotion.description}
	                                    </ycommerce:testId>
	                                </div>
	                            </c:if>
	                        </c:forEach>
	                    </c:forEach>
	                </c:if>
	
	                <c:if test="${entry.product.configurable}">
	                    <div class="hidden-xs hidden-sm">
	                        <c:url value="/cart/${entry.entryNumber}/configuration/${entry.configurationInfos[0].configuratorType}" var="entryConfigUrl"/>
	                        <div class="item__configurations">
	                            <c:forEach var="config" items="${entry.configurationInfos}">
	                                <c:set var="style" value=""/>
	                                <c:if test="${config.status eq errorStatus}">
	                                    <c:set var="style" value="color:red"/>
	                                </c:if>
	                                <div class="item__configuration--entry" style="${style}">
	                                    <div class="row">
	                                        <div class="item__configuration--name col-sm-4">
	                                                ${config.configurationLabel}:
	                                        </div>
	                                        <div class="item__configuration--value col-sm-8">
	                                                ${config.configurationValue}
	                                        </div>
	                                    </div>
	                                </div>
	                            </c:forEach>
	                        </div>
	                        <c:if test="${not empty entry.configurationInfos}">
	                            <div class="item__configurations--edit">
	                                <a class="btn" href="${entryConfigUrl}"><spring:theme code="basket.page.change.configuration"/></a>
	                            </div>
	                        </c:if>
	                    </div>
	                </c:if>
	            </div>

	            <%-- price --%>
	            <div class="item__price">
	                <span class="visible-xs visible-sm"><spring:theme code="basket.page.itemPrice"/>: </span>
	                <format:price priceData="${entry.basePrice}" displayFreeForZero="true"/>
	            </div>
	           
                  
	            <%-- quantity --%>
	            <div class="item__quantity hidden-xs hidden-sm ">
	                <c:choose>
	                    <c:when test="${not entry.product.multidimensional}" >
	                        <c:url value="/cart/update-cart" var="cartUpdateFormAction" />
	                        <form:form id="updateCartForm${entry.entryNumber}" action="${cartUpdateFormAction}" method="post" commandName="updateQuantityForm${entry.entryNumber}"
	                                    class="js-qty-form${entry.entryNumber} gvgUpdateCartForm"
	                                    data-cart='{"cartCode" : "${cartData.code}","productPostPrice":"${entry.basePrice.value}","productName":"${entry.product.name}"}'>
	                            <input type="hidden" name="entryNumber" value="${entry.entryNumber}"/>
	                            <input type="hidden" name="productCode" value="${entry.product.code}"/>
	                            <input type="hidden" name="initialQuantity" value="${entry.quantity}"/>
	                            <ycommerce:testId code="cart_product_quantity">
	                                <form:label cssClass="visible-xs visible-sm" path="quantity" for="quantity${entry.entryNumber}"></form:label>
	                                <div class="qty-selector input-group js-qty-selector">
										<span class="input-group-btn">
											<button class="btn btn-default js-qty-minus btn-primary" type="button"><span class="glyphicon glyphicon-minus" aria-hidden="true"></span></button>
										</span>
	                                	<form:input cssClass="form-control js-update-quantity-input" disabled="${not entry.updateable}" type="text" 
	                                			size="1" id="quantity_${entry.entryNumber}" maxlength="10" path="quantity" />
										<input id="hiddenText" type="text" class="hidden" />
										<span class="input-group-btn">
											<button class="btn btn-default js-qty-plus btn-primary" type="button"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span></button>
										</span>
									</div>
									<c:if test="${isLowStock == entry.entryNumber}">
										<div id="lowStock${entry.entryNumber}" class="lowStock">
											<spring:theme code="basket.information.quantity.lowStock" arguments="${entry.quantity}"/>
										</div>
									</c:if>
									<c:if test="${isNoStock == entry.entryNumber}">
										<div id="lowStock${entry.entryNumber}" class="lowStock">
											<spring:theme code="basket.information.quantity.noStock"/>
										</div>
									</c:if>
	                            </ycommerce:testId>
	                        </form:form>
	                    </c:when>
	                    <c:otherwise>
	                        <c:url value="/cart/updateMultiD" var="cartUpdateMultiDFormAction" />
	                        <form:form id="updateCartForm${entry.entryNumber}" action="${cartUpdateMultiDFormAction}" method="post" class="js-qty-form${entry.entryNumber}" commandName="updateQuantityForm${entry.entryNumber}">
	                            <input type="hidden" name="entryNumber" value="${entry.entryNumber}"/>
	                            <input type="hidden" name="productCode" value="${entry.product.code}"/>
	                            <input type="hidden" name="initialQuantity" value="${entry.quantity}"/>
	                            <label class="visible-xs visible-sm"><spring:theme code="basket.page.qty"/>:</label>
	                            <span class="qtyValue"><c:out value="${entry.quantity}" /></span>
	                            <%--<input type="text" value="${entry.quantity}" class="form-control qtyValue" name="quantity" readonly>--%>
	                            <input type="hidden" name="quantity" value="0"/>
	                            <ycommerce:testId code="cart_product_updateQuantity">
	                                <div id="QuantityProduct${entry.entryNumber}" class="updateQuantityProduct"></div>
	                            </ycommerce:testId>
	                        </form:form>
	                    </c:otherwise>
	                </c:choose>
	            </div>

	            <%-- delivery --%>
	            <%-- <div class="item__delivery">
	                <c:if test="${entry.product.purchasable}">
	                    <c:if test="${not empty entryStock and entryStock ne 'outOfStock'}">
	                        <c:if test="${entry.deliveryPointOfService eq null or not entry.product.availableForPickup}">
	                            <span class="item__delivery--label"><spring:theme code="basket.page.shipping.ship"/></span>
	                        </c:if>
	                    </c:if>
	                    <c:if test="${not empty entry.deliveryPointOfService.name}">
	                        <span class="item__delivery--label"><spring:theme code="basket.page.shipping.pickup"/></span>
	                    </c:if>
	
	                    <c:if test="${entry.product.availableForPickup and not empty entry.deliveryPointOfService.name}">
	                        <div class="item__delivery--store">${entry.deliveryPointOfService.name}</div>
	                    </c:if>
	                </c:if>
	            </div> --%>

	            <%-- total --%>
	            <ycommerce:testId code="cart_totalProductPrice_label">
	                <div class="item__total js-item-total hidden-xs hidden-sm">
	                    <format:price priceData="${entry.totalPrice}" displayFreeForZero="true"/>
	                </div>
	            </ycommerce:testId>
	            
	            <%-- depositPaymentType --%>
	            <%-- <ycommerce:testId code="cart_totalProductPrice_label">
	                <div class="item__total js-item-total hidden-xs hidden-sm">
	                    <spring:theme code="basket.page.deposit${entry.depositPaymentType}"/>
	                </div>
	            </ycommerce:testId> --%>

	            <%-- remove icon --%>
	            <div class="item__remove  ">
	                <c:if test="${entry.updateable}" >
	                    <ycommerce:testId code="cart_product_removeProduct">
	                        <c:choose>
	                            <c:when test="${not entry.product.multidimensional}" >
	                                <button class="btn js-remove-entry-button" id="removeEntry_${entry.entryNumber}"
	                                		data-popup-title="<spring:theme code="basket.page.message.remove.confirm" />">
	                                    <span class="glyphicon glyphicon-remove"></span>
	                                </button>
	                            </c:when>
	                            <c:otherwise>
	                                <button class="btn js-submit-remove-product-multi-d" data-index="${entry.entryNumber}"  id="removeEntry_${entry.entryNumber}">
	                                    <span class="glyphicon glyphicon-remove"></span>
	                                </button>
	                            </c:otherwise>
	                        </c:choose>
	                    </ycommerce:testId>
	                </c:if>
	             
	            </div>
	               
        	</li>
      
	        <li>
	        	<spring:url value="/cart/getProductVariantMatrix" var="targetUrl"/>
				<grid:gridWrapper entry="${entry}" index="${entry.entryNumber}" styleClass="add-to-cart-order-form-wrap display-none" 
					targetUrl="${targetUrl}"/>
			</li>
			
			<div class="display-none">
		       	 <div id="popup_confirm_address_removal_${entry.entryNumber}" class="account-address-removal-popup">
		        	<div class="addressItem">
		       			<spring:theme code="basket.page.remove.following" />
	       				<c:url value="${entry.product.url}" var="entryProductUrl"/>
						<div class="thumb">
							<a href="${entryProductUrl}">
								<product:productPrimaryImage product="${entry.product}" format="cartIcon"/>
							</a>
						</div>
						<div class="details">
							<a class="name" href="${entryProductUrl}">${entry.product.name}</a>
							<div class="qty"><spring:theme code="popup.cart.quantity"/>: ${entry.quantity}</div>
							<c:forEach items="${entry.product.baseOptions}" var="baseOptions">
								<c:forEach items="${baseOptions.selected.variantOptionQualifiers}" var="baseOptionQualifier">
									<c:if test="${baseOptionQualifier.qualifier eq 'style' and not empty baseOptionQualifier.image.url}">
										<div class="itemColor">
											<span class="label"><spring:theme code="product.variants.colour"/></span>
											<img src="${baseOptionQualifier.image.url}" alt="${baseOptionQualifier.value}" title="${baseOptionQualifier.value}"/>
										</div>
									</c:if>
									<c:if test="${baseOptionQualifier.qualifier eq 'size'}">
										<div class="itemSize">
											<span class="label"><spring:theme code="product.variants.size"/></span>
												${baseOptionQualifier.value}
										</div>
									</c:if>
								</c:forEach>
							</c:forEach>
							<c:if test="${not empty entry.deliveryPointOfService.name}">
								<div class="itemPickup"><span class="itemPickupLabel"><spring:theme code="popup.cart.pickup"/></span>&nbsp;${entry.deliveryPointOfService.name}</div>
							</c:if>
						</div>
						<div class="price"><spring:theme code="popup.cart.unitPrice"/>: <format:price priceData="${entry.basePrice}"/></div>
						<div class="price"><spring:theme code="popup.cart.total"/>: <format:price priceData="${entry.totalPrice}"/></div>

				        <div class="modal-actions">
                               <div class="row">
                                   <ycommerce:testId code="addressRemove_delete_button">
                                       <div class="col-xs-6 col-sm-6 col-sm-push-6">
                                           <a class="btn btn-primary btn-block js-remove-entry-button-confirm" id="removeEntry_${entry.entryNumber}" >
                                               <spring:theme code="text.address.delete" />
                                           </a>
                                       </div>
                                   </ycommerce:testId>
                                   <div class="col-xs-6 col-sm-6 col-sm-pull-6">
                                       <a class="btn btn-default btn-block closeColorBox" data-address-id="${address.id}">
                                           <spring:theme code="text.button.cancel"/>
                                       </a>
                                   </div>
				       	    	</div>
				       		</div>
	        			</div>
		        	</div>
		        </div>
    		</c:forEach>
		</ul>
	</div>
</c:forEach>
</div>

