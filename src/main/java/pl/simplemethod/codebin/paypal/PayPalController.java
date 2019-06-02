package pl.simplemethod.codebin.paypal;

import com.paypal.api.payments.Agreement;
import com.paypal.api.payments.Plan;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

@RestController
public class PayPalController {

    private PayPalBillingPlan payPalBillingPlan;
    private PayPalBillingAgreement payPalBillingAgreement;

    @Autowired
    public PayPalController(PayPalBillingPlan payPalBillingPlan, PayPalBillingAgreement payPalBillingAgreement) {
        this.payPalBillingPlan = payPalBillingPlan;
        this.payPalBillingAgreement = payPalBillingAgreement;
    }

    /**
     * Define and create billing plan and billing agreement.
     * Redirects response to successful or unsuccessful URL
     */
    @GetMapping("/subscribe")
    public void subscribe() {
        try {
            Plan plan = payPalBillingPlan.create();
            Agreement agreement = payPalBillingAgreement.define(plan.getId());
            String redirect = payPalBillingAgreement.create(agreement);
            // TODO: 02/06/2019 REDIRECT to redirect url
        } catch (PayPalRESTException e) {
            System.err.println(e.getDetails());
        } catch (MalformedURLException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Executes successfully created billing agreement
     * @param token Token which is automatically assigned to billing agreement
     */
    @GetMapping("/subscribe-success")
    public void success(@RequestParam String token) {
        try {
            payPalBillingAgreement.execute(token);
        } catch (PayPalRESTException e) {
            System.err.println(e.getDetails());
        }
    }
}
