package com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.integrations.payment.stripe;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@CrossOrigin("*")
@RequestMapping("/api/stripe")
@RestController
public class StripeController {

    private final StripeService stripeService;

    public StripeController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    @Value("${stripeApiKey}")
    private String stripeApiKey;

    @Value("${stripe.subscriptionPriceId}")
    private String subscriptionPriceId;

    @GetMapping("/{loggedInUser}/checkoutsession")
    public String createCheckoutSession(@PathVariable String loggedInUser) {
        try {
            return stripeService.createCheckoutSession(loggedInUser);
        } catch (Exception e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    @GetMapping("/success")
    public RedirectView handleSubscriptionSuccess(@RequestParam("session_id") String sessionId) {
        try {
            return stripeService.handleSubscriptionSuccess(sessionId);
        } catch (Exception e) {
            return new RedirectView(
                    "https://lobster-app-ebdey.ondigitalocean.app?page=error&message=" + e.getMessage());
        }
    }
}