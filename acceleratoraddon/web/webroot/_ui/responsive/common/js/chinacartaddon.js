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
 ACC.chinacartitem = {
		submitTriggered: false,
		
		timeoutID: 0,
		
		input: null,
	bindAll: function () {
		hideCartMsg();
		adjustCheckboxes();
		setCheckoutBtnEnablement();
		$('.submitRemoveProduct').on("click", function () {
			var entryNumber = $(this).prop('id').split("_");
			anguoColorBoxConfirm("是否确认删除信息", function() {
				var $form = $('#updateCartForm' + entryNumber[1]);
				var productCode = $form.find('input[name=productCode]').val(); 
				var initialCartQuantity = $form.find('input[name=initialQuantity]');
				var cartQuantity = $form.find('input[name=quantity]');			
				var cartData = $form.data("cart");
				ACC.track.trackRemoveFromCart(productCode, initialCartQuantity.val(), cartData);
				
				cartQuantity.val(0);
				initialCartQuantity.val(0);
				$form.submit();
			}, function() {});
		});
		
		$('.submitRemoveInvalidProducts').on('click', function(){
			anguoColorBoxConfirm("是否确认删除所有失效商品", function() {//TODO:i18n
				$('#removeinvalidForm').submit();
			}, function() {});
		});
		
		$('.updateQuantityProductMinus, .updateQuantityProductPlus').on('click', function(){
			//loading of the cat which changes the number
			var htmlMask = '<div class="popup_bg"></div>';
			var $this = $(this).parents('.commodity');
			$this.append(htmlMask);
			var entryNumber = $(this).prop('id').split("_")
			var $form = $('#updateCartForm' + entryNumber[1]);
			var productCode = $form.find('input[name=productCode]').val(); 
			var initialCartQuantity = parseInt($form.find('input[name=initialQuantity]').val());
			var coefficient = parseInt($(this).data('coefficient'));//+:1, -:-1
			var newCartQuantity = initialCartQuantity  + coefficient;
			var cartData = $form.data("cart");
			
			if(newCartQuantity == 0){//update to 0 means delete
				ACC.track.trackRemoveFromCart(productCode, initialCartQuantity, cartData);
			}else{
				ACC.track.trackUpdateCart(productCode, initialCartQuantity, newCartQuantity, cartData);
			}
			$form.find('input[name="quantity"]').val(newCartQuantity);
			$form.submit();
		});
		
		$("input[id^='quantity']").on('blur',function(){
			var entryNumber = $(this).prop('id').substring(8);
			var $form = $('#updateCartForm' + entryNumber);
			var productCode = $form.find('input[name=productCode]').val(); 
			var initialCartQuantity = $form.find('input[name=initialQuantity]').val();
			var newCartQuantity = $(this).val();

			var reg =  /^[1-9]\d*$/;
			if(reg.test(newCartQuantity) == false){
				anguoColorBox("请使用正整数更新商品数量.");//TODO: i18n?
				newCartQuantity = initialCartQuantity;
				$(this).val(initialCartQuantity);
			}
			var cartData = $form.data("cart");
			if(newCartQuantity == 0){//update to 0 means delete
				ACC.track.trackRemoveFromCart(productCode, initialCartQuantity.val(), cartData);
				$form.submit();
			}else if(initialCartQuantity != newCartQuantity){
				ACC.track.trackUpdateCart(productCode, initialCartQuantity, newCartQuantity,cartData);
				$form.submit();
			}	
		});
		
		$('.submitRemoveSelectedProduct').on("click", function () {
			var entryNumbers = $("input[id^='select_']:checked").map(function(){
				return $(this).data('entrynumber');
			}).get();
			if (entryNumbers.length <= 0) {
				anguoColorBox("请选择商品");
			} else {
				anguoColorBoxConfirm("是否确认删除信息", function() {
					$("#selectedProductCodes").val(entryNumbers);
					$('#removeSelectedForm').submit();
				}, function() {});
			}
		});
		$('.miniCartubmitRemoveProduct').on("click", function () {
			var entryNumber= $('#entryNumber').val();
			$.ajax({
				url: "/cart/miniCart/update",
				dataType: "text",
				data: {
				entryNumber:entryNumber
				},
				type: 'POST',
				success: function (data) {
					window.location.reload();
				}
			});
		});
		
		//select entry level
		$("input[type=checkbox][id^='select_']").on('click', function() {
			adjustCheckboxes();
			var entryNumber = $(this).data('entrynumber');
			var entryNumbers = [];
			entryNumbers.push(entryNumber);
			ACC.chinacartitem.updateEntryStatus(entryNumbers, this.checked);
			setCheckoutBtnEnablement();
		});
		
		//select store level
		$("input[id^='selectAll_']").on('click', function() {
			$effectedEntries = $(this).closest('div').next().find('input[type="checkbox"]:not(:disabled)');
			$effectedEntries.prop('checked', this.checked);
			$('#selectAllTop, #selectAllBottom').prop("checked", $("[id^='selectAll_']:not(:checked)").length == 0);
			var entryNumbers = $effectedEntries.map(function(){
				return $(this).data('entrynumber');
			}).get();
			ACC.chinacartitem.updateEntryStatus(entryNumbers, this.checked);
			setCheckoutBtnEnablement();
		});
		
		//select all
		$('#selectAllTop, #selectAllBottom').on('click', function() {
			$effectedEntries = $('input[type="checkbox"][id^="select_"]:not(:disabled)');
			$effectedEntries.prop('checked', this.checked);		
			$('input[type="checkbox"][id^="selectAll_"]:not(:disabled)').prop('checked', this.checked);
			$('#selectAllTop, #selectAllBottom').prop('checked', this.checked);
			var entryNumbers = $effectedEntries.map(function(){
				return $(this).data('entrynumber');
			}).get();
			ACC.chinacartitem.updateEntryStatus(entryNumbers, this.checked);
			setCheckoutBtnEnablement();
		});		
	},
	
	bindUpdateQuantities : function(){
//		$('form[id^="updateCartForm"], form[id="removeSelectedForm"], form[id="removeinvalidForm"]').each(function(){
//			var options = {
//				type: 'POST',
//				success: function (data)
//				{
//					$('.popup_bg').remove();
//					$('#cartItems').remove();
//					$footer = $('.browse_footprint');
//					$('.alert').remove();
//					$(data).insertBefore($footer);
//					ACC.chinacartitem.bindAll();
//					ACC.chinacartitem.bindUpdateQuantities();
//					setCheckoutBtnEnablement();
//				},
//				error: function (xht, textStatus, ex)
//				{
//					$('.popup_bg').remove();
//					anguoColorBox("操作失败");
//				}
//			};
//			$(this).ajaxForm(options);
//		});
	},
	
	updateEntryStatus : function(entryNumbers, status){
		$.ajax({
			url: "cart/updateEntriesStatus/" + status,
			type: 'POST',
			dataType: 'json',
			contentType: 'application/json',
			async : false,
			cache : false,
			data : JSON.stringify(entryNumbers),
			success: function (data) {
				ACC.chinacartitem.updateTotalDatas(data);
			},
			error: function() {
				anguoColorBox("操作失败!");
			}
		});
	},
	
	updateTotalDatas : function(data){
		if(ACC.chinacartitem.validateCallbackData(data)){							
			$('#typeCount').text(data.selectedTotalSellersCount);
			$('#totalUnitCount').text(data.selectedTotalItemsCount);
//			$('#cartTotalPrice').text(data.selectedTotalPrice.formattedValue);
//			$('#cartTotalPrice').text(data.selectedFirstPaymentPrice.formattedValue);
			
			$('.cart-totals-right, .cart-top-totals-amount').text(data.selectedTotalPrice.formattedValue);
//			$('.cart-first-right').text(data.selectedFirstPaymentPrice.formattedValue);
		}		
	},
	
	validateCallbackData: function(cartData){
		return cartData;//TODO: add more validation here
	},
	
	bindCartItem: function ()
	{
		$(document).on("click", '.js-qty-selector .js-qty-minus', function () {
            ACC.chinacartitem.checkQtySelector(this, "minus");
        });

        $(document).on("click", '.js-qty-selector .js-qty-plus', function () {
			ACC.chinacartitem.checkQtySelector(this, "plus");
        });
        
        $('.js-qty-selector .js-update-quantity-input').on("keyup", function (e)
		{
			ACC.chinacartitem.input = this;
			var reg =  /^[1-9]\d*$/;
			if (reg.test($(this).val()) == false) {
				$(this).val($(this).parents('form[id^=updateCartForm]').find('input[name=initialQuantity]').val());
			}
			else {
				clearTimeout(ACC.chinacartitem.timeoutID);
				ACC.chinacartitem.timeoutID = setTimeout(function() {
					ACC.chinacartitem.checkQtySelector(ACC.chinacartitem.input, "input");
				}, 300);
			}
		});
        
        $('.js-remove-entry-button').on("click", function ()
		{
			var entryNumber = $(this).attr('id').split("_");
			var popupTitle = $(this).attr('data-popup-title');
			
			ACC.colorbox.open(popupTitle,{
				inline: true,
				height: false,
				href: "#popup_confirm_address_removal_" + entryNumber[1],
				onComplete: function ()
				{
					$(this).colorbox.resize();
				}
			});
		});
		
		$('.js-remove-entry-button-confirm').on("click", function ()
		{
			var entryNumber = $(this).attr('id').split("_")
			var form = $('#updateCartForm' + entryNumber[1]);

			var productCode = form.find('input[name=productCode]').val();
			var initialCartQuantity = form.find('input[name=initialQuantity]');
			var cartQuantity = form.find('input[name=quantity]');

			ACC.track.trackRemoveFromCart(productCode, initialCartQuantity.val());

			cartQuantity.val(0);
			initialCartQuantity.val(0);
			form.submit();
		});
		
	},

	gvgHandleUpdateQuantity: function (input)
	{
		var entryNumber = input.closest('form').find('input[name=entryNumber]').val();
		
		$(input.closest('form')).each(function ()
		{
			var options = {
				type: 'POST',
				success: function (data)
				{
					// replace cartItems
					$('#cartItems').remove();
					$footer = $('label[id="footer"]');
					$(data).insertBefore($footer);
					
					// low Stock info
					var retrunCartQuantity = $('#quantity_' + entryNumber).val();
//					if (parseInt(input.val()) > parseInt(retrunCartQuantity))
//					{
//						$('#lowStock' + entryNumber).removeClass("hidden");
//					}
					
					// add listeners
					$('.js-qty-selector .js-update-quantity-input').on("keyup", function (e)
					{
						ACC.chinacartitem.input = this;
						var reg =  /^[1-9]\d*$/;
						if (reg.test($(this).val()) == false) {
							$(this).val($(this).parents('form[id^=updateCartForm]').find('input[name=initialQuantity]').val());
						}
						else {
							clearTimeout(ACC.chinacartitem.timeoutID);
							ACC.chinacartitem.timeoutID = setTimeout(function() {
								ACC.chinacartitem.checkQtySelector(ACC.chinacartitem.input, "input");
							}, 300);
						}
					});
			        
			        $('.js-remove-entry-button').on("click", function ()
					{
						var entryNumber = $(this).attr('id').split("_");
						var popupTitle = $(this).attr('data-popup-title');
						
						ACC.colorbox.open(popupTitle,{
							inline: true,
							height: false,
							href: "#popup_confirm_address_removal_" + entryNumber[1],
							onComplete: function ()
							{
								$(this).colorbox.resize();
							}
						});
					});
					
					$('.js-remove-entry-button-confirm').on("click", function ()
					{
						var entryNumber = $(this).attr('id').split("_")
						var form = $('#updateCartForm' + entryNumber[1]);

						var productCode = form.find('input[name=productCode]').val();
						var initialCartQuantity = form.find('input[name=initialQuantity]');
						var cartQuantity = form.find('input[name=quantity]');

						ACC.track.trackRemoveFromCart(productCode, initialCartQuantity.val());

						cartQuantity.val(0);
						initialCartQuantity.val(0);
						form.submit();
					});
					
					// set focus for input
					$('#quantity_' + entryNumber).focus();
					$('#quantity_' + entryNumber).val(retrunCartQuantity);
					
					// add checkout status
					ACC.chinacartitem.bindAll();
					$enableEntries = $('input[type="checkbox"][id^="select_"]:checked');
					var entryNumbers = $enableEntries.map(function(){
						return $(this).data('entrynumber');
					}).get();
					ACC.chinacartitem.updateEntryStatus(entryNumbers, "true");
				}
			};
			$(this).ajaxSubmit(options);
		});
	},
	
	checkQtySelector: function (self, mode) {
        var input = $(self).parents(".js-qty-selector").find(".js-update-quantity-input");
        var inputVal = parseInt(input.val());
        var minusBtn = $(self).parents(".js-qty-selector").find(".js-qty-minus");
        var plusBtn = $(self).parents(".js-qty-selector").find(".js-qty-plus");

        $('.js-qty-selector').find('.btn').removeAttr("disabled");

        if (mode == "minus") {
            if (inputVal != 1) {
                ACC.chinacartitem.updateQtyValue(input, inputVal - 1)
            } else {
            	var entryNumber = input.attr('id').split("_");
				var popupTitle = $('#removeEntry_' + entryNumber[1]).attr('data-popup-title');
				
				ACC.colorbox.open(popupTitle,{
					inline: true,
					height: false,
					href: "#popup_confirm_address_removal_" + entryNumber[1],
					onComplete: function ()
					{
						$(this).colorbox.resize();
					}
				});
            }
        } else if (mode == "input") {
        	if (input.val() == null || parseInt(input.val()) < 1 || input.val() == "")
			{
				ACC.chinacartitem.updateQtyValue(input, 1);
			}
        	else {
				ACC.chinacartitem.gvgHandleUpdateQuantity(input);
			}
        } else if (mode == "plus") {
    		ACC.chinacartitem.updateQtyValue(input, inputVal + 1)
        } 
    },

    updateQtyValue: function (input, value) {
        input.val(value);
        ACC.chinacartitem.gvgHandleUpdateQuantity(input);
    }
}

function setCheckoutBtnEnablement() {
	$("<div style='display: none;'>" + $("#RECV_ORD_ID").val() + "</div>").insertBefore("#header");
	var cnt = 0;
	$("[id^='select_']").each(function() {
		if (this.checked) {
			cnt++;
		}
	});
	if (cnt > 0) {
		$('.btn--continue-checkout').each(function() {
//			$("#checkoutButton").each(function() {
			$(this).removeAttr("disabled");
//			$(this).css("background-color", "#ff5001");
			$(this).addClass("doCheckoutBut");
		});
//		ACC.checkout.bindAll();
	} else {
		$('.btn--continue-checkout').each(function() {
			$(this).prop("disabled", "disabled");
//			$(this).css("background-color", "#C7C7C7");
			$(this).removeClass("doCheckoutBut");
		});
	}
}

function hideCartMsg() {
	$(".alertCart").delay(2000).fadeOut("slow");
}

function adjustCheckboxes() {
	$("input[id^='selectAll_']").each(function(){
		//如果分区中的商品全部被disabled，则不选择分区级别checkbox，并且disable掉分区级别checkbox
		if($(this).closest('div').next().find('input[type="checkbox"]:not(:disabled)').length > 0){
			$(this).prop('checked',$(this).closest('div').next().find('input[type="checkbox"]:not(:disabled):not(:checked)').length == 0);			
			$('#selectAllTop, #selectAllBottom').prop('checked', $('input[type="checkbox"][id^="selectAll_"]:not(:checked)').length == 0);
		}else {
			$(this).attr("disabled", "disabled");
		}
	})
}
$(document).ready(function () {
	ACC.chinacartitem.bindAll();
	ACC.chinacartitem.bindUpdateQuantities();
	ACC.chinacartitem.bindCartItem();

	$('form[id^="updateCartForm"]').on('keyup keypress', function(e) {
		var keyCode = e.keyCode || e.which;
		if (keyCode === 13) { 
			e.preventDefault();
			return false;
		}
	});
});

