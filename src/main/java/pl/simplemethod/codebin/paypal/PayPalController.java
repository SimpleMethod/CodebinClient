package pl.simplemethod.codebin.paypal;

import com.paypal.api.payments.Agreement;
import com.paypal.api.payments.Plan;
import com.paypal.base.Constants;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.simplemethod.codebin.model.Users;
import pl.simplemethod.codebin.repository.UsersRepository;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

@RestController
@RequestMapping("paypal")
public class PayPalController {

    private UsersRepository usersRepository;

    private APIContext apiContext;
    private PayPalBillingPlan payPalBillingPlan;
    private PayPalBillingAgreement payPalBillingAgreement;

    private static final String WEBHOOK_ID = "5D129190NJ603512E";

    private static String planId;
    private static final String PAYMENT_ACCEPT_URL = "https://145.239.31.229/dashboard.html#!/payment-accept";

    @Autowired
    public PayPalController(APIContext apiContext, PayPalBillingPlan payPalBillingPlan,
                            PayPalBillingAgreement payPalBillingAgreement, UsersRepository usersRepository) {
        this.apiContext = apiContext;
        this.payPalBillingPlan = payPalBillingPlan;
        this.payPalBillingAgreement = payPalBillingAgreement;
        this.usersRepository = usersRepository;
    }

    /**
     * Define and create billing plan and billing agreement.
     * Redirects response to returned URL
     */
    @GetMapping(path = "/subscribe")
    public void subscribe(HttpServletResponse response) {
        try {
            Plan plan = payPalBillingPlan.create();
            Agreement agreement = payPalBillingAgreement.define(plan.getId());
            planId = plan.getId();

            response.sendRedirect(payPalBillingAgreement.create(agreement));
        } catch (PayPalRESTException e) {
            System.err.println(e.getDetails());
        } catch (MalformedURLException | UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Executes successfully created billing agreement
     * @param token Token which is automatically assigned to billing agreement
     */
    @GetMapping("/payment-success")
    public void success(HttpServletResponse response, @RequestParam String token, @CookieValue("id") Integer id) {
        try {
            payPalBillingAgreement.execute(token);

            Users users = usersRepository.getFirstById(id);
            users.setSubscription(planId);
            usersRepository.save(users);
        } catch (PayPalRESTException e) {
            System.err.println(e.getDetails());
        }

        try {
            response.sendRedirect(PAYMENT_ACCEPT_URL);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostMapping(path = "/webhook", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity getInfo(@RequestBody String payload) {
        apiContext.addConfiguration(Constants.PAYPAL_WEBHOOK_ID, WEBHOOK_ID);

        JSONObject jsonObject = new JSONObject(payload);
        if (jsonObject.getString("event_type").equals("BILLING.SUBSCRIPTION.CANCELLED")) {
            JSONObject resource = (JSONObject) jsonObject.get("resource");
            usersRepository.updatePlan(resource.getString("plan_id"));
            System.err.println(resource.getString("plan_id") + " - subscription cancelled");
        } else {
            return new ResponseEntity<>("", null, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("", null, HttpStatus.OK);
    }
}
