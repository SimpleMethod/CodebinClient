package pl.simplemethod.codebin.paypal;

import com.paypal.api.payments.Agreement;
import com.paypal.api.payments.Plan;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Autowired;
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

    private PayPalBillingPlan payPalBillingPlan;
    private PayPalBillingAgreement payPalBillingAgreement;

    private static String planId;
    private static final String PAYMENT_ACCEPT_URL = "http://127.0.0.1/dashboard.html#!/payment-accept";

    @Autowired
    public PayPalController(PayPalBillingPlan payPalBillingPlan, PayPalBillingAgreement payPalBillingAgreement,
                            UsersRepository usersRepository) {
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
}
