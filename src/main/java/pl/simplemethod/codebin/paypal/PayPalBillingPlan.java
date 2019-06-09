package pl.simplemethod.codebin.paypal;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PayPalBillingPlan {

    private APIContext apiContext;

    @Autowired
    public PayPalBillingPlan(APIContext apiContext) {
        this.apiContext = apiContext;
    }

    private static final String CANCEL_URL = "https://145.239.31.229/dashboard.html#!/payment-error";
    private static final String PROCESS_URL = "https://145.239.31.229/paypal/payment-success";

    /**
     * Creates a plan to billing plan
     * @return Created billing plan
     * @throws PayPalRESTException When some parameters are wrong
     */
    public Plan create() throws PayPalRESTException {
        Plan plan = new Plan();
        plan.setName("Codebin Premium");
        plan.setDescription("Codebin premium plan");
        plan.setType("fixed");

        PaymentDefinition paymentDefinition = new PaymentDefinition();
        paymentDefinition.setName("Codebin Subscription");
        paymentDefinition.setType("REGULAR");
        paymentDefinition.setFrequency("MONTH");
        paymentDefinition.setFrequencyInterval("1");
        paymentDefinition.setCycles("12");

        Currency currency = new Currency();
        currency.setCurrency("USD");
        currency.setValue("9.95");
        paymentDefinition.setAmount(currency);

        ChargeModels chargeModels = new ChargeModels();
        chargeModels.setType("TAX");
        chargeModels.setAmount(currency);
        List<ChargeModels> chargeModelsList = new ArrayList<>();
        chargeModelsList.add(chargeModels);
        paymentDefinition.setChargeModels(chargeModelsList);

        List<PaymentDefinition> paymentDefinitions = new ArrayList<>();
        paymentDefinitions.add(paymentDefinition);
        plan.setPaymentDefinitions(paymentDefinitions);

        MerchantPreferences merchantPreferences = new MerchantPreferences();
        merchantPreferences.setSetupFee(currency);
        merchantPreferences.setCancelUrl(CANCEL_URL);
        merchantPreferences.setReturnUrl(PROCESS_URL);
        merchantPreferences.setMaxFailAttempts("0");
        merchantPreferences.setAutoBillAmount("YES");
        merchantPreferences.setInitialFailAmountAction("CONTINUE");
        plan.setMerchantPreferences(merchantPreferences);

        Plan createdPlan = plan.create(apiContext);

        List<Patch> patchRequests = new ArrayList<>();
        Map<String, String> values = new HashMap<>();
        values.put("state", "ACTIVE");

        Patch patch = new Patch();
        patch.setPath("/");
        patch.setValue(values);
        patch.setOp("replace");
        patchRequests.add(patch);

        createdPlan.update(apiContext, patchRequests);

        return createdPlan;
    }
}
