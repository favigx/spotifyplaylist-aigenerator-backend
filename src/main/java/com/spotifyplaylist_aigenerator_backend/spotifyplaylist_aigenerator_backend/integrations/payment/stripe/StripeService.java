package com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.integrations.payment.stripe;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.RedirectView;

import com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.user.User;
import com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.user.UserService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Subscription;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

@Service
public class StripeService {

    @Value("${stripeApiKey}")
    private String stripeApiKey;

    @Value("${stripe.subscriptionPriceId}")
    private String subscriptionPriceId;

    private final UserService userService;

    public StripeService(UserService userService) {
        this.userService = userService;
    }

    public String createCheckoutSession(String loggedInUser) throws StripeException, IOException {
        Stripe.apiKey = stripeApiKey;

        User user = userService.getUserByUsername(loggedInUser);

        String stripeCustomerId = user.getStripeCustomerId();

        Map<String, String> subscriptionMetaData = new HashMap<>();
        subscriptionMetaData.put("loggedInUser", String.valueOf(loggedInUser));

        SessionCreateParams.Builder paramsBuilder = SessionCreateParams.builder()
                .setSuccessUrl(
                        "https://sea-turtle-app-le797.ondigitalocean.app/api/stripe/success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(
                        "https://lobster-app-ebdey.ondigitalocean.app/?page=stripepaymentlink/?page=stripepaymentlink")
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.LINK)
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPrice(subscriptionPriceId)
                        .build())
                .setAutomaticTax(
                        SessionCreateParams.AutomaticTax.builder()
                                .setEnabled(true)
                                .build())
                .putMetadata("loggedInUser", String.valueOf(loggedInUser))
                .setSubscriptionData(SessionCreateParams.SubscriptionData.builder()
                        .putAllMetadata(subscriptionMetaData)
                        .build())
                .setBillingAddressCollection(SessionCreateParams.BillingAddressCollection.REQUIRED);

        if (stripeCustomerId != null && !stripeCustomerId.isEmpty()) {
            paramsBuilder.setCustomer(stripeCustomerId);
        } else {
            paramsBuilder.setSubscriptionData(SessionCreateParams.SubscriptionData.builder()
                    .setTrialPeriodDays(7L)
                    .build());
        }

        Session session = Session.create(paramsBuilder.build());
        System.out.println(session.getId());
        return session.getUrl();
    }

    public RedirectView handleSubscriptionSuccess(String sessionId) throws StripeException {
        Session session = Session.retrieve(sessionId);
        String loggedInUser = session.getMetadata().get("loggedInUser");

        String subscriptionId = session.getSubscription();
        if (subscriptionId == null || subscriptionId.isEmpty()) {
            throw new IllegalArgumentException("Subscription ID is null or empty for session: " + sessionId);
        }

        Subscription subscription = Subscription.retrieve(subscriptionId);
        Customer customer = Customer.retrieve(subscription.getCustomer());
        System.out.println(customer);

        if (session.getPaymentStatus().equals("paid")) {
            User existingUser = userService.getUserByUsername(loggedInUser);

            if (existingUser != null) {
                existingUser.setPremium(true);
                userService.updateUser(existingUser);

                return new RedirectView("https://lobster-app-ebdey.ondigitalocean.app/?page=generateplaylist");
            } else {
                return new RedirectView(
                        "https://lobster-app-ebdey.ondigitalocean.app?page=error&message=Användare+hittades+inte");
            }
        } else {
            return new RedirectView(
                    "https://lobster-app-ebdey.ondigitalocean.app?page=error&message=Betalning+misslyckades");
        }
    }

}
