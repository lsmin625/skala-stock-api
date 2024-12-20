package com.sk.skala.stockapi.sagasample;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sk.skala.stockapi.config.Error;
import com.sk.skala.stockapi.data.common.Response;
import com.sk.skala.stockapi.exception.ResponseException;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/saga/sample")
public class SagaSampleController {

	private final OrderRepository orderRepository;
	private final InventoryRepository inventoryRepository;
	private final BillingRepository billingRepository;

	@PostMapping("/orders")
	public Response postOrder(@RequestBody Map<String, String> map) throws Exception {
		Response response = new Response();

		Long orderId = Long.valueOf(map.get("orderId"));
		if (orderId > 0) {
			Optional<Order> option = orderRepository.findById(orderId);
			if (option.isEmpty()) {
				throw new ResponseException(Error.DATA_NOT_FOUND);
			}
			response.setBody(orderRepository.save(option.get()));

		} else {
			Order order = new Order();
			order.setCustomerId(Long.valueOf(map.get("customerId")));
			order.setCustomerName(map.get("customerName"));
			order.setProductId(Long.valueOf(map.get("productId")));
			order.setProductName(map.get("productName"));

			order.setOrderStatus(Order.OrderStatus.CREATED);
			order.setCreatedDate(new Date());

			response.setBody(orderRepository.save(order));
		}
		return response;
	}

	@PostMapping("/orders/cancel")
	public Response cancelOrder(@RequestBody Map<String, String> map) throws Exception {
		Optional<Order> option = orderRepository.findById(Long.valueOf(map.get("orderId")));
		if (option.isEmpty()) {
			throw new ResponseException(Error.DATA_NOT_FOUND);
		}

		Order order = option.get();
		order.setOrderStatus(Order.OrderStatus.CANCELED);
		order.setUpdatedDate(new Date());

		Response response = new Response();
		response.setBody(orderRepository.save(order));
		return response;
	}

	@PostMapping("/inventory")
	public Response postInventory(@RequestBody Map<String, String> map) throws Exception {
		Long orderId = Long.valueOf(map.get("orderId"));
		if (orderId == 0) {
			throw new ResponseException(Error.INVALID_DATA_ID);
		}

		if (!Order.OrderStatus.CREATED.toString().equals(map.get("orderStatus"))) {
			throw new ResponseException(Error.INVALID_DATA_STATUS);
		}

		Inventory inventory = new Inventory();
		inventory.setOrderId(orderId);
		inventory.setOrderDescription("ordered by " + map.get("customerName"));
		inventory.setProductId(Long.valueOf(map.get("productId")));
		inventory.setProductName(map.get("productName"));

		inventory.setInventoryStatus(Inventory.InventoryStatus.RESERVED);
		inventory.setReservedDate(new Date());

		Response response = new Response();
		response.setBody(inventoryRepository.save(inventory));
		return response;
	}

	@PostMapping("/inventory/cancel")
	public Response cancelInventory(@RequestBody Map<String, String> map) throws Exception {
		Optional<Inventory> option = inventoryRepository.findById(Long.valueOf(map.get("inventoryId")));
		if (option.isEmpty()) {
			throw new ResponseException(Error.DATA_NOT_FOUND);
		}

		Inventory inventory = option.get();
		inventory.setInventoryStatus(Inventory.InventoryStatus.RELEASED);
		inventory.setReleasedDate(new Date());

		Response response = new Response();
		response.setBody(inventoryRepository.save(inventory));
		return response;
	}

	@PostMapping("/billing")
	public Response postBilling(@RequestBody Map<String, String> map) throws Exception {
		Long orderId = Long.valueOf(map.get("orderId"));
		if (orderId == 0) {
			throw new ResponseException(Error.INVALID_DATA_ID);
		}

		if (!Inventory.InventoryStatus.RESERVED.toString().equals(map.get("inventoryStatus"))) {
			throw new ResponseException(Error.INVALID_DATA_STATUS);
		}

		Billing billing = new Billing();
		billing.setOrderId(orderId);
		billing.setOrderDescription("ordered by " + map.get("customerName"));
		billing.setCustomerId(Long.valueOf(map.get("customerId")));
		billing.setCustomerName(map.get("customerName"));

		billing.setBillingStatus(Billing.BillingStatus.PENDING);
		billing.setCreatedDate(new Date());

		Response response = new Response();
		response.setBody(billingRepository.save(billing));
		return response;
	}
}
