package com.medibook.payment.controller;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/payments/razorpay")
public class RazorpayController {

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    // Create Razorpay order
    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> request) {
        try {
            RazorpayClient client = new RazorpayClient(keyId, keySecret);

            JSONObject orderRequest = new JSONObject();
            // Amount in paise (multiply by 100)
            int amount = (int)(Double.parseDouble(request.get("amount").toString()) * 100);
            orderRequest.put("amount", amount);
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "receipt_" + request.get("appointmentId"));

            Order order = client.orders.create(orderRequest);

            Map<String, String> response = new HashMap<>();
            response.put("orderId", order.get("id"));
            response.put("amount", String.valueOf(amount));
            response.put("currency", "INR");
            response.put("keyId", keyId);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to create order: " + e.getMessage());
        }
    }

    // Verify payment after completion
    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, String> request) {
        try {
            String razorpayOrderId    = request.get("razorpayOrderId");
            String razorpayPaymentId  = request.get("razorpayPaymentId");
            String razorpaySignature  = request.get("razorpaySignature");

            // Verify signature
            String data = razorpayOrderId + "|" + razorpayPaymentId;
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
            mac.init(new javax.crypto.spec.SecretKeySpec(
                keySecret.getBytes(), "HmacSHA256"));
            byte[] hash = mac.doFinal(data.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            String generatedSignature = hexString.toString();

            if (generatedSignature.equals(razorpaySignature)) {
                Map<String, String> response = new HashMap<>();
                response.put("status", "success");
                response.put("paymentId", razorpayPaymentId);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(Map.of("status", "failed"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Verification failed: " + e.getMessage());
        }
    }
}