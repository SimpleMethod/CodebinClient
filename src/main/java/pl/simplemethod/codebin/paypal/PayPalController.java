package pl.simplemethod.codebin.paypal;

import com.paypal.api.payments.Agreement;
import com.paypal.api.payments.Plan;
import com.paypal.base.rest.PayPalRESTException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

@RestController
@RequestMapping("paypal")
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
     * Redirects response to returned URL
     */
    @GetMapping("/subscribe")
    public @ResponseBody ResponseEntity subscribe() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            Plan plan = payPalBillingPlan.create();
            Agreement agreement = payPalBillingAgreement.define(plan.getId());

            JSONObject body = new JSONObject();
            body.put("url", payPalBillingAgreement.create(agreement));
            return new ResponseEntity<>(body.toString(), headers, HttpStatus.valueOf(200));
        } catch (PayPalRESTException e) {
            System.err.println(e.getDetails());
        } catch (MalformedURLException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return new ResponseEntity<>("", headers, HttpStatus.BAD_REQUEST);
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
