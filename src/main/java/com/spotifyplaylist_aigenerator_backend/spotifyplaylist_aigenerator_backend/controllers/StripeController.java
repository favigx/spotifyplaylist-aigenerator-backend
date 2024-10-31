package com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.models.User;
import com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.services.UserService;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

@CrossOrigin(origins = "*")
@RestController
public class StripeController {

    @Autowired
    UserService userService;

    @Value("${stripeApiKey}")
    private String stripeApiKey;

    @Value("${stripe.subscriptionPriceId}")
    private String subscriptionPriceId;

    @PostMapping("/stripecheckoutsession/{loggedInUser}")
    public ResponseEntity<?> createCheckoutSession(@PathVariable String loggedInUser) {
        Stripe.apiKey = stripeApiKey;

        try {
            SessionCreateParams params = SessionCreateParams.builder()
                    .setSuccessUrl(
                            "https://shark-app-j7qxa.ondigitalocean.app/success?session_id={CHECKOUT_SESSION_ID}")

                    .setCancelUrl("https://lobster-app-ebdey.ondigitalocean.app/?page=stripepaymentlink")
                    .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                    .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                    .addLineItem(SessionCreateParams.LineItem.builder()
                            .setQuantity(1L)
                            .setPrice(subscriptionPriceId)
                            .build())
                    .putMetadata("username", loggedInUser)
                    .build();

            Session session = Session.create(params);
            return ResponseEntity.ok(session.getUrl());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Ett fel uppstod: " + e.getMessage());
        }
    }

    @GetMapping("/success")
    public RedirectView handleSubscriptionSuccess(@RequestParam("session_id") String sessionId) {
        Stripe.apiKey = stripeApiKey;

        try {
            Session session = Session.retrieve(sessionId);

            String username = session.getMetadata().get("username");

            if (session.getPaymentStatus().equals("paid")) {
                User existingUser = userService.getUserByUsername(username);

                if (existingUser != null) {
                    existingUser.setPremium(true);
                    userService.updateUser(existingUser);

                    RedirectView redirectView = new RedirectView();
                    String redirectUrl = "https://lobster-app-ebdey.ondigitalocean.app/?page=generateplaylist";
                    redirectView.setUrl(redirectUrl);
                    return redirectView;
                } else {
                    return new RedirectView(
                            "https://lobster-app-ebdey.ondigitalocean.app?page=error&message=Användare+hittades+inte");
                }
            } else {
                return new RedirectView(
                        "https://lobster-app-ebdey.ondigitalocean.app?page=error&message=Betalning+misslyckades");
            }
        } catch (Exception e) {
            return new RedirectView(
                    "https://lobster-app-ebdey.ondigitalocean.app?page=error&message=" + e.getMessage());
        }
    }
}